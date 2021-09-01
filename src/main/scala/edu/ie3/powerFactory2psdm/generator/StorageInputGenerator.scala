/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.generator

import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.system.StorageInput
import edu.ie3.datamodel.models.input.system.`type`.StorageTypeInput
import edu.ie3.powerFactory2psdm.config.model.BsConversionConfig.BatteryStorageModelGeneration
import edu.ie3.powerFactory2psdm.converter.ConversionHelper.{
  convertQCharacteristic,
  determineCosPhiRated
}
import edu.ie3.powerFactory2psdm.generator.types.StorageTypeInputGenerator
import edu.ie3.powerFactory2psdm.model.entity.StaticGenerator

import java.util.UUID

object StorageInputGenerator {

  def generate(
      input: StaticGenerator,
      node: NodeInput,
      params: BatteryStorageModelGeneration
  ): (StorageInput, StorageTypeInput) = {
    val cosPhiRated = determineCosPhiRated(input)
    val qCharacteristic =
      convertQCharacteristic(params.qCharacteristic, cosPhiRated)
    val storageTypeInput = StorageTypeInputGenerator.generate(input, params)

    val storageInput = new StorageInput(
      UUID.randomUUID(),
      input.id,
      node,
      qCharacteristic,
      storageTypeInput
    )
    (storageInput, storageTypeInput)
  }

}
