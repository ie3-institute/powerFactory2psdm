/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.generator

import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.system.BmInput
import edu.ie3.datamodel.models.input.system.`type`.BmTypeInput
import edu.ie3.datamodel.models.input.system.characteristic.ReactivePowerCharacteristic
import edu.ie3.powerFactory2psdm.config.model.BmConversionConfig.BmModelGeneration
import edu.ie3.powerFactory2psdm.converter.ConversionHelper
import edu.ie3.powerFactory2psdm.generator.types.BmTypeGenerator
import edu.ie3.powerFactory2psdm.model.entity.StaticGenerator
import edu.ie3.powerFactory2psdm.util.QuantityUtils.RichQuantityDouble
import edu.ie3.powerFactory2psdm.util.RandomSampler
import java.util.UUID

object BmGenerator {

  /** Generates a [[BmInput]] and a [[BmTypeInput]] to replace a shallow
    * [[StaticGenerator]]. As a static generator does not hold all parameters
    * necessary, the other parameters are generated via the defined generation
    * methods for every parameter.
    *
    * @param input
    *   base model of the generation
    * @param node
    *   the node the input is connected to
    * @param params
    *   parameters for generating missing parameters
    * @return
    *   [[BmInput]] and a [[BmTypeInput]] that replace the [[StaticGenerator]]
    */
  def generate(
      input: StaticGenerator,
      node: NodeInput,
      params: BmModelGeneration
  ): (BmInput, BmTypeInput) = {
    val cosPhiRated = ConversionHelper.determineCosPhiRated(input)
    val qCharacteristics: ReactivePowerCharacteristic = ConversionHelper
      .convertQCharacteristic(params.qCharacteristic, cosPhiRated)
    val bmTypeInput = BmTypeGenerator.convert(input, params)
    val feedInTariff =
      RandomSampler.sample(params.feedInTariff).toEuroPerMegaWattHour

    val bmInput = new BmInput(
      UUID.randomUUID(),
      input.id,
      node,
      qCharacteristics,
      BmTypeInput,
      params.marketReaction,
      params.costControlled,
      feedInTariff
    )
    (bmInput, bmTypeInput)
  }

}
