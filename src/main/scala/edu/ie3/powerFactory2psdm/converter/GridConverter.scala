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
import edu.ie3.powerFactory2psdm.exception.pf.{
  ConversionException,
  ElementConfigurationException
}
import edu.ie3.powerFactory2psdm.model.Subnet
import edu.ie3.powerFactory2psdm.model.powerfactory.PowerFactoryGrid.{
  LineTypes,
  Lines,
  Nodes
}
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
    val uuid2NodeInput: Map[UUID, NodeInput] =
      subnets.foldLeft(Map[UUID, NodeInput]())(
        (acc, subnet) =>
          acc ++ convertNodesOfSubnet(subnet, pfGridMaps.uuid2Node)
      )
    val maybeLines = (rawPfGrid.lines, rawPfGrid.lineTypes) match {
      case (Some(lines), Some(types)) =>
        convertLines(
          lines,
          types,
          pfGridMaps.nodeId2Uuid,
          uuid2NodeInput
        )
      case (Some(lines), None) =>
        throw ConversionException(
          "Conversion of lines without specified line types is not supported."
        )
      case (None, _) => None
    }
  }

  private def convertLines(
      lines: List[Lines],
      types: List[LineTypes],
      nodeId2Uuid: Map[String, UUID],
      uuid2NodeInput: Map[UUID, NodeInput]
  ): List[LineInput] = {
    val typeId2lineTypeInput: Map[String, LineTypeInput] =
      types
        .map(
          lineType =>
            (
              lineType.id.getOrElse(
                throw ElementConfigurationException(
                  s"LineType: $lineType has no id."
                )
              ),
              LineTypeConverter.convert(lineType)
            )
        )
        .toMap
    lines.map(
      LineConverter
        .convert(_, typeId2lineTypeInput, nodeId2Uuid, uuid2NodeInput)
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
  ): Map[UUID, NodeInput] =
    subnet.nodeUuids
      .map(
        nodeUuid =>
          (nodeUuid, NodeConverter.convertNode(nodeUuid, uuid2node, subnet))
      )
      .toMap

}
