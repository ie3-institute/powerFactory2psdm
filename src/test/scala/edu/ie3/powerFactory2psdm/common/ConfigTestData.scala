/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.common

import edu.ie3.powerFactory2psdm.config.ConversionConfig
import edu.ie3.powerFactory2psdm.config.ConversionConfig.{ConversionMode, Model}
import edu.ie3.powerFactory2psdm.config.ConversionConfig.Model.DefaultParams
import edu.ie3.powerFactory2psdm.config.ConversionConfig.Model.DefaultParams.Pv

trait ConfigTestData {
  val pvParamsConfig: Pv = Pv(0.2)
  val defaultParams: DefaultParams = DefaultParams(pvParamsConfig)
  val modelConfig: Model = Model(defaultParams)
  val conversionModeConfig: ConversionMode = ConversionMode(true)
  val validConfig: ConversionConfig = ConversionConfig(
    conversionModeConfig,
    modelConfig
  )
}
