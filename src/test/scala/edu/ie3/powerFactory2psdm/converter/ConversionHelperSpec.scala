/**
 * © 2021. Johannes Hiry,
 **/

package edu.ie3.powerFactory2psdm.converter

import edu.ie3.datamodel.models.input.system.characteristic.ReactivePowerCharacteristic
import edu.ie3.powerFactory2psdm.common.ConverterTestData
import edu.ie3.powerFactory2psdm.config.ConversionConfigUtils.{
  DependentQCharacteristic,
  FixedQCharacteristic
}
import edu.ie3.powerFactory2psdm.exception.pf.ElementConfigurationException
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class ConversionHelperSpec extends Matchers with AnyWordSpecLike {

  "A conversion helper" when {

    "converting a q characteristic" should {
      val cosPhi = 0.95

      "return a correct fixed reactive power characteristic " in {
        ConversionHelper.convertQCharacteristic(
          FixedQCharacteristic,
          cosPhi
        ) shouldBe ReactivePowerCharacteristic.parse(
          s"cosPhiFixed:{(0.0, 0.95)}"
        )
      }

      "return a correct dependant reactive power characteristic" in {
        val characteristicString =
          "qV:{(0.92, -1),(0.97, 0.0),(1.03, 0.0),(1.08, 1.0)}"
        val characteristic = DependentQCharacteristic(characteristicString)
        ConversionHelper.convertQCharacteristic(
          characteristic,
          cosPhi
        ) shouldBe ReactivePowerCharacteristic.parse(characteristicString)
      }
    }

    "determining cos phi rated of a static generator" should {
      val input = ConverterTestData.staticGenerator
      "calculate cos phi correctly " in {
        ConversionHelper.determineCosPhiRated(
          input.copy(indCapFlag = 0)
        ) shouldBe input.cosPhi
        ConversionHelper.determineCosPhiRated(
          input.copy(indCapFlag = 1)
        ) shouldBe -input.cosPhi
      }

      "throw an exception if the inductive capacitive specifier is neither 0 nor 1" in {
        val exc = intercept[ElementConfigurationException](
          ConversionHelper.determineCosPhiRated(input.copy(indCapFlag = 2))
        )
        exc.getMessage.startsWith(
          s"Can't determine cos phi rated for static generator: ${input.id}."
        ) shouldBe true
      }

      "determines all duplicates of a sequence" in {
        ConversionHelper.getDuplicates(Seq(1, 2, 3)) shouldBe Seq.empty
        ConversionHelper.getDuplicates(Seq("abc", "ab", "abc")) shouldBe Seq(
          "abc"
        )
      }
    }
  }
}
