/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter

import edu.ie3.datamodel.models.voltagelevels.GermanVoltageLevelUtils
import edu.ie3.powerFactory2psdm.common.ConverterTestData.getNodePair
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class NodeConverterSpec extends Matchers with AnyWordSpecLike {

  "The node converter" should {

    "convert a correctly configured pf node to a correctly configured PSDM Node" in {
      val conversionPair = getNodePair("someNode")
      val input = conversionPair.input
      val expected = conversionPair.result

      val actual = NodeConverter.convertNode(
        input,
        1,
        GermanVoltageLevelUtils.LV
      )
      actual.getId shouldBe expected.getId
      actual.getVoltLvl shouldBe expected.getVoltLvl
      actual.getSubnet shouldBe expected.getSubnet
      actual.isSlack shouldBe expected.isSlack
    }

    "convert a correctly configured slack pf node to a correctly configured slack PSDM Node" in {
      val conversionPair = getNodePair("someSlackNode")
      val input = conversionPair.input
      val expected = conversionPair.result

      val actual = NodeConverter.convertNode(
        input,
        2,
        GermanVoltageLevelUtils.LV
      )
      actual.getId shouldBe expected.getId
      actual.getVoltLvl shouldBe expected.getVoltLvl
      actual.getSubnet shouldBe expected.getSubnet
      actual.isSlack shouldBe expected.isSlack
    }
  }
}
