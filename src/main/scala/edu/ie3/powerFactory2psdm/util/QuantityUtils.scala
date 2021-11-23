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

    def asPercent: ComparableQuantity[Dimensionless] =
      Quantities.getQuantity(value, PERCENT)

    def asPu: ComparableQuantity[Dimensionless] =
      Quantities.getQuantity(value, PU)

    def asDegreeGeom: ComparableQuantity[Angle] =
      Quantities.getQuantity(value, DEGREE_GEOM)

    def asSiemens: ComparableQuantity[ElectricConductance] =
      Quantities.getQuantity(value, SIEMENS)

    def asNanoSiemens: ComparableQuantity[ElectricConductance] =
      Quantities.getQuantity(value, MetricPrefix.NANO(SIEMENS))

    def asMicroSiemensPerKilometre: ComparableQuantity[SpecificConductance] =
      Quantities.getQuantity(
        value,
        MICRO_SIEMENS_PER_KILOMETRE
      )

    def asOhm: ComparableQuantity[ElectricResistance] = Quantities.getQuantity(
      value,
      OHM
    )

    def asMilliOhm: ComparableQuantity[ElectricResistance] =
      Quantities.getQuantity(value, MetricPrefix.MILLI(OHM))

    def asOhmPerKilometre: ComparableQuantity[SpecificResistance] =
      Quantities.getQuantity(
        value,
        OHM_PER_KILOMETRE
      )

    def asVoltAmpere: ComparableQuantity[Power] = Quantities.getQuantity(
      value,
      VOLTAMPERE
    )

    def asMegaVoltAmpere: ComparableQuantity[Power] = Quantities.getQuantity(
      value,
      MetricPrefix.MEGA(VOLTAMPERE)
    )

    def asAmpere: ComparableQuantity[ElectricCurrent] =
      Quantities.getQuantity(
        value,
        AMPERE
      )

    def asKiloAmpere: ComparableQuantity[ElectricCurrent] =
      Quantities.getQuantity(
        value,
        MetricPrefix.KILO(AMPERE)
      )

    def asVolt: ComparableQuantity[ElectricPotential] =
      Quantities.getQuantity(value, VOLT)

    def asKiloVolt: ComparableQuantity[ElectricPotential] =
      Quantities.getQuantity(
        value,
        KILOVOLT
      )

    def asEuro: ComparableQuantity[Currency] =
      Quantities.getQuantity(
        value,
        EURO
      )

    def asEuroPerMegaWattHour: ComparableQuantity[EnergyPrice] =
      Quantities.getQuantity(
        value,
        EURO_PER_MEGAWATTHOUR
      )

    def asKiloWattHour: ComparableQuantity[Energy] =
      Quantities.getQuantity(
        value,
        KILOWATTHOUR
      )

    def asPercentPerHour: ComparableQuantity[DimensionlessRate] =
      Quantities.getQuantity(
        value,
        PERCENT_PER_HOUR
      )

    def asHour: ComparableQuantity[Time] =
      Quantities.getQuantity(
        value,
        HOUR
      )

    def asKiloWatt: ComparableQuantity[Power] =
      Quantities.getQuantity(
        value,
        KILOWATT
      )

    def asMetre: ComparableQuantity[Length] =
      Quantities.getQuantity(
        value,
        METRE
      )

    def asKilometre: ComparableQuantity[Length] =
      Quantities.getQuantity(
        value,
        KILOMETRE
      )

    def asSquareMetre: ComparableQuantity[Area] =
      Quantities.getQuantity(
        value,
        SQUARE_METRE
      )

  }
}
