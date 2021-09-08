/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.config

import edu.ie3.powerFactory2psdm.config.ConversionConfig.ModelConfigs

final case class ConversionConfig(modelConfigs: ModelConfigs)

object ConversionConfig {

  final case class ModelConfigs(
      pvConfig: PvConfig
  )

  final case class PvConfig(
      conversionMode: PvConversionMode,
      individualConfigs: Option[List[IndividualPvConfig]]
  )

  final case class IndividualPvConfig(
      ids: Set[String],
      conversionMode: PvConversionMode
  )

  sealed trait PvConversionMode

  final case object PvFixedFeedIn extends PvConversionMode

  final case class PvModelGeneration(
      albedo: GenerationMethod,
      azimuth: GenerationMethod,
      etaConv: GenerationMethod,
      kG: GenerationMethod,
      kT: GenerationMethod
  ) extends PvConversionMode

  sealed trait GenerationMethod

  final case class Fixed(
      value: Double
  ) extends GenerationMethod

  final case class UniformDistribution(
      lowerBound: Double,
      upperBound: Double
  ) extends GenerationMethod

  final case class NormalDistribution(
      mean: Double,
      standardDeviation: Double
  ) extends GenerationMethod

}
