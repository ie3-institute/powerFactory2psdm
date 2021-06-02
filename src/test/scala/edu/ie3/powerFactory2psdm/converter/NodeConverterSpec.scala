/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter

import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.voltagelevels.VoltageLevel
import edu.ie3.powerFactory2psdm.common.ConverterTestData
import edu.ie3.powerFactory2psdm.exception.pf.{
  ElementConfigurationException,
  TestException
}
import edu.ie3.powerFactory2psdm.model.Subnet
import edu.ie3.powerFactory2psdm.model.powerfactory.PowerFactoryGrid.ConElms
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import tech.units.indriya.quantity.Quantities

import scala.util.{Failure, Success}

class NodeConverterSpec
    extends Matchers
    with ConverterTestData
    with AnyWordSpecLike {

  "The node converter" should {
    val voltageLevel: VoltageLevel = new VoltageLevel(
      "Hochspannung",
      Quantities.getQuantity(132, StandardUnits.RATED_VOLTAGE_MAGNITUDE)
    )
    val bus3Uuid = pfGridMaps.nodeId2Uuid(bus3Id)
    val bus4Uuid = pfGridMaps.nodeId2Uuid(bus4Id)
    val pfXnetBus = pfGridMaps.uuid2Node(pfGridMaps.nodeId2Uuid(bus1Id))
    val nodeUUIDs = Set(bus3Uuid, bus4Uuid)
    val testSubnet = Subnet(2, nodeUUIDs, voltageLevel)

    "correctly identify that a node connected to an external grid is a slack node" in {
      NodeConverter.isSlack(pfXnetBus.conElms) shouldBe Success(true)
    }

    "should correctly identify that regular nodes aren't slack nodes" in {
      NodeConverter.isSlack(pfGridMaps.uuid2Node(bus3Uuid).conElms) shouldBe Success(
        false
      )
    }

    "should return a failure checking for a slack node if the connected elements are None" in {
      val withEmptyConElms = pfXnetBus.copy(conElms = None)
      NodeConverter.isSlack(withEmptyConElms.conElms) match {
        case Success(true) =>
          throw TestException("This should not be a slack node!")
        case Success(false) =>
          throw TestException("This should have returned a Failure!")
        case Failure(ex) =>
          ex.getMessage shouldBe "The optional connected elements attribute is None."
      }
    }

    "isslack should return a failure if there are more than one external nets connected to a node" in {
      val conElmA = ConElms(Some("conElmA"), Some("ElmXnet"))
      val conElmB = ConElms(Some("conElmB"), Some("ElmXnet"))
      val testConElms = Some(List(Some(conElmA), Some(conElmB)))

      NodeConverter.isSlack(testConElms) match {
        case Success(true) =>
          throw TestException("This should not be a slack node!")
        case Success(false) =>
          throw TestException("This should have returned a Failure!")
        case Failure(ex) =>
          ex.getMessage shouldBe "There is more than one external grid connected to the node."
      }
    }

    "convert a correctly configured pf node to a correctly configured PSDM Node" in {
      val convertedNode = NodeConverter.convertNode(
        bus3Uuid,
        pfGridMaps.uuid2Node,
        testSubnet
      )
      convertedNode.getUuid shouldBe bus3Uuid
      convertedNode.getId shouldBe bus3Id
      convertedNode.getVoltLvl shouldBe voltageLevel
      convertedNode.getSubnet shouldBe testSubnet.id
      convertedNode.isSlack shouldBe false
    }

    "should throw an exception when converting a node if the node has no id" in {
      val bus3NoId = pfGridMaps.uuid2Node(bus3Uuid).copy(id = None)
      val adjustedPfGridMaps = pfGridMaps.uuid2Node.updated(bus3Uuid, bus3NoId)

      val thrown = intercept[ElementConfigurationException](
        NodeConverter.convertNode(
          bus3Uuid,
          adjustedPfGridMaps,
          testSubnet
        )
      )
      thrown.getMessage shouldBe s"The PF node $bus3NoId has no ID"

    }

  }
}
