/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.generator

import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.system.WecInput
import edu.ie3.datamodel.models.input.system.`type`.WecTypeInput
import edu.ie3.datamodel.models.input.system.characteristic.ReactivePowerCharacteristic
import edu.ie3.powerFactory2psdm.config.model.WecConversionConfig.WecModelGeneration
import edu.ie3.powerFactory2psdm.converter.ConversionHelper
import edu.ie3.powerFactory2psdm.generator.types.WecTypeGenerator
import edu.ie3.powerFactory2psdm.model.entity.StaticGenerator

import java.util.UUID

object WecGenerator {

  /** Generates a [[WecInput]] and a [[WecTypeInput]] to replace a shallow
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
    *   [[WecInput]] and a [[WecTypeInput]] that replace the [[StaticGenerator]]
    */
  def generate(
      input: StaticGenerator,
      node: NodeInput,
      params: WecModelGeneration
  ): (WecInput, WecTypeInput) = {
    val cosPhiRated = ConversionHelper.determineCosPhiRated(input)
    val qCharacteristics: ReactivePowerCharacteristic = ConversionHelper
      .convertQCharacteristic(params.qCharacteristic, cosPhiRated)
    val wecTypeInput = WecTypeGenerator.convert(input, params)

    val wecInput = new WecInput(
      UUID.randomUUID(),
      input.id,
      node,
      qCharacteristics,
      wecTypeInput,
      false
    )
    (wecInput, wecTypeInput)
  }

}
