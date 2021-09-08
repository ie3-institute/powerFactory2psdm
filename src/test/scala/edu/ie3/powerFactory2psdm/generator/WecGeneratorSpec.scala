/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.generator

import edu.ie3.powerFactory2psdm.common.ConverterTestData.{
  generateWecs,
  getGenerateWecPair,
  getNodePair,
  wecModelGeneration
}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class WecGeneratorSpec extends Matchers with AnyWordSpecLike {

  "A wec generator" should {
    val generationPair = getGenerateWecPair("someWec")
    val input = generationPair.input
    val modelGenerationConfig = wecModelGeneration
    val node = getNodePair("someNode").result

    "generate a WecInput correctly" in {
      val expected = generationPair.resultModel
      val actual = WecGenerator.generate(input, node, modelGenerationConfig)._1

      actual.getId shouldBe expected.getId
      actual.getNode shouldBe expected.getNode
    }

    "generate a WecTypeInput correctly" in {
      val expected = generationPair.resultType
      val actual = WecGenerator.generate(input, node, modelGenerationConfig)._2

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
