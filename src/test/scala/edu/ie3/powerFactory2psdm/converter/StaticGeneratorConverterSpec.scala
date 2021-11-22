package edu.ie3.powerFactory2psdm.converter

import edu.ie3.powerFactory2psdm.common.ConverterTestData
import edu.ie3.powerFactory2psdm.config.ConversionConfigUtils.FixedQCharacteristic
import edu.ie3.powerFactory2psdm.config.model.PvConversionConfig.{IndividualPvConfig, PvFixedFeedIn}
import edu.ie3.powerFactory2psdm.config.model.WecConversionConfig.{IndividualWecConfig, WecFixedFeedIn}
import edu.ie3.powerFactory2psdm.model.entity.StaticGenerator.StatGenCategories.{PV, WEC}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class StaticGeneratorConverterSpec extends Matchers with AnyWordSpecLike {
  "A static generator converter" should {
    "convert a list of static generators correctly" in {
      val conversionPair =  ConverterTestData.getStaticGenerator2FixedFeedInPair("someStatGen")
      val statGen = conversionPair.input
      val statGenModelConfigs = ConverterTestData.config.modelConfigs

      val inputWecModel = statGen.copy(id = "WecModel", category = WEC)
      val inputWecFixedFeedIn = statGen.copy(id = "WecFixedFeedIn", category = WEC)
      val individualWecConfig = IndividualWecConfig(Set("WecFixedFeedIn"), WecFixedFeedIn(FixedQCharacteristic))
      val wecConversionConfig = statGenModelConfigs.wecConfig.copy(individualConfigs = Some(List(individualWecConfig)))

      val inputPvModel = statGen.copy(id = "PvModel", category = PV)
      val inputPvFixedFeedIn = statGen.copy(id = "PvFixedFeedIn", category = PV)
      val individualPvConfig = IndividualPvConfig(Set("PvFixedFeedIn"), PvFixedFeedIn(FixedQCharacteristic))
      val pvConversionConfig = statGenModelConfigs.pvConfig.copy(individualConfigs = Some(List(individualPvConfig)))

      val inputs = List(statGen, inputWecModel, inputWecFixedFeedIn, inputPvModel, inputPvFixedFeedIn)
        val node = ConverterTestData.getNodePair("someNode").result
        val nodesMap = Map("someNode" -> node)

      val actual = StaticGeneratorConverter.convert(inputs, statGenModelConfigs.copy(pvConfig = pvConversionConfig, wecConfig = wecConversionConfig), nodesMap)

      actual.fixedFeedIns.size shouldBe 2
      actual.wecInputs.size shouldBe 1
      actual.pvInputs.size shouldBe 1

    }
  }
}
