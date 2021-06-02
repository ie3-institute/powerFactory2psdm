/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.model

import edu.ie3.powerFactory2psdm.common.ConverterTestData
import edu.ie3.powerFactory2psdm.converter.{GridGraphBuilder, SubnetBuilder}
import edu.ie3.powerFactory2psdm.exception.pf.ElementConfigurationException
import org.jgrapht.alg.connectivity.BiconnectivityInspector
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class SubnetBuilderSpec
    extends Matchers
    with ConverterTestData
    with AnyWordSpecLike {

  "The SubnetBuilder" should {

    val gridGraph = GridGraphBuilder.build(pfGridMaps)
    val subgraphs =
      new BiconnectivityInspector(gridGraph).getConnectedComponents

    "build a subnet for each subgraph" in {
      SubnetBuilder
        .buildSubnets(gridGraph, pfGridMaps.uuid2Node)
        .size shouldBe subgraphs.size
    }

    "throw an exception if at least one of the nodes has a deviating nominal voltage" in {
      val nodeId = "Grid.ElmNet\\Bus_0003.ElmTerm"
      val nodeUUID = pfGridMaps.nodeId2Uuid(nodeId)
      val node = pfGridMaps.uuid2Node(nodeUUID)
      val faultyNode = node.copy(uknom = Some(131.0))
      val newMap = pfGridMaps.uuid2Node.updated(nodeUUID, faultyNode)
      intercept[ElementConfigurationException] {
        SubnetBuilder.buildSubnet(1, subnet1Uuids, newMap)
      }.getMessage shouldBe (s"There are the following divergences from the nominal voltage 132.0 : List($nodeId -> 131.0)")
    }

    "identify the correct voltage level id for the voltage level " in {
      val subnet1: Subnet =
        SubnetBuilder.buildSubnet(1, subnet1Uuids, pfGridMaps.uuid2Node)
      subnet1.voltLvl.getId shouldBe "Hochspannung"
      val subnet2: Subnet =
        SubnetBuilder.buildSubnet(2, subnet2Uuids, pfGridMaps.uuid2Node)
      subnet2.voltLvl.getId shouldBe "Niederspannung"
    }

  }

}