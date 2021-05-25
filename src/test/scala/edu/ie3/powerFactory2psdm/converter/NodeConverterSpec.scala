package edu.ie3.powerFactory2psdm.converter

import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.voltagelevels.VoltageLevel
import edu.ie3.powerFactory2psdm.common.ConverterTestData
import edu.ie3.powerFactory2psdm.exception.pf.TestException
import edu.ie3.powerFactory2psdm.model.Subnet
import edu.ie3.powerFactory2psdm.model.powerfactory.PowerFactoryGrid.ConElms
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import tech.units.indriya.quantity.Quantities

import scala.util.{Failure, Success}

class NodeConverterSpec extends Matchers with ConverterTestData with AnyWordSpecLike{

  "The node converter" should {
    val voltageLevel: VoltageLevel = new VoltageLevel("Hochspannung", Quantities.getQuantity(132, StandardUnits.RATED_VOLTAGE_MAGNITUDE))
    val bus3UUID = pfGridMaps.nodeId2UUID(bus3Id)
    val bus4UUID = pfGridMaps.nodeId2UUID(bus4Id)
    val pfXnetBus = pfGridMaps.UUID2node(pfGridMaps.nodeId2UUID(bus1Id))
    val nodeUUIDs = Set(bus3UUID, bus4UUID)
    val testSubnet = Subnet(2, nodeUUIDs, voltageLevel)

    "convert all Nodes of a given subnet" in {
      NodeConverter.convertNodesOfSubnet(testSubnet, pfGridMaps.UUID2node).size shouldBe 2
    }

    "convert a pf node to a correctly configured PSDM Node" in {
      val convertedNode = NodeConverter.convertNode(pfGridMaps.nodeId2UUID(bus3Id), pfGridMaps.UUID2node, testSubnet)
      convertedNode.getUuid shouldBe bus3UUID
      convertedNode.getId shouldBe bus3Id
      convertedNode.getVoltLvl shouldBe voltageLevel
      convertedNode.getSubnet shouldBe testSubnet.id
      convertedNode.isSlack shouldBe false
    }

    "correctly identify that an external grid is a slack node" in {
      NodeConverter.isSlack(pfXnetBus.conElms) shouldBe Success(true)
    }

    "should throw a failure checking for a slack node if the list of connected elements is empty" in {
      val withEmptyConElms = pfXnetBus.copy(conElms = Some(List()))
      NodeConverter.isSlack(withEmptyConElms.conElms) match {
        case Success(true) => throw TestException("This should not be a slack node!")
        case Failure(ex) => ex.getMessage shouldBe "The list of connected elements is empty."
      }
    }

    "should throw a failure checking for a slack node if the connected elements are None" in {
      val withEmptyConElms = pfXnetBus.copy(conElms = None)
      NodeConverter.isSlack(withEmptyConElms.conElms) match {
        case Success(true) => throw TestException("This should not be a slack node!")
        case Failure(ex) => ex.getMessage shouldBe "The optional connected elements attribute is None."
      }
    }
  }
}
