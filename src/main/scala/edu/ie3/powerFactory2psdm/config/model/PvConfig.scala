/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.config.model

import edu.ie3.powerFactory2psdm.config.ConversionConfigUtils.{
  ModelConversionMode,
  QCharacteristic
}
import edu.ie3.powerFactory2psdm.config.model.PvConfig.{
  IndividualPvConfig,
  PvModelConversionMode
}
import edu.ie3.powerFactory2psdm.generator.ParameterSamplingMethod

/** General configuration for pv model conversion of static generators of
  * category "Fotovoltaik". As the model does not include most of the model
  * parameters we can choose by setting the [[conversionMode]] to either convert
  * the models to [[edu.ie3.datamodel.models.input.system.FixedFeedInInput]] or
  * generate a [[edu.ie3.datamodel.models.input.system.PvInput]] by sampling the
  * missing parameters.
  *
  * @param conversionMode
  *   convert to fixed feed-in or generate model
  * @param individualConfigs
  *   for certain generators
  */
final case class PvConfig(
    conversionMode: PvModelConversionMode,
    individualConfigs: Option[List[IndividualPvConfig]]
) extends DefaultModelConfig

object PvConfig {

  /** Individual configuration for conversion of the set of generators with the
    * given [[ids]]
    *
    * @param ids
    *   models to apply the [[conversionMode]] to
    * @param conversionMode
    *   to apply for the models
    */
  final case class IndividualPvConfig(
      ids: Set[String],
      conversionMode: PvModelConversionMode
  ) extends IndividualModelConfig

  /** Trait to denote modes for converting pv static generators
    */
  sealed trait PvModelConversionMode extends ModelConversionMode

  /** Convert to a [[edu.ie3.datamodel.models.input.system.FixedFeedInInput]]
    *
    * @param qCharacteristic
    *   Description of a reactive power characteristic
    */
  final case class PvFixedFeedIn(qCharacteristic: QCharacteristic)
      extends PvModelConversionMode

  /** Convert to a [[edu.ie3.datamodel.models.input.system.PvInput]] considering
    * the given methods for generating the missing parameters.
    *
    * @param albedo
    *   Albedo value (typically a value between 0 and 1)
    * @param azimuth
    *   Inclination in a compass direction (typically °: South 0◦; West 90◦;
    *   East -90◦)
    * @param elevationAngle
    *   Tilted inclination from horizontal (typically in °)
    * @param etaConv
    *   Efficiency of converter (typically in %)
    * @param qCharacteristic
    *   Description of a reactive power characteristic
    * @param kG
    *   Generator correction factor merging different technical influences
    * @param kT
    *   Generator correction factor merging different technical influences
    */
  final case class PvModelGeneration(
      albedo: ParameterSamplingMethod,
      azimuth: ParameterSamplingMethod,
      elevationAngle: ParameterSamplingMethod,
      etaConv: ParameterSamplingMethod,
      qCharacteristic: QCharacteristic,
      kG: ParameterSamplingMethod,
      kT: ParameterSamplingMethod
  ) extends PvModelConversionMode
}
