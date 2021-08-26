//package edu.ie3.powerFactory2psdm.config.validate
//
//import edu.ie3.powerFactory2psdm.config.ConversionConfig.{PvConfig, PvModelGeneration}
//import edu.ie3.powerFactory2psdm.config.validate.PvConfigValidator.validatePvModelGenerationParams
//
//object WecConfigValidator {
//
//  private[config] def validate(wecConfig: Wec): Unit = {
//    Seq(pvConfig.conversionMode) ++ pvConfig.individualConfigs
//      .getOrElse(Nil)
//      .map(conf => conf.conversionMode)
//      .collect { case pvModelGeneration: PvModelGeneration =>
//        pvModelGeneration
//      }
//      .map(validatePvModelGenerationParams)
//  }
//
//
//}
