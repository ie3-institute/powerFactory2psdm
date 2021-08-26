package edu.ie3.powerFactory2psdm.config.model

import edu.ie3.powerFactory2psdm.config.ConversionConfigUtils.ConversionMode

trait IndividualModelConfig {
  val ids: Set[String]
  val conversionMode: ConversionMode
}
