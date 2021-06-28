/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter

import edu.ie3.powerFactory2psdm.common.ConverterTestData.{
  getLinePair,
  getLineTypePair,
  getNodePair
}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

import java.util.Objects

class LineConverterSpec extends Matchers with AnyWordSpecLike {
  "A line converter" should {

    val conversionPair = getLinePair("someLine")
    val lineType = getLineTypePair("someLineType").result
    val nodeA = getNodePair("someNode").result
    val nodeB = getNodePair("someSlackNode").result

    val actual = LineConverter.convert(
      conversionPair.input,
      lineType,
      nodeA,
      nodeB
    )
    val expected = conversionPair.result

    "convert a correctly configured line properly" in {
      Objects.nonNull(actual.getUuid) shouldBe true
      actual.getId shouldBe expected.getId
      actual.getNodeA shouldBe expected.getNodeA
      actual.getNodeB shouldBe expected.getNodeB
      actual.getType shouldBe expected.getType
      actual.getLength shouldBe expected.getLength
      actual.getOlmCharacteristic shouldBe expected.getOlmCharacteristic
      actual.getParallelDevices shouldBe expected.getParallelDevices
      actual.getOperationTime shouldBe expected.getOperationTime
      actual.getOperator shouldBe expected.getOperator
      actual.getGeoPosition shouldBe expected.getGeoPosition

    }
  }
}
