/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.generator

import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.system.StorageInput
import edu.ie3.datamodel.models.input.system.`type`.StorageTypeInput
import edu.ie3.datamodel.models.input.system.characteristic.ReactivePowerCharacteristic
import edu.ie3.powerFactory2psdm.config.ConversionConfig.{
  BatteryStorageModelGeneration,
  DependentQCharacteristic,
  FixedQCharacteristic
}
import edu.ie3.powerFactory2psdm.exception.pf.ElementConfigurationException
import edu.ie3.powerFactory2psdm.generator.types.StorageTypeInputGenerator
import edu.ie3.powerFactory2psdm.model.powerfactory.StaticGenerator

import java.util.UUID

object StorageInputGenerator {

  def generate(
      input: StaticGenerator,
      node: NodeInput,
      params: BatteryStorageModelGeneration
  ): (StorageInput, StorageTypeInput) = {
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
    val storageTypeInput = StorageTypeInputGenerator.generate(input, params)

    val storageInput = new StorageInput(
      UUID.randomUUID(),
      input.id,
      node,
      qCharacteristics,
      storageTypeInput
    )
    (storageInput, storageTypeInput)
  }

}
