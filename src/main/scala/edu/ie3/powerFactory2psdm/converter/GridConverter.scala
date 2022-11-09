/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter

import edu.ie3.datamodel.models.input.{MeasurementUnitInput, NodeInput}
import edu.ie3.datamodel.models.input.connector.Transformer3WInput
import edu.ie3.datamodel.models.input.container.{
  GraphicElements,
  JointGridContainer,
  RawGridElements,
  SystemParticipants
}
import edu.ie3.datamodel.models.input.graphics.{
  LineGraphicInput,
  NodeGraphicInput
}
import edu.ie3.datamodel.models.input.system.{
  BmInput,
  ChpInput,
  EvInput,
  EvcsInput,
  HpInput,
  StorageInput
}
import edu.ie3.powerFactory2psdm.config.ConversionConfig
import edu.ie3.powerFactory2psdm.config.ConversionConfig.{
  NodeUuidMappingInformation,
  StatGenModelConfigs
}
import edu.ie3.powerFactory2psdm.converter.NodeConverter.getNodeNameMapping
import edu.ie3.powerFactory2psdm.converter.types.{
  LineTypeConverter,
  Transformer2WTypeConverter
}
import edu.ie3.powerFactory2psdm.model.{PreprocessedPfGridModel, RawPfGridModel}
import java.util.UUID
import scala.jdk.CollectionConverters.SetHasAsJava

/** Functionalities to transform an exported and then parsed PowerFactory grid
  * to the PSDM.
  */
case object GridConverter {

  def convert(
      pfGrid: RawPfGridModel,
      config: ConversionConfig
  ): JointGridContainer = {
    val grid = PreprocessedPfGridModel.build(
      pfGrid,
      config.modelConfigs.sRatedSource,
      config.modelConfigs.cosPhiSource
    )
    val (gridElements, convertedNodes) =
      convertGridElements(grid, config.nodeMapping)
    val participants =
      convertParticipants(grid, convertedNodes, config.modelConfigs)
    new JointGridContainer(
      config.gridName,
      gridElements,
      participants,
      new GraphicElements(
        Set.empty[NodeGraphicInput].asJava,
        Set.empty[LineGraphicInput].asJava
      )
    )
  }

  /** Converts the grid elements of the PowerFactory grid
    *
    * @param grid
    *   the raw parsed PowerFactoryGrid
    */
  def convertGridElements(
      grid: PreprocessedPfGridModel,
      nodeUuidMapping: Option[NodeUuidMappingInformation]
  ): (RawGridElements, Map[String, NodeInput]) = {
    val graph =
      GridGraphBuilder.build(grid.nodes, grid.lines ++ grid.switches)
    val nodeId2node = grid.nodes.map(node => (node.id, node)).toMap
    val subnets = SubnetBuilder.buildSubnets(graph, nodeId2node)
    val nodeId2Uuid =
      nodeUuidMapping.map(getNodeNameMapping).getOrElse(Map.empty[String, UUID])
    val nodes = NodeConverter.convertNodesOfSubnets(subnets, nodeId2Uuid)
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
      .map(transformerType =>
        transformerType.id -> Transformer2WTypeConverter.convert(
          transformerType
        )
      )
      .toMap
    val transfomers2W = Transformer2WConverter.convertTransformers(
      grid.transformers2W,
      nodes,
      transformer2WTypes
    )
    val switches =
      grid.switches.map(switch => SwitchConverter.convert(switch, nodes))

    (
      new RawGridElements(
        nodes.values.toSet.asJava,
        lines.toSet.asJava,
        transfomers2W.toSet.asJava,
        Set.empty[Transformer3WInput].asJava,
        switches.toSet.asJava,
        Set.empty[MeasurementUnitInput].asJava
      ),
      nodes
    )
  }

  /** Convert system participants of the power factory grid.
    *
    * @param grid
    *   the PF grid
    * @param convertedNodes
    *   the converted nodes
    * @param statGenConversionConfig
    *   the conversion configuration for static generators
    * @return
    */
  def convertParticipants(
      grid: PreprocessedPfGridModel,
      convertedNodes: Map[String, NodeInput],
      statGenConversionConfig: StatGenModelConfigs
  ): SystemParticipants = {
    val loads = LoadConverter.convertLoads(grid.loads, convertedNodes)
    val statGenModelContainer = StaticGeneratorConverter.convert(
      grid.staticGenerators,
      statGenConversionConfig,
      convertedNodes
    )
    new SystemParticipants(
      Set.empty[BmInput].asJava,
      Set.empty[ChpInput].asJava,
      Set.empty[EvcsInput].asJava,
      Set.empty[EvInput].asJava,
      statGenModelContainer.fixedFeedIns.toSet.asJava,
      Set.empty[HpInput].asJava,
      loads.toSet.asJava,
      statGenModelContainer.pvInputs.toSet.asJava,
      Set.empty[StorageInput].asJava,
      statGenModelContainer.wecInputs.toSet.asJava
    )
  }
}
