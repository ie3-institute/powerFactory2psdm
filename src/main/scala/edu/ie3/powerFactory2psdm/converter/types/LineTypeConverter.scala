/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter.types
import edu.ie3.datamodel.models.input.connector.`type`.LineTypeInput
import edu.ie3.powerFactory2psdm.exception.pf.ConversionException
import edu.ie3.powerFactory2psdm.model.entity.LineSection
import edu.ie3.powerFactory2psdm.model.entity.types.LineType
import edu.ie3.powerFactory2psdm.util.QuantityUtils.RichQuantityDouble
import edu.ie3.util.quantities.interfaces.SpecificConductance
import tech.units.indriya.ComparableQuantity
import tech.units.indriya.quantity.Quantities

import java.util.UUID
import javax.measure.quantity.{ElectricCurrent, ElectricPotential}
import scala.math.{abs, min}
import scala.util.{Failure, Success, Try}

/** Functionality to translate a [[LineType]] to a [[LineTypeInput]]
  */
object LineTypeConverter {

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

  def convert(
      lineId: String,
      lineLength: Double,
      lineSections: List[LineSection],
      lineTypes: Map[String, LineTypeInput]
  ): LineTypeInput = {

    val aggregatedLineSectionLength =
      lineSections.map(section => section.length).sum

    val totalLength = lineLength - aggregatedLineSectionLength match {
      case x if abs(x) < 1e-3 => lineLength
      case x if x < 0         => aggregatedLineSectionLength
      case x if x > 0         => lineLength
    }

    if (abs(lineLength - aggregatedLineSectionLength) < 1e-3) {
      val weightedLineTypes = lineSections.map(section => {
        val lineType = getLineType(section.typeId, lineTypes)
          .getOrElse(
            throw ConversionException(
              s"Can't find line type ${section.typeId} of section ${section.id} within the converted line types."
            )
          )
        (section.length, lineType)
      })
      val lineType = new LineTypeInput(
        UUID.randomUUID(),
        "Custom_line_type_" + lineId,
        0.asMicroSiemensPerKilometre,
        0.asMicroSiemensPerKilometre,
        0.asOhmPerKilometre,
        0.asOhmPerKilometre,
        Double.MaxValue.asKiloAmpere,
        Double.MaxValue.asKiloVolt
      )

      val weightedLineType =
        weightedLineTypes.foldLeft(lineType)((averageType, current) => {
          val currentLine = current._2
          val weightingFactor = current._1 / totalLength
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
      weightedLineType
    }
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
