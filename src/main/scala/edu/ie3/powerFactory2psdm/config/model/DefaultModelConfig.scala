/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.config.model

import edu.ie3.powerFactory2psdm.config.model.IndividualModelConfig.ModelConversionMode

/** Default model config to apply for the model conversion.
  */
trait DefaultModelConfig[M <: ModelConversionMode, I <: IndividualModelConfig[
  M
]] {
  val conversionMode: M
  val individualConfigs: Option[List[I]]

  def getIndividualModelConfig(modelId: String): Option[I] =
    individualConfigs.flatMap(configs => configs.find(_.ids.contains(modelId)))

  /** Return conversion modes of the default and all individual model configs.
    */
  def getConversionModes: Seq[M] = {
    Seq(conversionMode) ++ individualConfigs
      .getOrElse(Nil)
      .map(conf => conf.conversionMode)
  }

  /** Return individual conversion mode of the model if there is one or
    * otherwise the default one
    *
    * @param modelId
    *   the model id for which to retrieve the conversion mode
    * @return
    */
  def getConversionMode(modelId: String): M = {
    getIndividualModelConfig(modelId)
      .map(config => config.conversionMode)
      .getOrElse(conversionMode)
  }
}
