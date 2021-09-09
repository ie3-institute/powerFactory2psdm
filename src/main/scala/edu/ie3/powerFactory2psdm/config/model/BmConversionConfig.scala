/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.config.model

import edu.ie3.powerFactory2psdm.config.ConversionConfigUtils.QCharacteristic
import edu.ie3.powerFactory2psdm.config.model.IndividualModelConfig.ModelConversionMode
import edu.ie3.powerFactory2psdm.config.model.BmConversionConfig.{
  IndividualBmConfig,
  BmModelConversionMode
}
import edu.ie3.powerFactory2psdm.generator.ParameterSamplingMethod

/** General configuration for wec model conversion of static generators of
  * category "Biogas". As the model does not include most of the model
  * parameters we can choose by setting the [[conversionMode]] to either convert
  * the models to [[edu.ie3.datamodel.models.input.system.FixedFeedInInput]] or
  * generate a [[edu.ie3.datamodel.models.input.system.BmInput]] by sampling the
  * missing parameters.
  *
  * @param conversionMode
  *   convert to fixed feed-in or generate model
  * @param individualConfigs
  *   for certain generators
  */
final case class BmConversionConfig(
    conversionMode: BmModelConversionMode,
    individualConfigs: Option[List[IndividualBmConfig]]
) extends DefaultModelConfig

object BmConversionConfig {

  /** Individual configuration for conversion of the set of generators with the
    * given [[ids]]
    *
    * @param ids
    *   models to apply the [[conversionMode]] to
    * @param conversionMode
    *   to apply for the models
    */
  final case class IndividualBmConfig(
      ids: Set[String],
      conversionMode: BmModelConversionMode
  ) extends IndividualModelConfig

  /** Trait to group different methods for generating a value for a model
    * parameter
    */
  sealed trait BmModelConversionMode extends ModelConversionMode

  /** Convert to a [[edu.ie3.datamodel.models.input.system.FixedFeedInInput]]
    *
    * @param qCharacteristic
    *   Description of a reactive power characteristic
    */
  final case class BmFixedFeedIn(qCharacteristic: QCharacteristic)
      extends BmModelConversionMode

  /** Convert to a [[edu.ie3.datamodel.models.input.system.BmInput]] and a
    * corresponding [[edu.ie3.datamodel.models.input.system.`type`.BmTypeInput]]
    *
    * @param capex
    *   - Capital expense for this type of BM in €
    * @param opex
    *   - Capital expense for this type of BM in € per megawatt hour
    * @param activePowerGradient
    *   – Maximum permissible gradient of active power change in % per hour
    * @param etaConv
    *   - Efficiency of converter for this type of BM in %
    * @param qCharacteristic
    *   - Reactive power characteristic to follow
    * @param marketReaction
    *   – Is this asset market oriented
    * @param costControlled
    *   – Does this plant increase the output power if the revenues exceed the
    *   energy generation costs?
    * @param feedInTariff
    *   – Granted feed in tariff in €/MWh
    */
  final case class BmModelGeneration(
      capex: ParameterSamplingMethod,
      opex: ParameterSamplingMethod,
      activePowerGradient: ParameterSamplingMethod,
      etaConv: ParameterSamplingMethod,
      qCharacteristic: QCharacteristic,
      marketReaction: Boolean,
      costControlled: Boolean,
      feedInTariff: ParameterSamplingMethod
  ) extends BmModelConversionMode

}
