/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter

import edu.ie3.powerFactory2psdm.converter.types.LineTypeConverter
import edu.ie3.powerFactory2psdm.model.{PreprocessedPfGridModel, RawPfGridModel}

/** Functionalities to transform an exported and then parsed PowerFactory grid
  * to the PSDM.
  */
case object GridConverter {

  def convert(pfGrid: RawPfGridModel) = {
    val grid = PreprocessedPfGridModel.build(pfGrid)
    val gridElements = convertGridElements(grid)
  }

  /** Converts the grid elements of the PowerFactory grid
    *
    * @param rawGrid
    *   the raw parsed PowerFactoryGrid
    */
  def convertGridElements(
      grid: PreprocessedPfGridModel
  ): Unit = {
    val graph =
      GridGraphBuilder.build(grid.nodes, grid.lines ++ grid.switches)
    val nodeId2node = grid.nodes.map(node => (node.id, node)).toMap
    val subnets = SubnetBuilder.buildSubnets(graph, nodeId2node)
    val nodes = NodeConverter.convertNodesOfSubnets(subnets)
    val lineTypes = grid.lineTypes
      .map(lineType => (lineType.id, LineTypeConverter.convert(lineType)))
      .toMap
    val lines = LineConverter.convertLines(
      grid.lines,
      grid.conversionPrefixes.lineLengthPrefix(),
      nodes,
      lineTypes
    )
  }
}
