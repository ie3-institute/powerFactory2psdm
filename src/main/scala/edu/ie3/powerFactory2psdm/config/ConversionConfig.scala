/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.config

import edu.ie3.powerFactory2psdm.config.ConversionConfig.ModelConfigs

final case class ConversionConfig(modelConfigs: ModelConfigs)

object ConversionConfig {

  case class ModelConfigs(
      pvConfig: PvConfig
  )

  case class PvConfig(
      conversionMode: PvConversionMode,
      individualConfigs: Option[List[IndividualPvConfig]]
  )

  case class IndividualPvConfig(
      ids: Set[String],
      conversionMode: PvConversionMode
  )

  sealed trait PvConversionMode

  case object PvFixedFeedIn extends PvConversionMode

  case class PvModelGeneration(
      albedo: GenerationMethod,
      azimuth: GenerationMethod,
      height: GenerationMethod,
      etaConv: GenerationMethod,
      qCharacteristic: QCharacteristic,
      kG: GenerationMethod,
      kT: GenerationMethod
  ) extends PvConversionMode

  sealed trait WecConversionMode

  case object WecFixedFeedIn extends WecConversionMode

  case class WecModelGeneration(
      capex: GenerationMethod,
      opex: GenerationMethod,
      cpCharacteristics: String,
      hubHeight: GenerationMethod,
      rotorArea: GenerationMethod,
      etaConv: GenerationMethod,
      qCharacteristic: QCharacteristic
  )

  sealed trait GenerationMethod

  case class Fixed(
      value: Double
  ) extends GenerationMethod

  case class UniformDistribution(
      lowerBound: Double,
      upperBound: Double
  ) extends GenerationMethod

  case class NormalDistribution(
      mean: Double,
      standardDeviation: Double
  ) extends GenerationMethod

  sealed trait QCharacteristic

  // use cos phi value of the model
  case object FixedQCharacteristic extends QCharacteristic

  /*
  reactive power characteristic dependent on either power or nodal voltage magnitude as described here:
  https://powersystemdatamodel.readthedocs.io/en/latest/models/input/participant/general.html?highlight=reactive#reactive-power-characteristics
   */
  case class DependentQCharacteristic(characteristic: String)
      extends QCharacteristic

}
