/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.config.model

import edu.ie3.powerFactory2psdm.config.model.IndividualModelConfig.ModelConversionMode

trait DefaultModelConfig {
  val conversionMode: ModelConversionMode
  val individualConfigs: Option[List[IndividualModelConfig]]
}

object DefaultModelConfig {
  def getConversionModes(
      config: DefaultModelConfig
  ): Seq[ModelConversionMode] = {
    Seq(config.conversionMode) ++ config.individualConfigs
      .getOrElse(Nil)
      .map(conf => conf.conversionMode)
  }
}
