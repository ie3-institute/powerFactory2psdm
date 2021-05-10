/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter

import edu.ie3.datamodel.models.OperationTime
import edu.ie3.datamodel.models.input.{NodeInput, OperatorInput}
import edu.ie3.powerFactory2psdm.model.powerfactory.PowerFactoryGrid.{
  ConElms,
  Nodes
}
import edu.ie3.util.quantities.PowerSystemUnits.PU
import org.jgrapht.graph.{DefaultEdge, Multigraph}
import tech.units.indriya.quantity.Quantities

import java.util.UUID

object NodeConverter {

  def isSlack(conElms: Option[List[Option[ConElms]]]): Boolean = {
    ???
  }

  def convert(
      nodeUuid: UUID,
      gridGraph: Multigraph[UUID, DefaultEdge],
      uuid2node: Map[UUID, Nodes]
  ): NodeInput = {

    val pfNode = uuid2node(nodeUuid)

    // todo: how to handle missing id?
    val id = uuid2node(nodeUuid).loc_name.getOrElse("")

    val vTarget = pfNode.vtarget match {
      case Some(value) => Quantities.getQuantity(value, PU)
      case None        => Quantities.getQuantity(1d, PU)
    }
    val geoPosition = CoordinateConverter.convert(pfNode.GPSlon, pfNode.GPSlat)

    val voltLvl = ???

    val subnet = ???

    // todo: how to check whether node is a slack node
    val slack = isSlack(pfNode.conElms)

    // todo: check if there already is a slack node for the given subnet

    new NodeInput(
      UUID.randomUUID(),
      id,
      OperatorInput.NO_OPERATOR_ASSIGNED,
      OperationTime.notLimited(),
      vTarget,
      slack,
      geoPosition,
      voltLvl,
      subnet
    )
  }
}
