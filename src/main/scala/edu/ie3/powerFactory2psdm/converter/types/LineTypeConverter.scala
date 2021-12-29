/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter.types
import com.typesafe.scalalogging.LazyLogging
import edu.ie3.datamodel.models.input.connector.`type`.LineTypeInput
import edu.ie3.powerFactory2psdm.exception.pf.ConversionException
import edu.ie3.powerFactory2psdm.model.entity.{Line, LineSection}
import edu.ie3.powerFactory2psdm.model.entity.types.LineType
import edu.ie3.powerFactory2psdm.util.QuantityUtils.RichQuantityDouble

import java.util.UUID
import scala.math.abs
import scala.util.{Failure, Success, Try}

/** Functionality to translate a [[LineType]] to a [[LineTypeInput]]
  */
object LineTypeConverter extends LazyLogging {

  def convert(input: LineType): LineTypeInput = {

    new LineTypeInput(
      UUID.randomUUID(),
      input.id,
      input.b.asMicroSiemensPerKilometre,
      input.g.asMicroSiemensPerKilometre,
      input.r.asOhmPerKilometre,
      input.x.asOhmPerKilometre,
      input.iMax.asKiloAmpere,
      input.vRated.asKiloVolt
    )
  }

  /** In PowerFactory lines can be made up of line sections. We convert them to
    * a single line by using the aggregated length of the line sections and
    * generate a line type by calculating the weighted (by line length) average
    * of the parameters.
    *
    * @param lineId
    *   name of the line made up of line sections
    * @param lineLength
    *   the noted length of the line
    * @param lineSections
    *   a list of the line sections
    * @param lineTypes
    *   mapping of line types
    * @return
    *   the averaged line type as [[LineTypeInput]]
    */
  def convert(
      lineId: String,
      lineLength: Double,
      lineSections: List[LineSection],
      lineTypes: Map[String, LineTypeInput]
  ): LineTypeInput = {

    val aggregatedLineSectionLength =
      lineSections.map(section => section.length).sum

    // sanity check of total line length versus aggregated line length of all corresponding line sections
    lineLength - aggregatedLineSectionLength match {
      case x if abs(x) < 1e-9 =>
      case x if x < 0 =>
        logger.error(
          s"The line length of line: $lineId is smaller than the aggregated length of line sections by ${(1 - (lineLength / aggregatedLineSectionLength)) * 100}% which distorts results. This should be prevented by PF and therefore not happen."
        )
      case x if x > 0 =>
        logger.error(
          s"The line length of line: $lineId is greater than the aggregated length of line sections by ${((lineLength / aggregatedLineSectionLength) - 1) * 100}% which distorts results. This should be prevented by PF and therefore not happen."
        )
    }

    val weightedLineTypes = lineSections.map(section => {
      val lineType = getLineType(section.typeId, lineTypes)
        .getOrElse(
          throw ConversionException(
            s"Can't find line type ${section.typeId} of section ${section.id} within the converted line types."
          )
        )
      (section.length, lineType)
    })
    val emptyLineType = new LineTypeInput(
      UUID.randomUUID(),
      "Custom_line_type_" + lineId,
      0.asMicroSiemensPerKilometre,
      0.asMicroSiemensPerKilometre,
      0.asOhmPerKilometre,
      0.asOhmPerKilometre,
      Double.MaxValue.asKiloAmpere,
      Double.MaxValue.asKiloVolt
    )

    weightedLineTypes.foldLeft(emptyLineType)((averageType, current) => {
      val currentLine = current._2
      val weightingFactor = current._1 / lineLength
      new LineTypeInput(
        averageType.getUuid,
        averageType.getId,
        averageType.getB.add(currentLine.getB.multiply(weightingFactor)),
        averageType.getG.add(currentLine.getG.multiply(weightingFactor)),
        averageType.getR.add(currentLine.getR.multiply(weightingFactor)),
        averageType.getX.add(currentLine.getX.multiply(weightingFactor)),
        if (averageType.getiMax().isLessThan(currentLine.getiMax()))
          averageType.getiMax()
        else currentLine.getiMax(),
        if (averageType.getvRated().equals(Double.MaxValue.asKiloVolt))
          currentLine.getvRated()
        else averageType.getvRated()
      )
    })
  }

  def getLineType(
      id: String,
      lineTypes: Map[String, LineTypeInput]
  ): Try[LineTypeInput] = {
    lineTypes
      .get(id)
      .map(Success(_))
      .getOrElse(
        Failure(
          ConversionException(
            s"Can't find line type $id within the converted line types."
          )
        )
      )
  }
}
