/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter.types

import edu.ie3.datamodel.models.input.connector.`type`.Transformer2WTypeInput
import edu.ie3.powerFactory2psdm.exception.pf.ElementConfigurationException
import edu.ie3.powerFactory2psdm.model.powerfactory.types.Transformer2wType
import tech.units.indriya.quantity.Quantities
import tech.units.indriya.unit.Units.{OHM, PERCENT, SIEMENS}
import edu.ie3.util.quantities.PowerSystemUnits.{
  DEGREE_GEOM,
  KILOVOLT,
  VOLTAMPERE
}

import java.util.UUID
import javax.measure.MetricPrefix

object TransformerType2wConverter {

  // todo DO SOME PROPER TESTING
  def convert(input: Transformer2wType): Transformer2WTypeInput = {

    val iRated = input.sRated / (math.sqrt(3) * input.vRatedA) // MVA / kV -> kA

    // short circuit experiment
    val rk = input.pCu / (3 * iRated) // kW / kA -> Ohm
    val zk = (input.vRatedA / math.sqrt(3)) / iRated // kV / kA -> Ohm
    val xk = math.sqrt(math.pow(zk, 2) - math.pow(rk, 2)) // Ohm

    // no load experiment
    val iNoLoadNom = input.iNoLoad * iRated
    val zNoLoad = (input.vRatedA / math.sqrt(3)) / iNoLoadNom // kV / kA -> Ohm
    val rp = math.pow(input.vRatedA, 2) / (input.pFe * 10e-3) // MV / MW -> Ohm
    val xh = 1 / math.sqrt((1 / math.pow(zNoLoad, 2)) - 1 / math.pow(rp, 2)) // -> Ohm

    val tapSide = input.tapSide match {
      case 0 => false
      case 1 => true
      case _ =>
        throw ElementConfigurationException(
          s"The tap side of the transformer-type ${input.id} is neither 0 nor 1 - I am confused!"
        )
    }

    new Transformer2WTypeInput(
      UUID.randomUUID(),
      input.id,
      Quantities.getQuantity(rk, OHM),
      Quantities.getQuantity(xk, OHM),
      Quantities.getQuantity(input.sRated, MetricPrefix.MEGA(VOLTAMPERE)),
      Quantities.getQuantity(input.vRatedA, KILOVOLT),
      Quantities.getQuantity(input.vRatedB, KILOVOLT),
      Quantities.getQuantity(1 / rp, SIEMENS),
      Quantities.getQuantity(1 / xh, SIEMENS),
      Quantities.getQuantity(input.dV, PERCENT),
      Quantities.getQuantity(input.dPhi, DEGREE_GEOM),
      tapSide,
      input.tapNeutr.toInt,
      input.tapMin.toInt,
      input.tapMax.toInt
    )

  }

}
