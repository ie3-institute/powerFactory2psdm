/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter

import edu.ie3.powerFactory2psdm.common.ConverterTestData
import edu.ie3.powerFactory2psdm.config.ConversionConfig.FixedQCharacteristic
import edu.ie3.powerFactory2psdm.exception.pf.ConversionException
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class FixedFeedInConverterSpec extends Matchers with AnyWordSpecLike {

  "A fixed feed in converter" should {
    val conversionPair =
      ConverterTestData.getStaticGenerator2FixedFeedInPair("someStatGen")
    val someNode = ConverterTestData.getNodePair("someNode").result
    val input = conversionPair.input
    val expected = conversionPair.result

    "throw an exception if the leading/lagging power factor specifier is neither 0 or 1" in {
      val exc = intercept[ConversionException](
        FixedFeedInConverter.convert(
          input.copy(indCapFlag = 3),
          someNode,
          FixedQCharacteristic
        )
      )
      exc.getMessage shouldBe "The leading/lagging specifier should be either 0 or 1 - I am confused!"
    }

    "convert a static generator correctly" in {
      val actual =
        FixedFeedInConverter.convert(input, someNode, FixedQCharacteristic)
      actual.getId shouldBe expected.getId
      actual.getNode shouldBe expected.getNode
      actual.getsRated shouldBe expected.getsRated
      actual.getqCharacteristics shouldBe expected.getqCharacteristics
      actual.getCosPhiRated shouldBe expected.getCosPhiRated
    }

  }

}
