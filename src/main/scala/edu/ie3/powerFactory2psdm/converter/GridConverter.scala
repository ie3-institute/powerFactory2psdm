/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter

import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.connector.LineInput
import edu.ie3.datamodel.models.input.connector.`type`.LineTypeInput
import edu.ie3.powerFactory2psdm.converter.types.LineTypeConverter
import edu.ie3.powerFactory2psdm.exception.pf.ConversionException
import edu.ie3.powerFactory2psdm.model.Subnet
import edu.ie3.powerFactory2psdm.model.powerfactory.{GridModel, Line, LineType, Node, RawGridModel}

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
    val nodeId2node = grid.nodes.map(node => (node.id, node)).toMap
    val subnets = SubnetBuilder.buildSubnets(graph, nodeId2node)
    val nodes = convertNodes(subnets)
    val lineTypes = grid.lineTypes
      .map(lineType => (lineType.id, LineTypeConverter.convert(lineType)))
      .toMap
    val lines = convertLines(grid.lines, nodes, lineTypes)
  }


  /**
   * Convert all nodes subnet by subnet.
   *
   * @param subnets subnets of the grid
   * @return Map of node id to PSDM [[NodeInput]]
   */
  def convertNodes(subnets: List[Subnet]): Map[String, NodeInput] = {
    subnets
      .flatMap(
        subnet => convertNodesOfSubnet(subnet)
      )
      .toMap
  }


  def convertNodesOfSubnet(
      subnet: Subnet
  ): Set[(String, NodeInput)] =
    subnet.nodes
      .map(
        node =>
          (node.id, NodeConverter.convertNode(node, subnet.id, subnet.voltLvl))
      )

  /**
   * Converts lines to PSDM [[LineInput]]
   *
   * @param lines the lines to be converted
   * @param nodes a map of node ids to nodes
   * @param lineTypes a map of line type ids to line types
   * @return a list of all converted Lines
   */
  def convertLines(
      lines: List[Line],
      nodes: Map[String, NodeInput],
      lineTypes: Map[String, LineTypeInput]
  ): List[LineInput] = {
    lines.map(line => {
      LineConverter.convert(
        line,
        lineTypes.getOrElse(
          line.typId,
          throw ConversionException(
            s"Can't find line type ${line.typId} within the converted line types."
          )
        ),
        nodes.getOrElse(
          line.id,
          throw ConversionException(
            s"Can't find node ${line.nodeAId} within the converted nodes."
          )
        ),
        nodes.getOrElse(
          line.id,
          throw ConversionException(
            s"Can't find node ${line.nodeBId} within the converted nodes."
          )
        )
      )
    })

  }

}
