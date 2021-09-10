/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter

import edu.ie3.datamodel.models.OperationTime
import edu.ie3.datamodel.models.input.{NodeInput, OperatorInput}
import edu.ie3.datamodel.models.voltagelevels.VoltageLevel
import edu.ie3.powerFactory2psdm.exception.pf.{
  ConversionException,
  GridConfigurationException
}
import edu.ie3.powerFactory2psdm.model.entity.{Node, Subnet}
import edu.ie3.util.quantities.PowerSystemUnits.PU
import edu.ie3.powerFactory2psdm.exception.pf.GridConfigurationException
import edu.ie3.powerFactory2psdm.model.entity.Node
import edu.ie3.powerFactory2psdm.util.QuantityUtils.RichQuantityDouble
import org.locationtech.jts.geom.Point
import tech.units.indriya.quantity.Quantities

import java.util.UUID

object NodeConverter {

  /** Convert all nodes subnet by subnet.
    *
    * @param subnets
    *   subnets of the grid
    * @return
    *   Map of node id to PSDM [[NodeInput]]
    */
  def convertNodesOfSubnets(subnets: List[Subnet]): Map[String, NodeInput] = {
    subnets.flatMap(subnet => convertNodesOfSubnet(subnet)).toMap
  }

  /** Converts all nodes within a subnet to PSDM [[NodeInput]] s
    *
    * @param subnet
    *   the subnet with reference to all PF nodes that live within
    * @return
    *   list of all converted [[NodeInput]]
    */
  def convertNodesOfSubnet(
      subnet: Subnet
  ): Set[(String, NodeInput)] =
    subnet.nodes
      .map(node =>
        (node.id, NodeConverter.convertNode(node, subnet.id, subnet.voltLvl))
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
      voltLvl: VoltageLevel
  ): NodeInput = {
    val geoPosition: Point = CoordinateConverter.convert(node.lat, node.lon)
    val slack = isSlack(node)
    new NodeInput(
      UUID.randomUUID(),
      node.id,
      OperatorInput.NO_OPERATOR_ASSIGNED,
      OperationTime.notLimited(),
      node.vTarget.toPu,
      slack,
      geoPosition,
      voltLvl,
      subnetId
    )
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

  def getNode(id: String, nodes: Map[String, NodeInput]): NodeInput = {
    nodes.getOrElse(
      id,
      throw ConversionException(
        s"Can't find node $id within the converted nodes."
      )
    )
  }
}
