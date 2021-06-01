/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter.`type`

import edu.ie3.datamodel.models.input.connector.`type`.LineTypeInput
import edu.ie3.powerFactory2psdm.exception.pf.ElementConfigurationException
import edu.ie3.powerFactory2psdm.model.powerfactory.PowerFactoryGrid.LineTypes
import tech.units.indriya.quantity.Quantities
import edu.ie3.util.quantities.PowerSystemUnits.{
  KILOVOLT,
  OHM_PER_KILOMETRE,
  SIEMENS_PER_KILOMETRE
}
import edu.ie3.util.quantities.interfaces.{
  SpecificConductance,
  SpecificResistance
}
import tech.units.indriya.ComparableQuantity
import tech.units.indriya.unit.Units.AMPERE

import java.util.UUID
import javax.measure.MetricPrefix
import javax.measure.quantity.{ElectricCurrent, ElectricPotential}

object LineTypeConverter {

  def convert(input: LineTypes): LineTypeInput = {

    /*
      args:

      uuid: UUID,
      id: String, -> loc_name
      Specific phase-to-ground conductance
      b: ComparableQuantity[SpecificConductance], -> bline (Mitsystem ?)
      Specific phase-to-ground susceptance
      g: ComparableQuantity[SpecificConductance], -> gline???
      specific resistance
      r: ComparableQuantity[SpecificResistance], -> rline
      specific reactance
      x: ComparableQuantity[SpecificResistance], -> xline
      maximum thermal current
      iMax: ComparableQuantity[ElectricCurrent], -> sline
      rated voltage
      vRated: ComparableQuantity[ElectricPotential]) -> uline
     */

    val id: String = input.id.getOrElse("NO_ID")
    val bQty: ComparableQuantity[SpecificConductance] = Quantities.getQuantity(
      input.bline.getOrElse(
        throw ElementConfigurationException(
          s"There is no phase-to-ground condcutance defined for line type: $id"
        )
      ),
      MetricPrefix.MICRO(SIEMENS_PER_KILOMETRE)
    )
    // todo: CHECK IF THAT IS ACTUALLY THE CORRECT PARAMETER
    val gQty: ComparableQuantity[SpecificConductance] = Quantities.getQuantity(
      input.gline.getOrElse(
        throw ElementConfigurationException(
          s"There is no phase-to-ground susceptance defined for line type: $id"
        )
      ),
      MetricPrefix.MICRO(SIEMENS_PER_KILOMETRE)
    )
    val rQty: ComparableQuantity[SpecificResistance] = Quantities.getQuantity(
      input.rline.getOrElse(
        throw ElementConfigurationException(
          s"There is no specific resistance defined for line type: $id"
        )
      ),
      OHM_PER_KILOMETRE
    )
    val xQty: ComparableQuantity[SpecificResistance] = Quantities.getQuantity(
      input.xline.getOrElse(
        throw ElementConfigurationException(
          s"There is no specific reactance defined for line type: $id"
        )
      ),
      OHM_PER_KILOMETRE
    )
    val iMaxQty: ComparableQuantity[ElectricCurrent] = Quantities.getQuantity(
      input.sline.getOrElse(
        throw ElementConfigurationException(
          s"There is no maximum thermal current defined for line type: $id"
        )
      ),
      MetricPrefix.KILO(AMPERE)
    )
    val vRatedQty: ComparableQuantity[ElectricPotential] =
      Quantities.getQuantity(
        input.uline.getOrElse(
          throw ElementConfigurationException(
            s"There is no maximum thermal current defined for line type: $id"
          )
        ),
        KILOVOLT
      )

    new LineTypeInput(
      UUID.randomUUID(),
      id,
      bQty,
      gQty,
      rQty,
      xQty,
      iMaxQty,
      vRatedQty
    )

  }

}
