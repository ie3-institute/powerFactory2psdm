/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter

import edu.ie3.powerFactory2psdm.common.ConverterTestData.{
  id2node,
  subnet1Ids,
  subnet2Ids,
  preProcessedGrid
}
import edu.ie3.powerFactory2psdm.exception.pf.{
  ElementConfigurationException,
  TestException
}
import edu.ie3.powerFactory2psdm.model.entity.Subnet
import org.jgrapht.alg.connectivity.BiconnectivityInspector
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class SubnetBuilderSpec extends Matchers with AnyWordSpecLike {

  "The SubnetBuilder" should {

    val gridGraph = GridGraphBuilder.build(
      preProcessedGrid.nodes,
      preProcessedGrid.lines ++ preProcessedGrid.switches
    )
    val subgraphs =
      new BiconnectivityInspector(gridGraph).getConnectedComponents

    "build a subnet for each subgraph" in {
      SubnetBuilder
        .buildSubnets(gridGraph, id2node)
        .size shouldBe subgraphs.size
    }

    "throw an exception if at least one of the nodes has a deviating nominal voltage" in {
      val nodeId = "Grid.ElmNet\\Bus_0003.ElmTerm"
      val node = id2node.getOrElse(
        nodeId,
        throw TestException(s"No node with id $nodeId in the id2node map")
      )
      val faultyNode = node.copy(nominalVoltage = 131.0)
      val updatedMap = id2node.updated(nodeId, faultyNode)
      intercept[ElementConfigurationException] {
        SubnetBuilder.buildSubnet(1, subnet1Ids, updatedMap)
      }.getMessage shouldBe (s"There are the following divergences from the nominal voltage 132.0 : HashSet($nodeId -> 131.0)")
    }

    "identify the correct voltage level id for the voltage level " in {
      val subnet1: Subnet =
        SubnetBuilder.buildSubnet(1, subnet1Ids, id2node)
      subnet1.voltLvl.getId shouldBe "Hochspannung"
      val subnet2: Subnet =
        SubnetBuilder.buildSubnet(2, subnet2Ids, id2node)
      subnet2.voltLvl.getId shouldBe "Niederspannung"
    }

  }

}
