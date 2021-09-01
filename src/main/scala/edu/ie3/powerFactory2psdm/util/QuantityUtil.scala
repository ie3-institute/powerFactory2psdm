/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.util

import edu.ie3.util.quantities.PowerSystemUnits.{
  DEGREE_GEOM,
  KILOVOLT,
  OHM_PER_KILOMETRE,
  PU,
  SIEMENS_PER_KILOMETRE,
  VOLTAMPERE
}
import edu.ie3.util.quantities.interfaces.{
  SpecificConductance,
  SpecificResistance
}
import tech.units.indriya.ComparableQuantity
import tech.units.indriya.quantity.Quantities
import tech.units.indriya.unit.Units.{AMPERE, OHM, PERCENT, SIEMENS, VOLT}

import javax.measure.MetricPrefix
import javax.measure.quantity.{
  Angle,
  Dimensionless,
  ElectricConductance,
  ElectricCurrent,
  ElectricPotential,
  ElectricResistance,
  Power
}

@deprecated("This class will be moved to the PowerSystemUtils")
object QuantityUtil {

  /** Implicit class to enrich the [[Double]] with [[ComparableQuantity]]
    * conversion capabilities
    *
    * @param value
    *   the actual double value
    */
  implicit class RichQuantityDouble(value: Double) {

    def toPercent: ComparableQuantity[Dimensionless] =
      Quantities.getQuantity(value, PERCENT)

    def toPu: ComparableQuantity[Dimensionless] =
      Quantities.getQuantity(value, PU)

    def toDegreeGeom: ComparableQuantity[Angle] =
      Quantities.getQuantity(value, DEGREE_GEOM)

    def toSiemens: ComparableQuantity[ElectricConductance] =
      Quantities.getQuantity(value, SIEMENS)

    def toNanoSiemens: ComparableQuantity[ElectricConductance] =
      Quantities.getQuantity(value, MetricPrefix.NANO(SIEMENS))

    def toMicroSiemensPerKilometre: ComparableQuantity[SpecificConductance] =
      Quantities.getQuantity(
        value,
        MetricPrefix.MICRO(SIEMENS_PER_KILOMETRE)
      )

    def toOhm: ComparableQuantity[ElectricResistance] = Quantities.getQuantity(
      value,
      OHM
    )

    def toMilliOhm: ComparableQuantity[ElectricResistance] =
      Quantities.getQuantity(value, MetricPrefix.MILLI(OHM))

    def toOhmPerKilometre: ComparableQuantity[SpecificResistance] =
      Quantities.getQuantity(
        value,
        OHM_PER_KILOMETRE
      )

    def toMegaVoltAmpere: ComparableQuantity[Power] = Quantities.getQuantity(
      value,
      MetricPrefix.MEGA(VOLTAMPERE)
    )

    def toKiloAmpere: ComparableQuantity[ElectricCurrent] =
      Quantities.getQuantity(
        value,
        MetricPrefix.KILO(AMPERE)
      )

    def toVolt: ComparableQuantity[ElectricPotential] =
      Quantities.getQuantity(value, VOLT)

    def toKiloVolt: ComparableQuantity[ElectricPotential] =
      Quantities.getQuantity(
        value,
        KILOVOLT
      )

  }
}
