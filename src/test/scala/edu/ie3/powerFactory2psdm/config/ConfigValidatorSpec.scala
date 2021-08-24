/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.config

import edu.ie3.powerFactory2psdm.common.ConverterTestData
import edu.ie3.powerFactory2psdm.config.ConfigValidator.{
  lowerBoundViolation,
  lowerUpperBoundViolation,
  upperBoundViolation,
  validatePvModelGenerationParams
}
import edu.ie3.powerFactory2psdm.config.ConversionConfig.{
  DependentQCharacteristic,
  Fixed,
  PvFixedFeedIn,
  PvModelGeneration,
  UniformDistribution
}
import edu.ie3.powerFactory2psdm.exception.io.ConversionConfigException
import edu.ie3.powerFactory2psdm.exception.pf.TestException
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class ConfigValidatorSpec extends Matchers with AnyWordSpecLike {

  "A ConfigValidator " should {

    val pvModelGeneration: PvModelGeneration =
      ConverterTestData.config.modelConfigs.pvConfig.conversionMode match {
        case PvFixedFeedIn("cosPhiFixed{(0.0, 0.95)}") =>
          throw TestException(
            "The test pv config is supposed to be configured for PvModelGeneration"
          )
        case x: PvModelGeneration => x
      }

    "validate a conversion config" in {
      ConfigValidator.validate(ConverterTestData.config)
    }

    "validate model configs" in {
      ConfigValidator.validateModelConfigs(
        ConverterTestData.config.modelConfigs
      )
    }

    "validate pv configs" in {
      ConfigValidator.validatePvConfig(
        ConverterTestData.config.modelConfigs.pvConfig
      )
    }

    "validate correct pv params" in {
      validatePvModelGenerationParams(pvModelGeneration)
    }

    "throw an exception for invalid pv param albedo" in {
      val faultyParams = pvModelGeneration.copy(albedo = Fixed(1.1))
      val exc =
        intercept[ConversionConfigException](
          validatePvModelGenerationParams(faultyParams)
        )
      exc.getMessage shouldBe s"The albedo of the plants surrounding: ${faultyParams.albedo} isn't valid. Exception: ${upperBoundViolation(1.1, 1.0).exception.getMessage}"
    }

    "throw an exception for invalid pv param azimuth" in {
      val faultyParams =
        pvModelGeneration.copy(azimuth = UniformDistribution(-91, 92))
      val exc =
        intercept[ConversionConfigException](
          validatePvModelGenerationParams(faultyParams)
        )
      exc.getMessage shouldBe s"The azimuth of the plant: ${faultyParams.azimuth} isn't valid. Exception: ${lowerUpperBoundViolation(-91, 92, -90, 90).exception.getMessage}"
    }

    "throw an exception for min/max error of pv param azimuth" in {
      val faultyParams =
        pvModelGeneration.copy(azimuth = UniformDistribution(20, -10))
      val exc =
        intercept[ConversionConfigException](
          validatePvModelGenerationParams(faultyParams)
        )
      exc.getMessage shouldBe s"The azimuth of the plant: ${faultyParams.azimuth} isn't valid. Exception: The minimum value: 20.0 exceeds the maximum value: -10.0"
    }

    "throw an exception for invalid pv param etaConv" in {
      val faultyParams = pvModelGeneration.copy(etaConv = Fixed(101))
      val exc =
        intercept[ConversionConfigException](
          validatePvModelGenerationParams(faultyParams)
        )
      exc.getMessage shouldBe s"The efficiency of the plants inverter: ${faultyParams.azimuth} isn't valid. Exception: ${upperBoundViolation(101, 100).exception.getMessage}"
    }

    "throw an exception for invalid pv param kG" in {
      val faultyParams = pvModelGeneration.copy(kG = Fixed(-0.1))
      val exc =
        intercept[ConversionConfigException](
          validatePvModelGenerationParams(faultyParams)
        )
      exc.getMessage shouldBe s"The PV generator correction factor (kG): ${faultyParams.kG} isn't valid. Exception: ${lowerBoundViolation(-0.1, 0).exception.getMessage}"
    }

    "throw an exception for invalid pv param kT" in {
      val faultyParams = pvModelGeneration.copy(kT = Fixed(-0.1))
      val exc =
        intercept[ConversionConfigException](
          validatePvModelGenerationParams(faultyParams)
        )
      exc.getMessage shouldBe s"The PV temperature correction factor (kT): ${faultyParams.kT} isn't valid. Exception: ${lowerBoundViolation(-0.1, 0).exception.getMessage}"
    }

    "throw an exception for invalid pv q characteristic" in {
      val faultyQCharacteristic =
        DependentQCharacteristic("cosPhiInv{(0.0, 1), (0.0, 0.8 )}")
      val faultyParams =
        pvModelGeneration.copy(qCharacteristic = faultyQCharacteristic)
      val exc =
        intercept[ConversionConfigException](
          validatePvModelGenerationParams(faultyParams)
        )
      exc.getMessage.startsWith(
        "The PV q characteristic configuration isn't valid. Exception:"
      ) shouldBe true
    }

  }
}
