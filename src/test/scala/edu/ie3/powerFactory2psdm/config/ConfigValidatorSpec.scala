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
  validatePvParams
}
import edu.ie3.powerFactory2psdm.config.ConversionConfig.{
  Fixed,
  UniformDistribution
}
import edu.ie3.powerFactory2psdm.exception.io.ConversionConfigException
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class ConfigValidatorSpec extends Matchers with AnyWordSpecLike {

  "A ConfigValidator " should {

    val pvParams = ConverterTestData.config.modelConfigs.pvConfig.params

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
      validatePvParams(pvParams)
    }

    "throw an exception for invalid pv param albedo" in {
      val faultyParams = pvParams.copy(albedo = Fixed(1.1))
      val exc =
        intercept[ConversionConfigException](validatePvParams(faultyParams))
      exc.getMessage shouldBe s"The albedo of the plants surrounding: ${faultyParams.albedo} isn't valid. Exception: ${upperBoundViolation(1.1, 1.0).exception.getMessage}"
    }

    "throw an exception for invalid pv param azimuth" in {
      val faultyParams = pvParams.copy(azimuth = UniformDistribution(-91, 92))
      val exc =
        intercept[ConversionConfigException](validatePvParams(faultyParams))
      exc.getMessage shouldBe s"The azimuth of the plant: ${faultyParams.azimuth} isn't valid. Exception: ${lowerUpperBoundViolation(-91, 92, -90, 90).exception.getMessage}"
    }

    "throw an exception for invalid pv param etaConv" in {
      val faultyParams = pvParams.copy(etaConv = Fixed(101))
      val exc =
        intercept[ConversionConfigException](validatePvParams(faultyParams))
      exc.getMessage shouldBe s"The efficiency of the plants inverter: ${faultyParams.azimuth} isn't valid. Exception: ${upperBoundViolation(101, 100).exception.getMessage}"
    }

    "throw an exception for invalid pv param kG" in {
      val faultyParams = pvParams.copy(kG = Fixed(-0.1))
      val exc =
        intercept[ConversionConfigException](validatePvParams(faultyParams))
      exc.getMessage shouldBe s"The PV generator correction factor (kG): ${faultyParams.kG} isn't valid. Exception: ${lowerBoundViolation(-0.1, 0).exception.getMessage}"
    }

    "throw an exception for invalid pv param kT" in {
      val faultyParams = pvParams.copy(kT = Fixed(-0.1))
      val exc =
        intercept[ConversionConfigException](validatePvParams(faultyParams))
      exc.getMessage shouldBe s"The PV temperature correction factor (kT): ${faultyParams.kT} isn't valid. Exception: ${lowerBoundViolation(-0.1, 0).exception.getMessage}"
    }

  }
}