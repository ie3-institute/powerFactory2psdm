/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.config

import edu.ie3.powerFactory2psdm.config.ConversionConfig.StatGenModelConfigs
import edu.ie3.powerFactory2psdm.config.ConversionConfigUtils.ParameterSource
import edu.ie3.powerFactory2psdm.config.model.{
  PvConversionConfig,
  WecConversionConfig
}

final case class ConversionConfig(modelConfigs: StatGenModelConfigs)

/** Config used for the grid conversion
  */
object ConversionConfig {

  /** Groups all configs for model conversion of static generators.
    *
    * @param pvConfig
    *   config for the pv models
    * @param sRatedSource
    *   which apparent power source to choose from the PowerFactory model
    * @param cosPhiSource
    *   which cosinus phi source to choose from the PowerFactory model
    */
  final case class StatGenModelConfigs(
      pvConfig: PvConversionConfig,
      wecConfig: WecConversionConfig,
      sRatedSource: ParameterSource,
      cosPhiSource: ParameterSource
  )

  sealed trait GenerationMethod

  /** Use the given value fixed value
    *
    * @param value
    *   to be used
    */
  final case class Fixed(
      value: Double
  ) extends GenerationMethod

  /** Sample a value between [[lowerBound]] and [[upperBound]] from a uniform
    * distribution
    *
    * @param lowerBound
    *   of the distribution
    * @param upperBound
    *   of the distribution
    */
  final case class UniformDistribution(
      lowerBound: Double,
      upperBound: Double
  ) extends GenerationMethod

  /** Sample a value from a normal distribution
    *
    * @param mean
    *   of the distribution
    * @param standardDeviation
    *   of the distribution
    */
  final case class NormalDistribution(
      mean: Double,
      standardDeviation: Double
  ) extends GenerationMethod

  /** Trait to group QCharacteristic (reactive power characteristic)
    */
  sealed trait QCharacteristic

  /** Use the cosinus phi power factor of the model to establish a fixed
    * QCharacteristic
    */
  final case object FixedQCharacteristic extends QCharacteristic

  /** Dependent power characteristic dependent on either power or nodal voltage
    * magnitude.
    *
    * @param characteristic
    *   to follow
    * @see
    *   See
    *   [[https://powersystemdatamodel.readthedocs.io/en/latest/models/input/participant/general.html?highlight=reactive#reactive-power-characteristics PowerSystemDataModel]]
    *   for details and how the [[characteristic]] string has to look like.
    */
  final case class DependentQCharacteristic(characteristic: String)
      extends QCharacteristic

}
