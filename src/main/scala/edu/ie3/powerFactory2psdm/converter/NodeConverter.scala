/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter

import edu.ie3.datamodel.models.OperationTime
import edu.ie3.datamodel.models.input.{NodeInput, OperatorInput}
import edu.ie3.powerFactory2psdm.exception.pf.{
  ElementConfigurationException,
  GridConfigurationException
}
import edu.ie3.powerFactory2psdm.model.Subnet
import edu.ie3.powerFactory2psdm.model.powerfactory.PowerFactoryGrid.{
  ConElms,
  Nodes
}
import edu.ie3.util.quantities.PowerSystemUnits.PU
import org.locationtech.jts.geom.Point
import tech.units.indriya.quantity.Quantities

import java.util.UUID
import scala.util.{Failure, Success, Try}

object NodeConverter {

  /**
    * Checks if a node is a slack node by checking if there is an external grid connected to the node.
    *
    * @param maybeConElms an optional List of connected elements to the node
    * @return either whether the node is a slack node or an exception
    */
  def isSlack(maybeConElms: Option[List[Option[ConElms]]]): Try[Boolean] =
    maybeConElms match {
      case Some(conElms) =>
        conElms.flatten.filter(
          conElm => conElm.pfCls.getOrElse("NO_CLASS_DEFINED") == "ElmXnet"
        ) match {
          case Seq(_) => Success(true)
          case Nil    => Success(false)
          case _ =>
            Failure(
              GridConfigurationException(
                "There is more than one external grid connected to the node."
              )
            )
        }
      case None =>
        Failure(
          ElementConfigurationException(
            "The optional connected elements attribute is None."
          )
        )
    }

  /**
    * Converts a PowerFactory node into a PSDM node.
    *
    * @param nodeUUID  UUID of the PF node to convert
    * @param UUID2node Map of UUIDs and their associated [[Nodes]]
    * @param subnet    subnet the PF node lives in
    * @return a PSDM [[NodeInput]]
    */
  def convertNode(
      nodeUUID: UUID,
      UUID2node: Map[UUID, Nodes],
      subnet: Subnet
  ): NodeInput = {
    val pfNode = UUID2node(nodeUUID)
    val subnetNr = subnet.id
    val vTarget = pfNode.vtarget
      .map(Quantities.getQuantity(_, PU))
      .getOrElse(Quantities.getQuantity(1d, PU))
    val geoPosition: Point =
      CoordinateConverter.convert(pfNode.GPSlat, pfNode.GPSlon)
    val voltLvl = subnet.voltLvl
    (pfNode.id, isSlack(pfNode.conElms)) match {
      case (Some(id), Success(isSlack)) =>
        new NodeInput(
          nodeUUID,
          id,
          OperatorInput.NO_OPERATOR_ASSIGNED,
          OperationTime.notLimited(),
          vTarget,
          isSlack,
          geoPosition,
          voltLvl,
          subnetNr
        )
      case (None, _) =>
        throw ElementConfigurationException(s"The PF node $pfNode has no ID")
      case (Some(id), Failure(exc)) =>
        throw GridConfigurationException(
          s"Exception occurred while checking if node with id $id is a slack node. Exception: $exc"
        )
    }

  }

}
