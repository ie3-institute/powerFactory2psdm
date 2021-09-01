/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.config.model

import edu.ie3.powerFactory2psdm.config.ConversionConfigUtils.QCharacteristic
import edu.ie3.powerFactory2psdm.config.model.BsConversionConfig.{
  BsModelConversionMode,
  IndividualBsConfig
}
import edu.ie3.powerFactory2psdm.config.model.IndividualModelConfig.ModelConversionMode
import edu.ie3.powerFactory2psdm.generator.ParameterSamplingMethod

final case class BsConversionConfig(
    conversionMode: BsModelConversionMode,
    individualConfigs: Option[List[IndividualBsConfig]]
) extends DefaultModelConfig

object BsConversionConfig {
  final case class IndividualBsConfig(
      ids: Set[String],
      conversionMode: BsModelConversionMode
  ) extends IndividualModelConfig

  sealed trait BsModelConversionMode extends ModelConversionMode

  final case class BsFixedFeedIn(qCharacteristic: QCharacteristic)
      extends BsModelConversionMode

  final case class BatteryStorageModelGeneration(
      capex: ParameterSamplingMethod,
      opex: ParameterSamplingMethod,
      eStorage: ParameterSamplingMethod,
      pMax: ParameterSamplingMethod,
      activePowerGradient: ParameterSamplingMethod,
      eta: ParameterSamplingMethod,
      dod: ParameterSamplingMethod,
      lifeTime: ParameterSamplingMethod,
      lifeCycle: ParameterSamplingMethod,
      qCharacteristic: QCharacteristic,
      behaviour: ParameterSamplingMethod
  ) extends BsModelConversionMode

}
