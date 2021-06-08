/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.model.powerfactory

import edu.ie3.powerFactory2psdm.exception.pf.{
  MissingGridElementException,
  MissingParameterException
}
import edu.ie3.powerFactory2psdm.model.powerfactory.RawGridModel.{
  Lines,
  Nodes,
  Switches
}
import java.util.UUID
import com.typesafe.scalalogging.LazyLogging

case class PowerFactoryGridMaps(
    uuid2Node: Map[UUID, Nodes],
    nodeId2Uuid: Map[String, UUID],
    uuid2Line: Map[UUID, Lines],
    uuid2Switch: Map[UUID, Switches]
)

object PowerFactoryGridMaps extends LazyLogging {
  def apply(pfGrid: RawGridModel): PowerFactoryGridMaps = {

    val uuid2Node: Map[UUID, Nodes] = pfGrid.nodes match {
      case Some(nodes) => nodes.map(node => (UUID.randomUUID(), node)).toMap
      case None =>
        throw MissingGridElementException("There are no nodes in the Grid")
    }

    val nodeId2Uuid: Map[String, UUID] = uuid2Node map {
      case (uuid, node) =>
        (
          node.id.getOrElse(
            throw MissingParameterException(s"Node ${node.id} has no id")
          ),
          uuid
        )
    }

    val uuid2Line: Map[UUID, Lines] = pfGrid.lines match {
      case Some(lines) => lines.map(line => (UUID.randomUUID(), line)).toMap
      case None =>
        logger.debug("There are no lines in the grid")
        Map.empty
    }

    val uuid2Switch: Map[UUID, Switches] = pfGrid.switches match {
      case Some(switches) =>
        switches.map(switch => (UUID.randomUUID(), switch)).toMap
      case None =>
        logger.debug("There are no switches in the grid")
        Map.empty
    }

    PowerFactoryGridMaps(
      uuid2Node,
      nodeId2Uuid,
      uuid2Line,
      uuid2Switch
    )
  }
}
