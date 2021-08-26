package edu.ie3.powerFactory2psdm.config.validate

import edu.ie3.datamodel.models.input.system.characteristic.ReactivePowerCharacteristic
import edu.ie3.powerFactory2psdm.config.ConversionConfig
import edu.ie3.powerFactory2psdm.config.ConversionConfig.StatGenModelConfigs
import edu.ie3.powerFactory2psdm.config.ConversionConfigUtils.{DependentQCharacteristic, FixedQCharacteristic, QCharacteristic}
import edu.ie3.powerFactory2psdm.exception.io.ConversionConfigException
import edu.ie3.powerFactory2psdm.generator.ParameterSamplingMethod
import edu.ie3.powerFactory2psdm.generator.ParameterSamplingMethod.{Fixed, NormalDistribution, UniformDistribution}

import scala.util.{Failure, Success, Try}

object ConfigValidator {

  /** Checks the parsed [[ConversionConfig]] for general soundness.
   *
   * @param config
   * the parsed config
   */
  def validate(config: ConversionConfig): Unit = {
    validateModelConfigs(config.modelConfigs)
  }

  private[config] def validateModelConfigs(
    modelConfigs: StatGenModelConfigs
  ): Unit = {
    PvConfigValidator.validate(modelConfigs.pvConfig)
  }

  private[config] def validateGenerationMethod(
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
    case FixedQCharacteristic => Success(())
    case DependentQCharacteristic(characteristic) =>
      Try {
        ReactivePowerCharacteristic.parse(characteristic)
      }.map(_ => ())
  }

}
