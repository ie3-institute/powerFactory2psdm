package edu.ie3.powerFactory2psdm.config.model

import edu.ie3.powerFactory2psdm.config.ConversionConfigUtils.{ConversionMode, QCharacteristic}
import edu.ie3.powerFactory2psdm.config.model.WecConfig.{IndividualWecConfig, WecConversionMode}
import edu.ie3.powerFactory2psdm.generator.ParameterSamplingMethod

  final case class WecConfig(
    conversionMode: WecConversionMode,
    individualConfigs: Option[List[IndividualWecConfig]]
  ) extends DefaultModelConfig

object WecConfig {

  final case class IndividualWecConfig(
    ids: Set[String],
    conversionMode: WecConversionMode
  ) extends IndividualModelConfig

  /** Trait to group different methods for generating a value for a model
   * parameter
   */
  sealed trait WecConversionMode extends ConversionMode

  case object WecFixedFeedIn extends WecConversionMode

  case class WecModelGeneration(
     capex: ParameterSamplingMethod,
     opex: ParameterSamplingMethod,
     cpCharacteristics: String,
     hubHeight: ParameterSamplingMethod,
     rotorArea: ParameterSamplingMethod,
     etaConv: ParameterSamplingMethod,
     qCharacteristic: QCharacteristic
   )

}

