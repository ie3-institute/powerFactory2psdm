/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter

import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.system.WecInput
import edu.ie3.datamodel.models.input.system.`type`.WecTypeInput
import edu.ie3.datamodel.models.input.system.characteristic.ReactivePowerCharacteristic
import edu.ie3.powerFactory2psdm.config.ConversionConfig.{
  DependentQCharacteristic,
  FixedQCharacteristic,
  WecModelGeneration
}
import edu.ie3.powerFactory2psdm.converter.types.WecTypeConverter
import edu.ie3.powerFactory2psdm.exception.pf.ElementConfigurationException
import edu.ie3.powerFactory2psdm.model.powerfactory.StaticGenerator

import java.util.UUID

object WecConverter {

  def convert(
      input: StaticGenerator,
      node: NodeInput,
      params: WecModelGeneration
  ): (WecInput, WecTypeInput) = {
    val cosPhiRated = input.indCapFlag match {
      case 0 => input.cosPhi
      case 1 => -input.cosPhi
      case _ =>
        throw ElementConfigurationException(
          s"The inductive capacitive specifier of the static generator: ${input.id} should be either 0 or 1"
        )
    }
    val qCharacteristics: ReactivePowerCharacteristic =
      params.qCharacteristic match {
        case FixedQCharacteristic =>
          ReactivePowerCharacteristic.parse(
            s"cosPhiFixed:{(0.0, $cosPhiRated)}"
          )
        case DependentQCharacteristic(characteristic) =>
          ReactivePowerCharacteristic.parse(characteristic)
      }
    val wecTypeInput = WecTypeConverter.convert(input, params)

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
