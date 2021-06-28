/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter

import edu.ie3.powerFactory2psdm.common.ConverterTestData
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class SwitchConverterSpec extends Matchers with AnyWordSpecLike {

  "A switch converter" should {

    val conversionPair = ConverterTestData.getSwitches("someSwitch")
    val nodeA = ConverterTestData.getNodePair("someNode").result
    val nodeB = ConverterTestData.getNodePair("someSlackNode").result
    val nodeId2nodeInput = Map(nodeA.getId -> nodeA, nodeB.getId -> nodeB)

    "convert a switch correctly" in {
      val expected = conversionPair.result
      val actual =
        SwitchConverter.convert(conversionPair.input, nodeId2nodeInput)
      actual.getId shouldBe expected.getId
      actual.getNodeA shouldBe expected.getNodeA
      actual.getNodeB shouldBe expected.getNodeB
      actual.isClosed shouldBe expected.isClosed
    }

  }

}
