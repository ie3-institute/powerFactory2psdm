/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter

import edu.ie3.powerFactory2psdm.common.ConverterTestData
import edu.ie3.powerFactory2psdm.common.ConverterTestData.{
  getNodePair,
  getSampledPvPair
}
import edu.ie3.powerFactory2psdm.config.ConversionConfig
import edu.ie3.powerFactory2psdm.config.ConversionConfig.{
  PvFixedFeedIn,
  PvModelGeneration
}
import edu.ie3.powerFactory2psdm.exception.pf.{
  ElementConfigurationException,
  TestException
}
import edu.ie3.powerFactory2psdm.generator.PvInputGenerator
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class PvInputGeneratorSpec extends Matchers with AnyWordSpecLike {

  "A PvConverter" should {
    val conversionPair = getSampledPvPair("somePvPlant")
    val input = conversionPair.input
    val node = getNodePair("someNode").result
    val expected = conversionPair.result
    val params = ConverterTestData.pvModelGeneration

    "convert a static generator correctly" in {
      val actual = PvInputGenerator.generate(
        input,
        node,
        params
      )
      actual.getId shouldBe expected.getId
      actual.getNode shouldBe expected.getNode
      actual.getsRated shouldBe expected.getsRated
      actual.getCosPhiRated shouldBe expected.getCosPhiRated
    }

    "throw an exception if the inductiva capacitive specifier is neither 0 nor 1" in {
      val exc = intercept[ElementConfigurationException](
        PvInputGenerator.generate(
          input.copy(indCapFlag = 2),
          node,
          params
        )
      )
      exc.getMessage shouldBe s"The inductive capacitive specifier of the static generator: ${input.id} should be either 0 or 1"
    }

  }

}
