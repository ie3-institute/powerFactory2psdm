/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter.types
import edu.ie3.datamodel.models.input.connector.`type`.LineTypeInput
import edu.ie3.powerFactory2psdm.model.entity.types.LineType
import tech.units.indriya.quantity.Quantities
import edu.ie3.util.quantities.PowerSystemUnits.{
  KILOVOLT,
  OHM_PER_KILOMETRE,
  SIEMENS_PER_KILOMETRE
}
import tech.units.indriya.unit.Units.AMPERE

import java.util.UUID
import javax.measure.MetricPrefix

/**
  * Functionality to translate a [[LineType]] to a [[LineTypeInput]]
  */
object LineTypeConverter {

  def convert(input: LineType): LineTypeInput = {
    val b = Quantities.getQuantity(
      input.b,
      MetricPrefix.MICRO(SIEMENS_PER_KILOMETRE)
    )
    val g = Quantities.getQuantity(
      input.g,
      MetricPrefix.MICRO(SIEMENS_PER_KILOMETRE)
    )
    val r = Quantities.getQuantity(
      input.r,
      OHM_PER_KILOMETRE
    )
    val x = Quantities.getQuantity(
      input.x,
      OHM_PER_KILOMETRE
    )
    val iMax = Quantities.getQuantity(
      input.iMax,
      MetricPrefix.KILO(AMPERE)
    )
    val vRated =
      Quantities.getQuantity(
        input.vRated,
        KILOVOLT
      )

    new LineTypeInput(
      UUID.randomUUID(),
      input.id,
      b,
      g,
      r,
      x,
      iMax,
      vRated
    )
  }
}
