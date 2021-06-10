/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter

import edu.ie3.powerFactory2psdm.common.ConverterTestData.{
  getNodePair,
  getSubnet
}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class NodeConverterSpec extends Matchers with AnyWordSpecLike {

  "The node converter" should {

    val conversionPair = getNodePair("someNode")
    val input = conversionPair.input
    val expected = conversionPair.result

    "correctly identify that a node connected to an external grid is a slack node" in {
      NodeConverter.isSlack(getNodePair("someSlackNode").input) shouldBe true
    }

    "should correctly identify that regular nodes aren't slack nodes" in {
      NodeConverter.isSlack(input) shouldBe false
    }

    "convert a correctly configured pf node to a correctly configured PSDM Node" in {
      val actual = NodeConverter.convertNode(
        "someNode",
        Map("someNode" -> input),
        getSubnet("someSubnet")
      )
      actual.getId shouldBe expected.getId
      actual.getVoltLvl shouldBe expected.getVoltLvl
      actual.getSubnet shouldBe expected.getSubnet
      actual.isSlack shouldBe expected.isSlack
    }

  }
}
