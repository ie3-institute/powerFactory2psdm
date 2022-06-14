/**
 * © 2021. Johannes Hiry,
 **/

package edu.ie3.powerFactory2psdm.generator

import edu.ie3.powerFactory2psdm.common.ConverterTestData.{
  generateWecs,
  getGenerateWecPair,
  getNodePair,
  wecModelGeneration
}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class WecInputGeneratorSpec extends Matchers with AnyWordSpecLike {

  "A wec generator" should {
    val generationPair = getGenerateWecPair("someWec")
    val input = generationPair.input
    val modelGenerationConfig = wecModelGeneration
    val node = getNodePair("someNode").result

    "generate a WecInput correctly" in {
      val expected = generationPair.resultModel
      val actual =
        WecInputGenerator.generate(input, node, modelGenerationConfig)

      actual.getId shouldBe expected.getId
      actual.getNode shouldBe expected.getNode
    }

    "generate a WecTypeInput correctly" in {
      val expected = generationPair.resultType
      val actual =
        WecInputGenerator.generate(input, node, modelGenerationConfig).getType

      actual.getsRated shouldBe expected.getsRated
      actual.getEtaConv shouldBe expected.getEtaConv
      actual.getRotorArea shouldBe expected.getRotorArea
      actual.getCosPhiRated shouldBe expected.getCosPhiRated
      actual.getHubHeight shouldBe expected.getHubHeight
      actual.getCpCharacteristic shouldBe expected.getCpCharacteristic
      actual.getOpex shouldBe expected.getOpex
      actual.getCapex shouldBe expected.getCapex

    }

  }

}
