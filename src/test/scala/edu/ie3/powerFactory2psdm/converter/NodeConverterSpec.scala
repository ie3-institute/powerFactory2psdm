/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter

import edu.ie3.datamodel.models.voltagelevels.GermanVoltageLevelUtils
import edu.ie3.powerFactory2psdm.common.ConverterTestData.getNodePair
import edu.ie3.powerFactory2psdm.config.ConversionConfig.NodeUuidMappingInformation
import edu.ie3.powerFactory2psdm.exception.pf.{
  ConversionException,
  TestException
}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

import java.io.File
import java.util.UUID

class NodeConverterSpec extends Matchers with AnyWordSpecLike {

  "The node converter" should {

    "convert a correctly configured pf node to a correctly configured PSDM Node" in {
      val conversionPair = getNodePair("someNode")
      val input = conversionPair.input
      val expected = conversionPair.result

      val actual = NodeConverter.convertNode(
        input,
        1,
        GermanVoltageLevelUtils.LV,
        Map.empty
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
      val uuid = UUID.fromString("1f085331-b7c5-5c20-aff5-09b3c6a7e0ab")

      val actual = NodeConverter.convertNode(
        input,
        2,
        GermanVoltageLevelUtils.LV,
        Map("someSlackNode" -> uuid)
      )
      actual.getId shouldBe expected.getId
      actual.getVoltLvl shouldBe expected.getVoltLvl
      actual.getSubnet shouldBe expected.getSubnet
      actual.isSlack shouldBe expected.isSlack
      actual.getUuid shouldBe uuid
    }

    "build a node name mapping correctly" in {
      val csvMappingPath =
        s"${new File(".").getCanonicalPath}/src/test/resources/id_mapping.csv"
      val nodeUuidMappingInformation =
        NodeUuidMappingInformation(csvMappingPath, ";")
      val mapping = NodeConverter.getNodeNameMapping(nodeUuidMappingInformation)

      mapping
        .getOrElse(
          "Node-A",
          fail("Map does not contain the given key.")
        )
        .toString shouldBe "15af4d66-e83a-5f3b-a992-2dd240cced81"
      mapping
        .getOrElse(
          "Node-B",
          throw TestException("Map does not contain the given key.")
        )
        .toString shouldBe "d254e50c-638f-5ec1-88d5-0332d13f5d0c"
      mapping
        .getOrElse(
          "Node-C",
          throw TestException("Map does not contain the given key.")
        )
        .toString shouldBe "1f085331-b7c5-5c20-aff5-09b3c6a7e0ab"
    }

    "throw an exception when id->uuid mapping has duplicted ids" in {
      val csvMappingPath =
        s"${new File(".").getCanonicalPath}/src/test/resources/corrupt_id_mapping.csv"
      val nodeUuidMappingInformation =
        NodeUuidMappingInformation(csvMappingPath, ";")
      val exc = intercept[ConversionException](
        NodeConverter.getNodeNameMapping(nodeUuidMappingInformation)
      )
      exc.getMessage shouldBe f"There are the following duplicate ids in the node id to uuid mapping: Vector(Node-A)"
    }
  }
}
