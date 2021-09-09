/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.generator.types

import edu.ie3.datamodel.models.input.system.WecInput
import edu.ie3.datamodel.models.input.system.`type`.{BmTypeInput, WecTypeInput}
import edu.ie3.datamodel.models.input.system.characteristic.WecCharacteristicInput
import edu.ie3.powerFactory2psdm.config.model.BmConversionConfig.BmModelGeneration
import edu.ie3.powerFactory2psdm.exception.pf.ElementConfigurationException
import edu.ie3.powerFactory2psdm.model.entity.StaticGenerator
import edu.ie3.powerFactory2psdm.util.RandomSampler
import edu.ie3.util.quantities.PowerSystemUnits
import edu.ie3.util.quantities.interfaces.{Currency, EnergyPrice}
import tech.units.indriya.ComparableQuantity
import tech.units.indriya.quantity.Quantities

import java.util.UUID
import javax.measure.quantity.Power

object BmTypeGenerator {

  /** Generates a [[BmTypeInput]] for a [[BmInput]] model off of a
    * [[StaticGenerator]]. As a static generator does not hold all parameters
    * necessary, the other parameters are generated via the defined generation
    * methods for every parameter.
    *
    * @param statGen
    *   base model of the generation
    * @param params
    *   parameters for generating missing parameters
    * @return
    *   a [[BmTypeInput]]
    */
  def convert(
      statGen: StaticGenerator,
      params: BmModelGeneration
  ): BmTypeInput = {

    val capex: ComparableQuantity[Currency] = Quantities.getQuantity(
      RandomSampler.sample(params.capex),
      PowerSystemUnits.EURO
    )
    val opex: ComparableQuantity[EnergyPrice] = Quantities.getQuantity(
      RandomSampler.sample(params.opex),
      PowerSystemUnits.EURO_PER_MEGAWATTHOUR
    )
    val sRated: ComparableQuantity[Power] =
      Quantities.getQuantity(statGen.sRated, PowerSystemUnits.MEGAVOLTAMPERE)
    val cosPhiRated: Double = statGen.indCapFlag match {
      case 0 => statGen.cosPhi
      case 1 => -statGen.cosPhi
      case _ =>
        throw ElementConfigurationException(
          s"The inductive capacitive specifier of the static generator: ${statGen.id} should be either 0 or 1"
        )
    }
    val activePowerGradient = ???
    val etaConv = ???
    val id = ???

    new BmTypeInput(
      UUID.randomUUID(),
      id,
      capex,
      opex,
      activePowerGradient,
      sRated,
      cosPhiRated,
      etaConv
    )
  }
}
