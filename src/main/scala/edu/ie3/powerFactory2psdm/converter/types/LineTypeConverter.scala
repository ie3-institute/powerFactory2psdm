/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter.types
import edu.ie3.datamodel.models.input.connector.`type`.LineTypeInput
import edu.ie3.powerFactory2psdm.exception.pf.ConversionException
import edu.ie3.powerFactory2psdm.model.entity.types.LineType
import edu.ie3.powerFactory2psdm.util.QuantityUtils.RichQuantityDouble
import java.util.UUID

/** Functionality to translate a [[LineType]] to a [[LineTypeInput]]
  */
object LineTypeConverter {

  def convert(input: LineType): LineTypeInput = {

    new LineTypeInput(
      UUID.randomUUID(),
      input.id,
      input.b.toMicroSiemensPerKilometre,
      input.g.toMicroSiemensPerKilometre,
      input.r.toOhmPerKilometre,
      input.x.toOhmPerKilometre,
      input.iMax.toKiloAmpere,
      input.vRated.toKiloVolt
    )
  }

  def getLineType(
      id: String,
      lineTypes: Map[String, LineTypeInput]
  ): LineTypeInput = {
    lineTypes.getOrElse(
      id,
      throw ConversionException(
        s"Can't find line type $id within the converted line types."
      )
    )
  }
}
