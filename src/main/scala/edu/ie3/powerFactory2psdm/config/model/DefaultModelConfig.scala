package edu.ie3.powerFactory2psdm.config.model

import edu.ie3.powerFactory2psdm.config.ConversionConfigUtils.ConversionMode

trait DefaultModelConfig {
  val conversionMode: ConversionMode
  val individualConfigs: Option[List[IndividualModelConfig]]
}

