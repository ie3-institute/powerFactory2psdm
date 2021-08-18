/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.config

import edu.ie3.powerFactory2psdm.config.ConversionConfig.StatGenModelConfigs

final case class ConversionConfig(modelConfigs: StatGenModelConfigs)

/**
  * Config used for the grid conversion
  */
object ConversionConfig {
  val loadFlowSource: String = "load flow"
  val basicDataSource: String = "basic data"

  /**
    * Groups all configs for model conversion of static generators.
    *
    * @param pvConfig config for the pv models
    * @param sRatedSource which apparent power source to choose from the PowerFactory model. Can be either [[basicDataSource]]
    *                     which uses the apparent power mentioned in the models basic data (Basisdaten) or
    *                     [[loadFlowSource]] which uses the apparent power of the load flow (Lastfluss) specification
    * @param cosPhiSource which cosinus phi source to choose from the PowerFactory model. Can be either [[basicDataSource]]
    *                     which uses the rated cosinus phi mentioned in the models basic data (Basisdaten) or
    *                     [[loadFlowSource]] which uses the cosinus phi of the load flow (Lastfluss) specification
    *
    */
  case class StatGenModelConfigs(
      pvConfig: PvConfig,
      sRatedSource: String,
      cosPhiSource: String
  )

  /**
    * General configuration for pv model conversion of static generators of category "Fotovoltaik".
    * As the model does not include most of the model parameters we can choose by setting the [[conversionMode]] to
    * either convert the models to [[edu.ie3.datamodel.models.input.system.FixedFeedInInput]] or generate a
    * [[edu.ie3.datamodel.models.input.system.PvInput]] by sampling the missing parameters.
    *
    * @param conversionMode convert to fixed feed-in or generate model
    * @param individualConfigs for certain generators
    */
  case class PvConfig(
      conversionMode: PvConversionMode,
      individualConfigs: Option[List[IndividualPvConfig]]
  )

  /**
    * Individual configuration for conversion of the set of generators with the given [[ids]]
    *
    * @param ids models to apply the [[conversionMode]] to
    * @param conversionMode to apply for the models
    */
  case class IndividualPvConfig(
      ids: Set[String],
      conversionMode: PvConversionMode
  )

  /**
    * Trait to denote modes for converting pv static generators
    */
  sealed trait PvConversionMode

  /**
    * Convert to a [[edu.ie3.datamodel.models.input.system.FixedFeedInInput]]
    */
  case object PvFixedFeedIn extends PvConversionMode

  /**
    * Convert to a [[edu.ie3.datamodel.models.input.system.PvInput]] considering the given methods for generating the
    * missing parameters.
    *
    * @param albedo Albedo value (typically a value between 0 and 1)
    * @param azimuth Inclination in a compass direction (typically °: South 0◦; West 90◦; East -90◦)
    * @param height Tilted inclination from horizontal (typically in °)
    * @param etaConv Efficiency of converter (typically in %)
    * @param qCharacteristic Description of a reactive power characteristic
    * @param kG Generator correction factor merging different technical influences
    * @param kT Generator correction factor merging different technical influences
    */
  case class PvModelGeneration(
      albedo: GenerationMethod,
      azimuth: GenerationMethod,
      height: GenerationMethod,
      etaConv: GenerationMethod,
      qCharacteristic: QCharacteristic,
      kG: GenerationMethod,
      kT: GenerationMethod
  ) extends PvConversionMode

  /**
    * Trait to group different methods for generating a value for a model parameter
    */
  sealed trait GenerationMethod

  /**
    * Use the given value fixed value
    *
    * @param value to be used
    */
  case class Fixed(
      value: Double
  ) extends GenerationMethod

  /**
    * Sample a value between [[lowerBound]] and [[upperBound]] from a uniform distribution
    *
    * @param lowerBound of the distribution
    * @param upperBound of the distribution
    */
  case class UniformDistribution(
      lowerBound: Double,
      upperBound: Double
  ) extends GenerationMethod

  /**
    * Sample a value from a normal distribution
    *
    * @param mean of the distribution
    * @param standardDeviation of the distribution
    */
  case class NormalDistribution(
      mean: Double,
      standardDeviation: Double
  ) extends GenerationMethod

  /**
    * Trait to group QCharacteristic (reactive power characteristic)
    */
  sealed trait QCharacteristic

  /**
    * Use the cosinus phi power factor of the model to establish a fixed QCharacteristic
    */
  case object FixedQCharacteristic extends QCharacteristic

  /**
    * Dependent power characteristic dependent on either power or nodal voltage magnitude. See here:
    * https://powersystemdatamodel.readthedocs.io/en/latest/models/input/participant/general.html?highlight=reactive#reactive-power-characteristics
    * for details and how the [[characteristic]] string has to look like.
    *
    * @param characteristic to follow
    */
  case class DependentQCharacteristic(characteristic: String)
      extends QCharacteristic

}
