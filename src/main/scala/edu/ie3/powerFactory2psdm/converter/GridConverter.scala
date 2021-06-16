/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter

import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.powerFactory2psdm.model.Subnet
import edu.ie3.powerFactory2psdm.model.powerfactory.{
  GridModel,
  Node,
  RawGridModel
}

/**
  * Functionalities to transform an exported and then parsed PowerFactory grid to the PSDM.
  */
case object GridConverter {

  def convert(pfGrid: RawGridModel) = {
    val gridElements = convertGridElements(pfGrid)
  }

  /**
    * Converts the grid elements of the PowerFactory grid
    *
    * @param rawGrid the raw parsed PowerFactoryGrid
    */
  def convertGridElements(rawGrid: RawGridModel): Unit = {
    val grid = GridModel.build(rawGrid)
    val graph =
      GridGraphBuilder.build(grid.nodes, grid.lines ++ grid.switches)
    val id2pfNodes = grid.nodes.map(node => (node.id, node)).toMap
    val subnets = SubnetBuilder.buildSubnets(graph, id2pfNodes)
    val id2psdmNodes = convertNodes(subnets, id2pfNodes)
  }

  /**
    * Converts [[Node]]s to PSDM [[NodeInput]]
    * @param subnets subnet data
    * @param id2pfnode map of all pf nodes
    * @return map of all PSDM nodes
    */
  def convertNodes(
      subnets: List[Subnet],
      id2pfnode: Map[String, Node]
  ): Map[String, NodeInput] =
    subnets.flatMap(subnet => convertNodesOfSubnet(subnet, id2pfnode)).toMap

  /**
    * Converts all nodes within a subnet to PSDM [[NodeInput]]
    *
    * @param subnet    the subnet with reference to all PF nodes that live within
    * @param id2node map that connects uuids with the associate PF [[Nodes]]
    * @return map containing all converted [[NodeInput]]
    */
  def convertNodesOfSubnet(
      subnet: Subnet,
      id2node: Map[String, Node]
  ): Map[String, NodeInput] =
    subnet.nodeIds
      .map(
        nodeId => (nodeId, NodeConverter.convertNode(nodeId, id2node, subnet))
      )
      .toMap

}
