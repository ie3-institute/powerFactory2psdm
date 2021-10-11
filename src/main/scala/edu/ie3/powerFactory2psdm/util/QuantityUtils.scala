/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.util

import edu.ie3.util.quantities.PowerSystemUnits._
import edu.ie3.util.quantities.interfaces.{
  Currency,
  DimensionlessRate,
  EnergyPrice,
  SpecificConductance,
  SpecificResistance
}
import tech.units.indriya.ComparableQuantity
import tech.units.indriya.quantity.Quantities
import tech.units.indriya.unit.Units.{
  AMPERE,
  HOUR,
  METRE,
  OHM,
  PERCENT,
  SIEMENS,
  SQUARE_METRE,
  VOLT
}

import javax.measure.MetricPrefix
import javax.measure.quantity.{
  Angle,
  Area,
  Dimensionless,
  ElectricConductance,
  ElectricCurrent,
  ElectricPotential,
  ElectricResistance,
  Energy,
  Length,
  Power,
  Time
}

@deprecated("This class will be moved to the PowerSystemUtils")
object QuantityUtils {

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

    def toEuro: ComparableQuantity[Currency] =
      Quantities.getQuantity(
        value,
        EURO
      )

    def toEuroPerMegaWattHour: ComparableQuantity[EnergyPrice] =
      Quantities.getQuantity(
        value,
        EURO_PER_MEGAWATTHOUR
      )

    def toKiloWattHour: ComparableQuantity[Energy] =
      Quantities.getQuantity(
        value,
        KILOWATTHOUR
      )

    def toPercentPerHour: ComparableQuantity[DimensionlessRate] =
      Quantities.getQuantity(
        value,
        PERCENT_PER_HOUR
      )

    def toHour: ComparableQuantity[Time] =
      Quantities.getQuantity(
        value,
        HOUR
      )

    def toKiloWatt: ComparableQuantity[Power] =
      Quantities.getQuantity(
        value,
        KILOWATT
      )

    def toMetre: ComparableQuantity[Length] =
      Quantities.getQuantity(
        value,
        METRE
      )

    def toSquareMetre: ComparableQuantity[Area] =
      Quantities.getQuantity(
        value,
        SQUARE_METRE
      )

  }
}
