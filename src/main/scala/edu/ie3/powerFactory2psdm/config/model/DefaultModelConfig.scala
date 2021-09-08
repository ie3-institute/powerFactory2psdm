/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.config.model

import edu.ie3.powerFactory2psdm.config.model.IndividualModelConfig.ModelConversionMode

/** Default model config to apply for the model conversion.
  */
trait DefaultModelConfig {
  val conversionMode: ModelConversionMode
  val individualConfigs: Option[List[IndividualModelConfig]]
}

object DefaultModelConfig {

  /** Return conversion modes of the default and all individual model configs.
    */
  def getConversionModes(
      config: DefaultModelConfig
  ): Seq[ModelConversionMode] = {
    Seq(config.conversionMode) ++ config.individualConfigs
      .getOrElse(Nil)
      .map(conf => conf.conversionMode)
  }
}
