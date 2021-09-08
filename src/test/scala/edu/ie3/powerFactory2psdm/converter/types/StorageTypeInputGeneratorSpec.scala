/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter.types

import edu.ie3.powerFactory2psdm.common.ConverterTestData
import edu.ie3.powerFactory2psdm.generator.types.StorageTypeInputGenerator
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class StorageTypeInputGeneratorSpec extends Matchers with AnyWordSpecLike {

  "A storage generator" should {
    val generationPair =
      ConverterTestData.getGenerateStorageType("someStorage_type")
    val input = generationPair.input
    val generationConfig = generationPair.modelConversionMode

    "generate a storage type correctly" in {
      val expected = generationPair.resultModel
      val actual = StorageTypeInputGenerator.generate(input, generationConfig)
      actual.getId shouldBe expected.getId
      actual.getCapex shouldBe expected.getCapex
      actual.getOpex shouldBe expected.getOpex
      actual.geteStorage shouldBe expected.geteStorage
      actual.getsRated shouldBe expected.getsRated
      actual.getCosPhiRated shouldBe expected.getCosPhiRated
      actual.getpMax shouldBe expected.getpMax
      actual.getActivePowerGradient shouldBe expected.getActivePowerGradient
      actual.getEta shouldBe expected.getEta
      actual.getDod shouldBe expected.getDod
      actual.getLifeTime shouldBe expected.getLifeTime
      actual.getLifeCycle shouldBe expected.getLifeCycle
    }
  }
}
