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
    *   which apparent powegit logr source to choose from the PowerFactory model
    * @param cosPhiSource
    *   which cosinus phi source to choose from the PowerFactory model
    */
  final case class StatGenModelConfigs(
      pvConfig: PvConversionConfig,
      wecConfig: WecConversionConfig,
      sRatedSource: ParameterSource,
      cosPhiSource: ParameterSource
  )
}
