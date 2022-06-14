/**
 * © 2021. Johannes Hiry,
 **/

package edu.ie3.powerFactory2psdm.converter

import edu.ie3.powerFactory2psdm.common.ConverterTestData
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class Transfomer2WConverterSpec extends Matchers with AnyWordSpecLike {

  "A 2w transformer converter" should {
    val trafoPair = ConverterTestData.getTransformer2WPair("someTransformer2W")
    val nodeA = ConverterTestData.getNodePair("someMvNode").result
    val nodeB = ConverterTestData.getNodePair("someNode").result
    val trafoType =
      ConverterTestData.getTransformer2WTypePair("10 -> 0.4").result

    "convert a transformer correctly" in {
      val expected = trafoPair.result
      val actual =
        Transformer2WConverter.convert(trafoPair.input, nodeA, nodeB, trafoType)

      actual.getId shouldBe expected.getId
      actual.getOperator shouldBe expected.getOperator
      actual.getOperationTime shouldBe expected.getOperationTime
      actual.getNodeA shouldBe expected.getNodeA
      actual.getNodeB shouldBe expected.getNodeB
      actual.getParallelDevices shouldBe expected.getParallelDevices
      actual.getType shouldBe expected.getType
      actual.getTapPos shouldBe expected.getTapPos
      actual.isAutoTap shouldBe expected.isAutoTap

    }

  }

}
