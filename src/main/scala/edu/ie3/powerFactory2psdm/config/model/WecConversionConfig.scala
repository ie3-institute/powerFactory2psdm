/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.config.model

import edu.ie3.powerFactory2psdm.config.ConversionConfigUtils.QCharacteristic
import edu.ie3.powerFactory2psdm.config.model.IndividualModelConfig.ModelConversionMode
import edu.ie3.powerFactory2psdm.config.model.WecConversionConfig.{
  IndividualWecConfig,
  WecModelConversionMode
}
import edu.ie3.powerFactory2psdm.generator.ParameterSamplingMethod

/** General configuration for wec model conversion of static generators of
  * category "Wind". As the model does not include most of the model parameters
  * we can choose by setting the [[conversionMode]] to either convert the models
  * to [[edu.ie3.datamodel.models.input.system.FixedFeedInInput]] or generate a
  * [[edu.ie3.datamodel.models.input.system.WecInput]] by sampling the missing
  * parameters.
  *
  * @param conversionMode
  *   convert to fixed feed-in or generate model
  * @param individualConfigs
  *   for certain generators
  */
final case class WecConversionConfig(
    conversionMode: WecModelConversionMode,
    individualConfigs: Option[List[IndividualWecConfig]]
) extends DefaultModelConfig[IndividualWecConfig]

object WecConversionConfig {

  /** Individual configuration for conversion of the set of generators with the
    * given [[ids]]
    *
    * @param ids
    *   models to apply the [[conversionMode]] to
    * @param conversionMode
    *   to apply for the models
    */
  final case class IndividualWecConfig(
      ids: Set[String],
      conversionMode: WecModelConversionMode
  ) extends IndividualModelConfig

  /** Trait to group different methods for generating a value for a model
    * parameter
    */
  sealed trait WecModelConversionMode extends ModelConversionMode

  /** Convert to a [[edu.ie3.datamodel.models.input.system.FixedFeedInInput]]
    *
    * @param qCharacteristic
    *   Description of a reactive power characteristic
    */
  final case class WecFixedFeedIn(qCharacteristic: QCharacteristic)
      extends WecModelConversionMode

  /** Convert to a [[edu.ie3.datamodel.models.input.system.WecInput]] and a
    * corresponding
    * [[edu.ie3.datamodel.models.input.system.`type`.WecTypeInput]]
    *
    * @param capex
    *   Captial expense for this type of WEC
    * @param opex
    *   Operating expense for this type of WEC
    * @param cpCharacteristic
    *   Betz curve of this type
    * @param hubHeight
    *   Height from ground to center of rotor for this type of WEC (typically in
    *   m)
    * @param rotorArea
    *   Swept Area of blades for this type of WEC (typically in m²)
    * @param etaConv
    *   Efficiency of converter for this type of WEC (typically in %)
    * @param qCharacteristic
    *   Description of a reactive power characteristic
    */
  final case class WecModelGeneration(
      capex: ParameterSamplingMethod,
      opex: ParameterSamplingMethod,
      cpCharacteristic: String,
      hubHeight: ParameterSamplingMethod,
      rotorArea: ParameterSamplingMethod,
      etaConv: ParameterSamplingMethod,
      qCharacteristic: QCharacteristic
  ) extends WecModelConversionMode

}
