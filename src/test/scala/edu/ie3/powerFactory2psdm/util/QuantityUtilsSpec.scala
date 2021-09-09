/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.util

import edu.ie3.powerFactory2psdm.util.QuantityUtils.RichQuantityDouble
import edu.ie3.scalatest.QuantityMatchers.equalWithTolerance
import edu.ie3.util.quantities.PowerSystemUnits.{
  DEGREE_GEOM,
  EURO,
  EURO_PER_MEGAWATTHOUR,
  KILOVOLT,
  KILOWATT,
  KILOWATTHOUR,
  OHM_PER_KILOMETRE,
  PERCENT_PER_HOUR,
  PU,
  SIEMENS_PER_KILOMETRE,
  VOLTAMPERE
}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import tech.units.indriya.quantity.Quantities
import tech.units.indriya.unit.Units.{AMPERE, HOUR, OHM, PERCENT, SIEMENS, VOLT}

import javax.measure.MetricPrefix

class QuantityUtilsSpec extends Matchers with AnyWordSpecLike {
  "A rich quantity util" should {
    implicit val quantityTolerance: Double = 1e-9
    val value = 10.123154122

    "convert a double to a percent quantity" in {
      value.toPercent should equalWithTolerance(
        Quantities.getQuantity(value, PERCENT)
      )
    }

    "convert a double to a pu value" in {
      value.toPu should equalWithTolerance(Quantities.getQuantity(value, PU))
    }

    "convert a double to degree geom" in {
      value.toDegreeGeom should equalWithTolerance(
        Quantities.getQuantity(value, DEGREE_GEOM)
      )
    }

    "convert a double to siemens" in {
      value.toSiemens should equalWithTolerance(
        Quantities.getQuantity(value, SIEMENS)
      )
    }

    "convert a double to nano siemens" in {
      value.toNanoSiemens should equalWithTolerance(
        Quantities.getQuantity(value, MetricPrefix.NANO(SIEMENS))
      )
    }

    "convert a double to micro siemens per kilometre" in {
      value.toMicroSiemensPerKilometre should equalWithTolerance(
        Quantities.getQuantity(value, MetricPrefix.MICRO(SIEMENS_PER_KILOMETRE))
      )
    }

    "convert a double to ohm" in {
      value.toOhm should equalWithTolerance(
        Quantities.getQuantity(
          value,
          OHM
        )
      )
    }

    "convert a double to milli ohm" in {
      value.toMilliOhm should equalWithTolerance(
        Quantities.getQuantity(value, MetricPrefix.MILLI(OHM))
      )
    }

    "convert a double to ohm per kilometre" in {
      value.toOhmPerKilometre should equalWithTolerance(
        Quantities.getQuantity(
          value,
          OHM_PER_KILOMETRE
        )
      )
    }

    "convert a double to mega volt ampere" in {
      value.toMegaVoltAmpere should equalWithTolerance(
        Quantities.getQuantity(
          value,
          MetricPrefix.MEGA(VOLTAMPERE)
        )
      )
    }

    "convert a double to kilo ampere" in {
      value.toKiloAmpere should equalWithTolerance(
        Quantities.getQuantity(
          value,
          MetricPrefix.KILO(AMPERE)
        )
      )
    }

    "convert a double to volt" in {
      value.toVolt should equalWithTolerance(
        Quantities.getQuantity(value, VOLT)
      )
    }

    "convert a double to kilo volt" in {
      value.toKiloVolt should equalWithTolerance(
        Quantities.getQuantity(
          value,
          KILOVOLT
        )
      )
    }

    "convert a double to euro" in {
      value.toEuro should equalWithTolerance(
        Quantities.getQuantity(
          value,
          EURO
        )
      )
    }

    "convert a double to euro per megawatt hour" in {
      value.toEuroPerMegaWattHour should equalWithTolerance(
        Quantities.getQuantity(
          value,
          EURO_PER_MEGAWATTHOUR
        )
      )
    }

    "convert a double to euro per kilowatt hour" in {
      value.toKiloWattHour should equalWithTolerance(
        Quantities.getQuantity(
          value,
          KILOWATTHOUR
        )
      )
    }

    "convert a double to percent per hour" in {
      value.toPercentPerHour should equalWithTolerance(
        Quantities.getQuantity(
          value,
          PERCENT_PER_HOUR
        )
      )
    }

    "convert a double to hour" in {
      value.toHour should equalWithTolerance(
        Quantities.getQuantity(
          value,
          HOUR
        )
      )
    }

    "convert a double to kilowatt" in {
      value.toKiloWatt should equalWithTolerance(
        Quantities.getQuantity(
          value,
          KILOWATT
        )
      )
    }

  }
}