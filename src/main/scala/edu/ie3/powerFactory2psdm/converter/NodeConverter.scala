/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter

import edu.ie3.datamodel.models.OperationTime
import edu.ie3.datamodel.models.input.{NodeInput, OperatorInput}
import edu.ie3.datamodel.models.voltagelevels.VoltageLevel
import edu.ie3.powerFactory2psdm.exception.pf.GridConfigurationException
import edu.ie3.powerFactory2psdm.model.Subnet
import edu.ie3.powerFactory2psdm.model.powerfactory.Node
import edu.ie3.util.quantities.PowerSystemUnits.PU
import org.locationtech.jts.geom.Point
import tech.units.indriya.quantity.Quantities

import java.util.UUID

object NodeConverter {

  /**
    * Converts a PowerFactory node into a PSDM node.
    *
    * @param id  id of the PF node to convert
    * @param id2node Map of ids and their associated [[Node]]s
    * @param subnet    subnet the PF node lives in
    * @return a PSDM [[NodeInput]]
    */
  def convertNode(
      node: Node,
      subnetId: Int,
      voltLvl: VoltageLevel
  ): NodeInput = {
    val vTarget = Quantities.getQuantity(node.vTarget, PU)
    val geoPosition: Point = CoordinateConverter.convert(node.lat, node.lon)
    val voltLvl = voltLvl
    val slack = isSlack(node)
    new NodeInput(
      UUID.randomUUID(),
      node.id,
      OperatorInput.NO_OPERATOR_ASSIGNED,
      OperationTime.notLimited(),
      vTarget,
      slack,
      geoPosition,
      voltLvl,
      subnetId
    )
  }

  /**
    * Checks if a node is a slack node by checking if there is an external grid connected to the node.
    *
    * @param node node to check
    * @return true or false
    */
  def isSlack(node: Node): Boolean =
    node.conElms.filter(_.pfCls == "ElmXnet") match {
      case Seq(_) => true
      case Nil    => false
      case _ =>
        throw GridConfigurationException(
          s"There is more than one external grid connected to Node: ${node.id}"
        )
    }
}
