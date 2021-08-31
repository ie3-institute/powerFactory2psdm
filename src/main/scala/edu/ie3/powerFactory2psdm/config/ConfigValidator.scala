/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.config

import edu.ie3.datamodel.models.input.system.characteristic.{
  ReactivePowerCharacteristic,
  WecCharacteristicInput
}
import edu.ie3.powerFactory2psdm.config.ConversionConfig.{
  DependentQCharacteristic,
  Fixed,
  ParameterSamplingMethod,
  NormalDistribution,
  PvConfig,
  PvModelGeneration,
  QCharacteristic,
  StatGenModelConfigs,
  UniformDistribution,
  WecModelGeneration
}
import edu.ie3.powerFactory2psdm.exception.io.ConversionConfigException

import scala.util.{Failure, Success, Try}

object ConfigValidator {

  /** Checks the parsed [[ConversionConfig]] for general soundness.
    * @param config
    *   the parsed config
    */
  def validate(config: ConversionConfig): Unit = {
    validateModelConfigs(config.modelConfigs)
  }

  private[config] def validateModelConfigs(
      modelConfigs: StatGenModelConfigs
  ): Unit = {
    validatePvConfig(modelConfigs.pvConfig)
  }

  private[config] def validatePvConfig(pvConfig: PvConfig): Unit = {
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
          s"The efficiency of the plants inverter: ${params.azimuth} isn't valid.",
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
          "The PV q characteristic configuration isn't valid.",
          exc
        )
    }
  }

  private[config] def validateWecModelGenerationParams(
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
    validateCpCharacteristic(params.cpCharacteristics) match {
      case Success(_) =>
      case Failure(exc) =>
        throw ConversionConfigException(
          "The WECs cpCharacteristic configuration isn't valid.",
          exc
        )
    }
  }

  private[config] def validateParameterSamplingMethod(
      genMethod: ParameterSamplingMethod,
      lowerBound: Double,
      upperBound: Double
  ): Try[Unit] =
    genMethod match {
      case Fixed(value) =>
        checkForBoundViolation(value, lowerBound, upperBound)
      case UniformDistribution(min, max) =>
        if (min > max)
          return Failure(
            ConversionConfigException(
              s"The minimum value: $min exceeds the maximum value: $max"
            )
          )
        if (min < lowerBound && max > upperBound)
          return lowerUpperBoundViolation(min, max, lowerBound, upperBound)
        else if (min < lowerBound) return lowerBoundViolation(min, lowerBound)
        else if (max > upperBound) return upperBoundViolation(max, upperBound)
        Success(())
      case NormalDistribution(mean, _) =>
        checkForBoundViolation(mean, lowerBound, upperBound)
    }

  private[config] def checkForBoundViolation(
      value: Double,
      lowerBound: Double,
      upperBound: Double
  ): Try[Unit] = {
    if (value < lowerBound) return lowerBoundViolation(value, lowerBound)
    if (value > upperBound) return upperBoundViolation(value, upperBound)
    Success(())
  }

  private[config] def lowerBoundViolation(
      value: Double,
      lowerBound: Double
  ): Failure[Unit] = Failure(
    ConversionConfigException(
      s"The parameters value: $value lies below the lower bound: $lowerBound"
    )
  )

  private[config] def upperBoundViolation(
      value: Double,
      upperBound: Double
  ): Failure[Unit] = Failure(
    ConversionConfigException(
      s"The parameters value: $value exceeds the upper bound: $upperBound"
    )
  )

  private[config] def lowerUpperBoundViolation(
      min: Double,
      max: Double,
      lowerBound: Double,
      upperBound: Double
  ): Failure[Unit] =
    Failure(
      ConversionConfigException(
        s"The minimum: $min and maximum: $max of the uniform distribution lie below the lower bound: $lowerBound and above the upper bound: $upperBound "
      )
    )

  private[config] def validateQCharacteristic(
      qCharacteristic: QCharacteristic
  ): Try[Unit] = Try(qCharacteristic).flatMap {
    case ConversionConfig.FixedQCharacteristic => Success(())
    case DependentQCharacteristic(characteristic) =>
      Try {
        ReactivePowerCharacteristic.parse(characteristic)
      }.map(_ => ())
  }

  private[config] def validateCpCharacteristic(
      cpCharacteristic: String
  ): Try[Unit] = Try(new WecCharacteristicInput(cpCharacteristic))

}
