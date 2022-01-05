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
  KILOMETRE,
  KILOVOLT,
  KILOWATT,
  KILOWATTHOUR,
  MICRO_SIEMENS_PER_KILOMETRE,
  OHM_PER_KILOMETRE,
  PERCENT_PER_HOUR,
  PU,
  SIEMENS_PER_KILOMETRE,
  VOLTAMPERE
}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
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
import javax.measure.quantity.Length

class QuantityUtilsSpec extends Matchers with AnyWordSpecLike {
  "A rich quantity util" should {
    implicit val quantityTolerance: Double = 1e-9
    val value = 10.123154122

    "convert a double to a percent quantity" in {
      value.asPercent should equalWithTolerance(
        Quantities.getQuantity(value, PERCENT)
      )
    }

    "convert a double to a pu value" in {
      value.asPu should equalWithTolerance(Quantities.getQuantity(value, PU))
    }

    "convert a double to degree geom" in {
      value.asDegreeGeom should equalWithTolerance(
        Quantities.getQuantity(value, DEGREE_GEOM)
      )
    }

    "convert a double to siemens" in {
      value.asSiemens should equalWithTolerance(
        Quantities.getQuantity(value, SIEMENS)
      )
    }

    "convert a double to nano siemens" in {
      value.asNanoSiemens should equalWithTolerance(
        Quantities.getQuantity(value, MetricPrefix.NANO(SIEMENS))
      )
    }

    "convert a double to micro siemens per kilometre" in {
      value.asMicroSiemensPerKilometre should equalWithTolerance(
        Quantities.getQuantity(value, MICRO_SIEMENS_PER_KILOMETRE)
      )
    }

    "convert a double to ohm" in {
      value.asOhm should equalWithTolerance(
        Quantities.getQuantity(
          value,
          OHM
        )
      )
    }

    "convert a double to milli ohm" in {
      value.asMilliOhm should equalWithTolerance(
        Quantities.getQuantity(value, MetricPrefix.MILLI(OHM))
      )
    }

    "convert a double to ohm per kilometre" in {
      value.asOhmPerKilometre should equalWithTolerance(
        Quantities.getQuantity(
          value,
          OHM_PER_KILOMETRE
        )
      )
    }

    "convert a double to volt ampere" in {
      value.asVoltAmpere should equalWithTolerance(
        Quantities.getQuantity(
          value,
          VOLTAMPERE
        )
      )
    }

    "convert a double to mega volt ampere" in {
      value.asMegaVoltAmpere should equalWithTolerance(
        Quantities.getQuantity(
          value,
          MetricPrefix.MEGA(VOLTAMPERE)
        )
      )
    }

    "convert a double to ampere" in {
      value.asAmpere should equalWithTolerance(
        Quantities.getQuantity(
          value,
          AMPERE
        )
      )
    }

    "convert a double to kilo ampere" in {
      value.asKiloAmpere should equalWithTolerance(
        Quantities.getQuantity(
          value,
          MetricPrefix.KILO(AMPERE)
        )
      )
    }

    "convert a double to volt" in {
      value.asVolt should equalWithTolerance(
        Quantities.getQuantity(value, VOLT)
      )
    }

    "convert a double to kilo volt" in {
      value.asKiloVolt should equalWithTolerance(
        Quantities.getQuantity(
          value,
          KILOVOLT
        )
      )
    }

    "convert a double to euro" in {
      value.asEuro should equalWithTolerance(
        Quantities.getQuantity(
          value,
          EURO
        )
      )
    }

    "convert a double to euro per megawatt hour" in {
      value.asEuroPerMegaWattHour should equalWithTolerance(
        Quantities.getQuantity(
          value,
          EURO_PER_MEGAWATTHOUR
        )
      )
    }

    "convert a double to euro per kilowatt hour" in {
      value.asKiloWattHour should equalWithTolerance(
        Quantities.getQuantity(
          value,
          KILOWATTHOUR
        )
      )
    }

    "convert a double to percent per hour" in {
      value.asPercentPerHour should equalWithTolerance(
        Quantities.getQuantity(
          value,
          PERCENT_PER_HOUR
        )
      )
    }

    "convert a double to hour" in {
      value.asHour should equalWithTolerance(
        Quantities.getQuantity(
          value,
          HOUR
        )
      )
    }

    "convert a double to kilowatt" in {
      value.asKiloWatt should equalWithTolerance(
        Quantities.getQuantity(
          value,
          KILOWATT
        )
      )
    }

    "convert a double to square metre" in {
      value.asSquareMetre should equalWithTolerance(
        Quantities.getQuantity(
          value,
          SQUARE_METRE
        )
      )
    }

    "convert a double to kilometre" in {
      value.asKilometre should equalWithTolerance(
        Quantities.getQuantity(
          value,
          KILOMETRE
        )
      )
    }

    "convert a double to metre" in {
      value.asMetre should equalWithTolerance(
        Quantities.getQuantity(
          value,
          METRE
        )
      )
    }

  }
}
