package edu.ie3.powerFactory2psdm.converter

import edu.ie3.powerFactory2psdm.common.ConverterTestData
import edu.ie3.powerFactory2psdm.exception.pf.ConversionException
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class PowerPlantConverterSpec extends Matchers with AnyWordSpecLike{

  "A power plant converter" should {
    val conversionPair = ConverterTestData.getPowerPlantPair("somePowerPlant")
    val someNode = ConverterTestData.getNodePair("someNode").result
    val input = conversionPair.input
    val expected = conversionPair.result

    "throw an exception if the leading/lagging power factor specifier is neither 0 or 1" in {
     val exc = intercept[ConversionException](PowerPlantConverter.convert(input.copy(indCap = 3), someNode))
      exc.getMessage shouldBe "The leading/lagging specifier should be either 0 or 1 - I am confused!"
    }

    "convert a power plant correctly" in {
      val actual = PowerPlantConverter.convert(input, someNode)
      actual.getId shouldBe expected.getId
      actual.getNode shouldBe expected.getNode
      actual.getsRated shouldBe expected.getsRated
      actual.getqCharacteristics shouldBe expected.getqCharacteristics
      actual.getCosPhiRated shouldBe expected.getCosPhiRated
    }

  }

}
