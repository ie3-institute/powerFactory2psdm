/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.config.validate.conversion

import edu.ie3.powerFactory2psdm.common.ConverterTestData
import edu.ie3.powerFactory2psdm.config.ConversionConfigUtils.{
  DependentQCharacteristic,
  FixedQCharacteristic
}
import edu.ie3.powerFactory2psdm.config.model.PvConversionConfig.{
  PvFixedFeedIn,
  PvModelGeneration
}
import edu.ie3.powerFactory2psdm.config.validate.ConfigValidator
import edu.ie3.powerFactory2psdm.config.validate.ConfigValidator.{
  lowerBoundViolation,
  lowerUpperBoundViolation,
  upperBoundViolation
}
import edu.ie3.powerFactory2psdm.config.validate.conversion.ConversionModeValidators.WecConversionModeValidator.validateModelGenerationParams
import edu.ie3.powerFactory2psdm.config.validate.conversion.ConversionModeValidators.{
  PvConversionModeValidator,
  WecConversionModeValidator
}
import edu.ie3.powerFactory2psdm.exception.io.ConversionConfigException
import edu.ie3.powerFactory2psdm.exception.pf.TestException
import edu.ie3.powerFactory2psdm.generator.ParameterSamplingMethod.{
  Fixed,
  UniformDistribution
}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class ConversionModeValidatorsSpec extends Matchers with AnyWordSpecLike {

  "From the ConversionModeValidators " when {

    "checking the pv model conversion modes" should {
      val pvModelGeneration: PvModelGeneration =
        ConverterTestData.config.modelConfigs.pvConfig.conversionMode match {
          case PvFixedFeedIn(FixedQCharacteristic) =>
            throw TestException(
              "The test pv config is supposed to be configured for PvModelGeneration"
            )
          case x: PvModelGeneration => x
        }

      "validate a conversion config" in {
        ConfigValidator.validateConversionConfig(ConverterTestData.config)
      }

      "validate model configs" in {
        ConfigValidator.validateModelConfigs(
          ConverterTestData.config.modelConfigs
        )
      }

      "validate correct pv params" in {
        PvConversionModeValidator.validate(pvModelGeneration)
      }

      "throw an exception for invalid pv param albedo" in {
        val faultyParams = pvModelGeneration.copy(albedo = Fixed(1.1))
        val exc =
          intercept[ConversionConfigException](
            PvConversionModeValidator.validate(faultyParams)
          )
        exc shouldBe ConversionConfigException(
          s"The albedo of the plants surrounding: ${faultyParams.albedo} isn't valid.",
          upperBoundViolation(1.1, 1.0).exception
        )
      }

      "throw an exception for invalid pv param azimuth" in {
        val faultyParams =
          pvModelGeneration.copy(azimuth = UniformDistribution(-91, 92))
        val exc =
          intercept[ConversionConfigException](
            PvConversionModeValidator.validate(faultyParams)
          )
        exc shouldBe ConversionConfigException(
          s"The azimuth of the plant: ${faultyParams.azimuth} isn't valid.",
          lowerUpperBoundViolation(-91, 92, -90, 90).exception
        )
      }

      "throw an exception for min/max error of pv param azimuth" in {
        val faultyParams =
          pvModelGeneration.copy(azimuth = UniformDistribution(20, -10))
        val exc =
          intercept[ConversionConfigException](
            PvConversionModeValidator.validate(faultyParams)
          )
        exc.getMessage + exc.getCause.getMessage shouldBe s"The azimuth of the plant: ${faultyParams.azimuth} isn't valid.The minimum value: 20.0 exceeds the maximum value: -10.0"
      }

      "throw an exception for invalid pv param etaConv" in {
        val faultyParams = pvModelGeneration.copy(etaConv = Fixed(101))
        val exc =
          intercept[ConversionConfigException](
            PvConversionModeValidator.validate(faultyParams)
          )
        exc shouldBe ConversionConfigException(
          s"The efficiency of the plants inverter: ${faultyParams.azimuth} isn't valid.",
          upperBoundViolation(101, 100).exception
        )
      }

      "throw an exception for invalid pv param kG" in {
        val faultyParams = pvModelGeneration.copy(kG = Fixed(-0.1))
        val exc =
          intercept[ConversionConfigException](
            PvConversionModeValidator.validate(faultyParams)
          )
        exc shouldBe ConversionConfigException(
          s"The PV generator correction factor (kG): ${faultyParams.kG} isn't valid.",
          lowerBoundViolation(-0.1, 0).exception
        )
      }

      "throw an exception for invalid pv param kT" in {
        val faultyParams = pvModelGeneration.copy(kT = Fixed(-0.1))
        val exc =
          intercept[ConversionConfigException](
            PvConversionModeValidator.validate(faultyParams)
          )
        exc shouldBe ConversionConfigException(
          s"The PV temperature correction factor (kT): ${faultyParams.kT} isn't valid.",
          lowerBoundViolation(-0.1, 0).exception
        )
      }

      "throw an exception for invalid pv q characteristic" in {
        val faultyQCharacteristic =
          DependentQCharacteristic("cosPhiInv{(0.0, 1), (0.0, 0.8 )}")
        val faultyParams =
          pvModelGeneration.copy(qCharacteristic = faultyQCharacteristic)
        val exc =
          intercept[ConversionConfigException](
            PvConversionModeValidator.validate(faultyParams)
          )
        exc.getMessage.startsWith(
          "The PV q characteristic configuration isn't valid."
        ) shouldBe true
      }
    }

    "checking the WEC model generation configuration" should {
      val wecModelGeneration = ConverterTestData.wecModelGeneration

      "validate a correct WEC model configuration" in {
        WecConversionModeValidator.validate(wecModelGeneration)
      }

      "throw an exception for invalid WEC param capex" in {
        val faultyValue = -10
        val faultyParams = wecModelGeneration.copy(capex = Fixed(faultyValue))
        val exc =
          intercept[ConversionConfigException](
            WecConversionModeValidator.validate(faultyParams)
          )
        exc shouldBe ConversionConfigException(
          s"The WECs capital expenditure: ${faultyParams.capex} isn't valid.",
          lowerBoundViolation(faultyValue, 0).exception
        )
      }

      "throw an exception for invalid WEC param opex" in {
        val faultyValue = -10
        val faultyParams = wecModelGeneration.copy(opex = Fixed(faultyValue))
        val exc =
          intercept[ConversionConfigException](
            WecConversionModeValidator.validate(faultyParams)
          )
        exc shouldBe ConversionConfigException(
          s"The WECs operational expenditure: ${faultyParams.opex} isn't valid.",
          lowerBoundViolation(faultyValue, 0).exception
        )
      }

      "throw an exception for invalid WEC param etaConv" in {
        val faultyValue = -10
        val faultyParams = wecModelGeneration.copy(etaConv = Fixed(faultyValue))
        val exc =
          intercept[ConversionConfigException](
            WecConversionModeValidator.validate(faultyParams)
          )
        exc shouldBe ConversionConfigException(
          s"The WECs efficiency of the plants inverter: ${faultyParams.etaConv} isn't valid.",
          lowerBoundViolation(faultyValue, 0).exception
        )
      }

      "throw an exception for invalid WEC param hubHeight" in {
        val faultyValue = -10
        val faultyParams =
          wecModelGeneration.copy(hubHeight = Fixed(faultyValue))
        val exc =
          intercept[ConversionConfigException](
            WecConversionModeValidator.validate(faultyParams)
          )
        exc shouldBe ConversionConfigException(
          s"The WECs hub height: ${faultyParams.hubHeight} isn't valid.",
          lowerBoundViolation(faultyValue, 0).exception
        )
      }

      "throw an exception for invalid WEC param rotorArea" in {
        val faultyValue = -10
        val faultyParams =
          wecModelGeneration.copy(rotorArea = Fixed(faultyValue))
        val exc =
          intercept[ConversionConfigException](
            WecConversionModeValidator.validate(faultyParams)
          )
        exc shouldBe ConversionConfigException(
          s"The WECs rotorArea: ${faultyParams.rotorArea} isn't valid.",
          lowerBoundViolation(faultyValue, 0).exception
        )
      }

      "throw an exception for invalid WEC param qCharacteristic" in {
        val faultyParams = wecModelGeneration.copy(qCharacteristic =
          DependentQCharacteristic("cosPhiInv{(0.0, 1), (0.0, 0.8 )}")
        )
        val exc =
          intercept[ConversionConfigException](
            validateModelGenerationParams(faultyParams)
          )
        exc.getMessage.startsWith(
          "The WEC q characteristic configuration isn't valid."
        ) shouldBe true
      }

      "throw an exception for invalid WEC param cpCharacteristic" in {
        val faultyParams =
          wecModelGeneration.copy(cpCharacteristic = "I am a cpCharacteristic.")
        val exc =
          intercept[ConversionConfigException](
            validateModelGenerationParams(faultyParams)
          )
        exc.getMessage.startsWith(
          "The WECs cpCharacteristic configuration isn't valid."
        ) shouldBe true
      }

    }

  }
}
