/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter.types
import edu.ie3.datamodel.models.input.connector.`type`.LineTypeInput
import edu.ie3.powerFactory2psdm.exception.pf.ElementConfigurationException
import edu.ie3.powerFactory2psdm.model.powerfactory.PowerFactoryGrid.LineTypes
import tech.units.indriya.quantity.Quantities
import edu.ie3.util.quantities.PowerSystemUnits.{
  KILOVOLT,
  OHM_PER_KILOMETRE,
  SIEMENS_PER_KILOMETRE
}
import tech.units.indriya.unit.Units.AMPERE

import java.util.UUID
import javax.measure.MetricPrefix

object LineTypeConverter {

  def convert(input: LineTypes): LineTypeInput = {
    val id = input.id.getOrElse("NO_ID")
    val b = Quantities.getQuantity(
      input.bline.getOrElse(
        throw ElementConfigurationException(
          s"There is no phase-to-ground condcutance defined for line type: $id"
        )
      ),
      MetricPrefix.MICRO(SIEMENS_PER_KILOMETRE)
    )
    val g = Quantities.getQuantity(
      input.gline.getOrElse(
        throw ElementConfigurationException(
          s"There is no phase-to-ground susceptance defined for line type: $id"
        )
      ),
      MetricPrefix.MICRO(SIEMENS_PER_KILOMETRE)
    )
    val r = Quantities.getQuantity(
      input.rline.getOrElse(
        throw ElementConfigurationException(
          s"There is no specific resistance defined for line type: $id"
        )
      ),
      OHM_PER_KILOMETRE
    )
    val x = Quantities.getQuantity(
      input.xline.getOrElse(
        throw ElementConfigurationException(
          s"There is no specific reactance defined for line type: $id"
        )
      ),
      OHM_PER_KILOMETRE
    )
    val iMax = Quantities.getQuantity(
      input.sline.getOrElse(
        throw ElementConfigurationException(
          s"There is no maximum thermal current defined for line type: $id"
        )
      ),
      MetricPrefix.KILO(AMPERE)
    )
    val vRated =
      Quantities.getQuantity(
        input.uline.getOrElse(
          throw ElementConfigurationException(
            s"There is no rated voltage defined for line type: $id"
          )
        ),
        KILOVOLT
      )

    new LineTypeInput(
      UUID.randomUUID(),
      id,
      b,
      g,
      r,
      x,
      iMax,
      vRated
    )
  }
}
