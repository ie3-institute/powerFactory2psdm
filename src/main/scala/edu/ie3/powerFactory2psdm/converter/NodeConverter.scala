/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter

import edu.ie3.datamodel.models.OperationTime
import edu.ie3.datamodel.models.input.{NodeInput, OperatorInput}
import edu.ie3.datamodel.models.voltagelevels.VoltageLevel
import edu.ie3.powerFactory2psdm.config.ConversionConfig.NodeUuidMappingInformation
import edu.ie3.powerFactory2psdm.exception.pf.{ConversionException, GridConfigurationException}
import edu.ie3.powerFactory2psdm.model.entity.{Node, Subnet}
import edu.ie3.util.quantities.PowerSystemUnits.PU
import edu.ie3.powerFactory2psdm.exception.pf.GridConfigurationException
import edu.ie3.powerFactory2psdm.model.entity.Node
import edu.ie3.powerFactory2psdm.util.QuantityUtils.RichQuantityDouble
import org.locationtech.jts.geom.Point
import tech.units.indriya.quantity.Quantities

import java.io.IOException
import java.util.UUID
import scala.io.Source
import scala.util.{Failure, Success, Try}

object NodeConverter {

  /** Convert all nodes subnet by subnet.
    *
    * @param subnets
    *   subnets of the grid
    * @return
    *   Map of node id to PSDM [[NodeInput]]
    */
  def convertNodesOfSubnets(subnets: List[Subnet], unsafeNodeId2Uuid: Map[String, UUID]): Map[String, NodeInput] = {
    subnets.flatMap(subnet => convertNodesOfSubnet(subnet, unsafeNodeId2Uuid)).toMap
  }

  /** Converts all nodes within a subnet to PSDM [[NodeInput]] s
    *
    * @param subnet
    *   the subnet with reference to all PF nodes that live within
    * @return
    *   list of all converted [[NodeInput]]
    */
  def convertNodesOfSubnet(
    subnet: Subnet,
    unsafeNodeId2Uuid: Map[String, UUID]
  ): Set[(String, NodeInput)] =
    subnet.nodes
      .map(node =>
        (node.id, NodeConverter.convertNode(node, subnet.id, subnet.voltLvl, unsafeNodeId2Uuid))
      )

  /** Converts a PowerFactory node into a PSDM node.
    *
    * @param node
    *   the PF node to convert
    * @param subnetId
    *   subnet id the node is assigned to
    * @param voltLvl
    *   voltage level of the node
    * @return
    *   a PSDM [[NodeInput]]
    */
  def convertNode(
      node: Node,
      subnetId: Int,
      voltLvl: VoltageLevel,
      shortNodeId2Uuid: Map[String, UUID]
  ): NodeInput = {
    val geoPosition: Point = CoordinateConverter.convert(node.lat, node.lon)
    val slack = isSlack(node)
    val uuid = shortNodeId2Uuid.getOrElse(node.unsafeId, UUID.randomUUID())
    new NodeInput(
      uuid,
      node.id,
      OperatorInput.NO_OPERATOR_ASSIGNED,
      OperationTime.notLimited(),
      node.vTarget.asPu,
      slack,
      geoPosition,
      voltLvl,
      subnetId
    )
  }

  /** Creates a Map that maps from unsafe node ids to uuids from a csv file that is used for node conversion.
   *  This can be used to keep uuids of the nodes consistent between original data and the converted grid.
   *
   * @param nodeUuidMappingInformation necessary mapping information
   * @return Map from unsafe node id to uuid
   */
  def getNodeNameMapping(nodeUuidMappingInformation: NodeUuidMappingInformation): Map[String, UUID] = {
    // parse csv file
    val bufferedSource = Source.fromFile(nodeUuidMappingInformation.filePath)
    val lines = bufferedSource.getLines()

    lines.take(1).next.split(nodeUuidMappingInformation.csvSeparator) match {
      case List("uuid", "id") =>
      case _ => throw new IOException("Invalid CSV header. We expect the header of the node name mapping file to be \"uuid\" [CSV Sep.] \"id\".")
    }

    val nodeId2Uuid = lines.zipWithIndex.map{ case (line, index) =>
      line.split(nodeUuidMappingInformation.csvSeparator).map(_.trim).match{
        case Array(uuidString, id) =>
          val uuid = Try(UUID.fromString(uuidString)) match {
            case Failure(exception) => throw new IllegalArgumentException(
              s"UUID: $uuidString on line: $line is not a valid UUID. Reason: ${exception.getMessage}")
            case Success(uuid) => uuid
          }
          (id, uuid)
        case Array(_) => throw new IOException(s"Invalid format on csv line $index. Every line should have exactly two elements.")
      }}

    // check for duplicates in ids or uuids
    val (ids, uuids) = nodeId2Uuid.toVector.unzip
    val duplicateIds = ConversionHelper.getDuplicates(ids)
    if (duplicateIds.nonEmpty) {
      throw ConversionException(f"There are the following duplicate ids in the node id to uuid mapping: $duplicateIds")
    }
    val duplicateUuids = ConversionHelper.getDuplicates(uuids)
    if (duplicateIds.nonEmpty) {
      throw ConversionException(f"There are the following duplicate uuids in the node id to uuid mapping: $duplicateUuids")
    }

    bufferedSource.close
    nodeId2Uuid.toMap
  }

  /** Checks if a node is a slack node by checking if there is an external grid
    * connected to the node.
    *
    * @param node
    *   node to check
    * @return
    *   true or false
    */
  private def isSlack(node: Node): Boolean =
    node.conElms.filter(_.pfCls == "ElmXnet") match {
      case Seq(_) => true
      case Nil    => false
      case _ =>
        throw GridConfigurationException(
          s"There is more than one external grid connected to Node: ${node.id}"
        )
    }

  def getNode(id: String, nodes: Map[String, NodeInput]): Try[NodeInput] = {
    nodes
      .get(id)
      .map(Success(_))
      .getOrElse(
        Failure(
          ConversionException(
            s"Can't find node $id within the converted nodes."
          )
        )
      )
  }

  def missingNodeException(
      system: String,
      exc: Throwable
  ): ConversionException =
    ConversionException(s"Can't find converted node of $system", exc)
}
