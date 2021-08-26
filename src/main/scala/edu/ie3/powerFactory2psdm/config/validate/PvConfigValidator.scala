package edu.ie3.powerFactory2psdm.config.validate

import edu.ie3.powerFactory2psdm.config.model.PvConfig
import edu.ie3.powerFactory2psdm.config.model.PvConfig.PvModelGeneration
import edu.ie3.powerFactory2psdm.config.validate.ConfigValidator.{validateGenerationMethod, validateQCharacteristic}
import edu.ie3.powerFactory2psdm.exception.io.ConversionConfigException

import scala.util.{Failure, Success}

object PvConfigValidator {

  private[config] def validate(pvConfig: PvConfig): Unit = {
    Seq(pvConfig.conversionMode) ++ pvConfig.individualConfigs
      .getOrElse(Nil)
      .map(conf => conf.conversionMode)
      .collect { case pvModelGeneration: PvModelGeneration =>
        pvModelGeneration
      }
      .map(validatePvModelGenerationParams)
  }

  private[config] def validatePvModelGenerationParams(
   params: PvModelGeneration
 ): Unit = {
    validateGenerationMethod(params.albedo, 0, 1) match {
      case Success(_) =>
      case Failure(exc) =>
        throw ConversionConfigException(
          s"The albedo of the plants surrounding: ${params.albedo} isn't valid. Exception: ${exc.getMessage}"
        )
    }
    validateGenerationMethod(params.azimuth, -90, 90) match {
      case Success(_) =>
      case Failure(exc) =>
        throw ConversionConfigException(
          s"The azimuth of the plant: ${params.azimuth} isn't valid. Exception: ${exc.getMessage}"
        )
    }
    validateGenerationMethod(params.etaConv, 0, 100) match {
      case Success(_) =>
      case Failure(exc) =>
        throw ConversionConfigException(
          s"The efficiency of the plants inverter: ${params.azimuth} isn't valid. Exception: ${exc.getMessage}"
        )
    }
    validateGenerationMethod(params.kG, 0, 1) match {
      case Success(_) =>
      case Failure(exc) =>
        throw ConversionConfigException(
          s"The PV generator correction factor (kG): ${params.kG} isn't valid. Exception: ${exc.getMessage}"
        )
    }
    validateGenerationMethod(params.kT, 0, 1) match {
      case Success(_) =>
      case Failure(exc) =>
        throw ConversionConfigException(
          s"The PV temperature correction factor (kT): ${params.kT} isn't valid. Exception: ${exc.getMessage}"
        )
    }
    validateQCharacteristic(params.qCharacteristic) match {
      case Success(_) =>
      case Failure(exc) =>
        throw ConversionConfigException(
          s"The PV q characteristic configuration isn't valid.",
          exc
        )
    }
  }

}
