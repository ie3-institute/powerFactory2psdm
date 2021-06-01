/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter

import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.powerFactory2psdm.model.Subnet
import edu.ie3.powerFactory2psdm.model.powerfactory.PowerFactoryGrid.Nodes
import edu.ie3.powerFactory2psdm.model.powerfactory.{
  PowerFactoryGrid,
  PowerFactoryGridMaps
}
import edu.ie3.powerFactory2psdm.util.GridPreparator

import java.util.UUID

/**
  * Functionalities to transform an exported and then parsed PowerFactory grid to the PSDM.
  */
case object GridConverter {

  def convert(pfGrid: PowerFactoryGrid) = {
    val gridElements = convertGridElements(pfGrid)
  }

  /**
    * Converts the grid elements of the PowerFactory grid
    *
    * @param rawPfGrid the raw parsed PowerFactoryGrid
    */
  def convertGridElements(rawPfGrid: PowerFactoryGrid): Unit = {
    val pfGrid = GridPreparator.prepare(rawPfGrid)
    val pfGridMaps = PowerFactoryGridMaps(pfGrid)
    val graph = GridGraphBuilder.build(pfGridMaps)
    val subnets = SubnetBuilder.buildSubnets(graph, pfGridMaps.uuid2Node)
    val psdmNodes = subnets.flatMap(
      subnet => convertNodesOfSubnet(subnet, pfGridMaps.uuid2Node)
    )
  }

  /**
    * Converts all nodes within a subnet to PSDM [[NodeInput]]
    *
    * @param subnet    the subnet with reference to all PF nodes that live within
    * @param uuid2node map that connects uuids with the associate PF [[Nodes]]
    * @return list of all converted [[NodeInput]]
    */
  def convertNodesOfSubnet(
      subnet: Subnet,
      uuid2node: Map[UUID, Nodes]
  ): List[NodeInput] =
    subnet.nodeUuids
      .map(nodeUuid => NodeConverter.convertNode(nodeUuid, uuid2node, subnet))
      .toList

}
