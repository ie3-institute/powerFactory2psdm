/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.generator

import edu.ie3.powerFactory2psdm.common.ConverterTestData
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class StorageInputGeneratorSpec extends Matchers with AnyWordSpecLike {
  "A storage generator" should {
    val generationPair = ConverterTestData.getGenerateStorage("someStorage")
    val input = generationPair.input
    val generationConfig = generationPair.modelConversionMode
    val node = ConverterTestData.getNodePair("someNode").result
    "generate a storage entity correctly" in {
      val expected = generationPair.resultModel
      val actual =
        StorageInputGenerator.generate(input, node, generationConfig)._1
      actual.getNode shouldBe expected.getNode
      actual.getqCharacteristics shouldBe expected.getqCharacteristics
    }
  }
}
