/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.config.validate.conversion

import edu.ie3.powerFactory2psdm.config.model.PvConversionConfig.{
  PvFixedFeedIn,
  PvModelConversionMode,
  PvModelGeneration
}
import edu.ie3.powerFactory2psdm.config.model.WecConversionConfig.{
  WecFixedFeedIn,
  WecModelConversionMode,
  WecModelGeneration
}
import edu.ie3.powerFactory2psdm.config.validate.ConfigValidator.{
  validateCpCharacteristic,
  validateParameterSamplingMethod,
  validateQCharacteristic
}
import edu.ie3.powerFactory2psdm.exception.io.ConversionConfigException

import scala.util.{Failure, Success}

object ConversionModeValidators {

  object PvConversionModeValidator
      extends ConversionModeValidator[PvModelConversionMode] {

    def validate(conversionMode: PvModelConversionMode): Unit = {
      conversionMode match {
        case x: PvFixedFeedIn     => validateQCharacteristic(x.qCharacteristic)
        case x: PvModelGeneration => validateModelGenerationParams(x)
      }
    }

    private[config] def validateModelGenerationParams(
        params: PvModelGeneration
    ): Unit = {
      validateParameterSamplingMethod(params.albedo, 0, 1) match {
        case Success(_) =>
        case Failure(exc) =>
          throw ConversionConfigException(
            s"The albedo of the plants surrounding: ${params.albedo} isn't valid.",
            exc
          )
      }
      validateParameterSamplingMethod(params.azimuth, -90, 90) match {
        case Success(_) =>
        case Failure(exc) =>
          throw ConversionConfigException(
            s"The azimuth of the plant: ${params.azimuth} isn't valid.",
            exc
          )
      }
      validateParameterSamplingMethod(params.etaConv, 0, 100) match {
        case Success(_) =>
        case Failure(exc) =>
          throw ConversionConfigException(
            s"The efficiency of the plants inverter: ${params.etaConv} isn't valid.",
            exc
          )
      }
      validateParameterSamplingMethod(params.kG, 0, 1) match {
        case Success(_) =>
        case Failure(exc) =>
          throw ConversionConfigException(
            s"The PV generator correction factor (kG): ${params.kG} isn't valid.",
            exc
          )
      }
      validateParameterSamplingMethod(params.kT, 0, 1) match {
        case Success(_) =>
        case Failure(exc) =>
          throw ConversionConfigException(
            s"The PV temperature correction factor (kT): ${params.kT} isn't valid.",
            exc
          )
      }
      validateQCharacteristic(params.qCharacteristic) match {
        case Success(_) =>
        case Failure(exc) =>
          throw ConversionConfigException(
            s"The PV q characteristic: ${params.qCharacteristic} configuration isn't valid.",
            exc
          )
      }
    }

  }
  object WecConversionModeValidator
      extends ConversionModeValidator[WecModelConversionMode] {

    def validate(conversionMode: WecModelConversionMode): Unit =
      conversionMode match {
        case x: WecFixedFeedIn     => validateQCharacteristic(x.qCharacteristic)
        case x: WecModelGeneration => validateModelGenerationParams(x)
      }

    private[config] def validateModelGenerationParams(
        params: WecModelGeneration
    ): Unit = {
      validateParameterSamplingMethod(params.capex, 0, Double.MaxValue) match {
        case Success(_) =>
        case Failure(exc) =>
          throw ConversionConfigException(
            s"The WECs capital expenditure: ${params.capex} isn't valid.",
            exc
          )
      }
      validateParameterSamplingMethod(params.opex, 0, Double.MaxValue) match {
        case Success(_) =>
        case Failure(exc) =>
          throw ConversionConfigException(
            s"The WECs operational expenditure: ${params.opex} isn't valid.",
            exc
          )
      }
      validateParameterSamplingMethod(params.etaConv, 0, 100) match {
        case Success(_) =>
        case Failure(exc) =>
          throw ConversionConfigException(
            s"The WECs efficiency of the plants inverter: ${params.etaConv} isn't valid.",
            exc
          )
      }
      validateParameterSamplingMethod(
        params.hubHeight,
        0,
        Double.MaxValue
      ) match {
        case Success(_) =>
        case Failure(exc) =>
          throw ConversionConfigException(
            s"The WECs hub height: ${params.hubHeight} isn't valid.",
            exc
          )
      }
      validateParameterSamplingMethod(
        params.rotorArea,
        0,
        Double.MaxValue
      ) match {
        case Success(_) =>
        case Failure(exc) =>
          throw ConversionConfigException(
            s"The WECs rotorArea: ${params.rotorArea} isn't valid.",
            exc
          )
      }
      validateQCharacteristic(params.qCharacteristic) match {
        case Success(_) =>
        case Failure(exc) =>
          throw ConversionConfigException(
            "The WEC q characteristic configuration isn't valid.",
            exc
          )
      }
      validateCpCharacteristic(params.cpCharacteristic) match {
        case Success(_) =>
        case Failure(exc) =>
          throw ConversionConfigException(
            "The WECs cpCharacteristic configuration isn't valid.",
            exc
          )
      }
    }

  }
}
