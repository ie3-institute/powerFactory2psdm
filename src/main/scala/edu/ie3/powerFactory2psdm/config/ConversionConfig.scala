/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.config

import com.typesafe.config.Config
import java.util.Properties
import scala.jdk.CollectionConverters._

object ConversionConfig {

  case class ModelConfigs(
      pvConfig: PvConfig
  )

  case class PvConfig(
      fixedFeedIn: Boolean,
      params: PvParams,
      individualConfigs: Option[List[IndividualPvConfig]]
  )

  case class PvParams(
      albedo: GenerationMethod,
      azimuth: GenerationMethod,
      etaConv: GenerationMethod,
      kG: GenerationMethod,
      kT: GenerationMethod
  )

  case class IndividualPvConfig(
      ids: Set[String],
      params: PvParams
  )

  sealed trait GenerationMethod

  case class Fixed(
      value: Double
  ) extends GenerationMethod

  case class UniformDistribution(
      lowerBound: Double,
      upperBound: Double
  ) extends GenerationMethod

}

trait ConversionConfig {
  implicit class configMapperOps(config: Config) {

    def toMap: Map[String, AnyRef] =
      config
        .entrySet()
        .asScala
        .map(pair => (pair.getKey, config.getAnyRef(pair.getKey)))
        .toMap

    def toProperties: Properties = {
      val properties = new Properties()
      properties.putAll(config.toMap.asJava)
      properties
    }
  }
}
