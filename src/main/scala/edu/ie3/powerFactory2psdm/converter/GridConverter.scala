/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter

import edu.ie3.powerFactory2psdm.config.ConversionConfig
import edu.ie3.powerFactory2psdm.config.ConversionConfig.StatGenModelConfigs
import edu.ie3.powerFactory2psdm.converter.types.{
  LineTypeConverter,
  Transformer2WTypeConverter
}
import edu.ie3.powerFactory2psdm.model.{PreprocessedPfGridModel, RawPfGridModel}

/** Functionalities to transform an exported and then parsed PowerFactory grid
  * to the PSDM.
  */
case object GridConverter {

  def convert(pfGrid: RawPfGridModel, config: ConversionConfig) = {
    val grid = PreprocessedPfGridModel.build(
      pfGrid,
      config.modelConfigs.sRatedSource,
      config.modelConfigs.cosPhiSource
    )
    val gridElements = convertGridElements(grid)
  }

  /** Converts the grid elements of the PowerFactory grid
    *
    * @param grid
    *   the raw parsed PowerFactoryGrid
    */
  def convertGridElements(
      grid: PreprocessedPfGridModel,
      statGenConversionConfig: StatGenModelConfigs
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
    val transformer2WTypes = grid.transformerTypes2W
      .map(transformer =>
        transformer.id -> Transformer2WTypeConverter.convert(transformer)
      )
      .toMap
    val transfomers2W = Transformer2WConverter.convertTransformers(
      grid.transformers2W,
      nodes,
      transformer2WTypes
    )
    val loads = LoadConverter.convertLoads(grid.loads, nodes)
    val statGenModels = ???
  }
}
