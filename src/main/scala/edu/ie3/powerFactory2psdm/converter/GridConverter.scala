/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter

import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.powerFactory2psdm.model.Subnet
import edu.ie3.powerFactory2psdm.model.powerfactory.{
  PreprocessedPfGridModel,
  RawPfGridModel
}

/**
  * Functionalities to transform an exported and then parsed PowerFactory grid to the PSDM.
  */
case object GridConverter {

  def convert(pfGrid: RawPfGridModel) = {
    val grid = PreprocessedPfGridModel.build(pfGrid)
    val gridElements = convertGridElements(grid)
  }

  /**
    * Converts the grid elements of the PowerFactory grid
    *
    * @param rawGrid the raw parsed PowerFactoryGrid
    */
  def convertGridElements(
      grid: PreprocessedPfGridModel
  ): Unit = {
    val graph =
      GridGraphBuilder.build(grid.nodes, grid.lines ++ grid.switches)
    val nodeId2node = grid.nodes.map(node => (node.id, node)).toMap
    val subnets = SubnetBuilder.buildSubnets(graph, nodeId2node)
    val psdmNodes = subnets.flatMap(
      subnet => convertNodesOfSubnet(subnet)
    )
  }

  /**
    * Converts all nodes within a subnet to PSDM [[NodeInput]]
    *
    * @param subnet    the subnet with reference to all PF nodes that live within
    * @return list of all converted [[NodeInput]]
    */
  def convertNodesOfSubnet(
      subnet: Subnet
  ): List[NodeInput] =
    subnet.nodes
      .map(node => NodeConverter.convertNode(node, subnet.id, subnet.voltLvl))
      .toList
}
