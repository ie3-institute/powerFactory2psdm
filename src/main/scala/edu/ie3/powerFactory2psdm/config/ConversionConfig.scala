/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.config

import edu.ie3.powerFactory2psdm.config.ConversionConfig.{
  NodeUuidMappingInformation,
  OutputConfig,
  StatGenModelConfigs
}
import edu.ie3.powerFactory2psdm.config.ConversionConfigUtils.ParameterSource
import edu.ie3.powerFactory2psdm.config.model.{
  PvConversionConfig,
  WecConversionConfig
}

final case class ConversionConfig(
    gridName: String,
    modelConfigs: StatGenModelConfigs,
    nodeMapping: Option[NodeUuidMappingInformation],
    output: OutputConfig
)

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

  final case class NodeUuidMappingInformation(
      filePath: String,
      csvSeparator: String
  )

  final case class OutputConfig(
      targetFolder: String,
      csvConfig: CsvConfig
  )

  final case class CsvConfig(
      directoryHierarchy: Boolean = false,
      fileEncoding: String = "UTF-8",
      fileEnding: String = ".csv",
      separator: String = ";"
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

}
