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
import tech.units.indriya.unit.Units.{OHM, PERCENT, SIEMENS, VOLT}
import edu.ie3.util.quantities.PowerSystemUnits.{
  DEGREE_GEOM,
  VOLTAMPERE
}
import math.{pow, sqrt}

import java.util.UUID
import javax.measure.MetricPrefix

object TransformerType2wConverter {

  // todo DO SOME PROPER TESTING
  def convert(input: Transformer2wType): Transformer2WTypeInput = {

    val sRated = input.sRated * 1e6
    val vRatedA = input.vRatedA * 1e3
    val vRatedB = input.vRatedB * 1e3
    val pCu = input.pCu * 1e3
    val pFe = input.pFe * 1e3
    val uk = (input.uk / 100) * vRatedA


    val iRated = sRated / (math.sqrt(3) * vRatedA)

    // short circuit experiment
    val rk = pCu / (3 * pow(iRated, 2))
    val zk = (uk / sqrt(3)) / iRated
    val xk = sqrt(pow(zk, 2) - pow(rk, 2))

    // no load experiment
    val iNoLoadNom = (input.iNoLoad / 100)  * iRated
    val zNoLoad = (vRatedA / sqrt(3)) / iNoLoadNom
    // fixme check why chris has a factor of 3 here
    val rp = pow(vRatedA, 2) / pFe
    val xh = 1 / sqrt((1 / pow(zNoLoad, 2)) - 1 / pow(rp, 2))

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
      Quantities.getQuantity(vRatedA, VOLT),
      Quantities.getQuantity(vRatedB, VOLT),
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
