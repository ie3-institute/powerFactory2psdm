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
import edu.ie3.powerFactory2psdm.model.powerfactory.PowerFactoryGrid.{
  Lines,
  Nodes,
  Switches
}
import java.util.UUID
import com.typesafe.scalalogging.LazyLogging

class PowerFactoryGridMaps(pfGrid: PowerFactoryGrid) extends LazyLogging {

  val UUID2node: Map[UUID, Nodes] = pfGrid.nodes match {
    case Some(nodes) => nodes.map(node => (UUID.randomUUID(), node)).toMap
    case None =>
      throw MissingGridElementException("There are no nodes in the Grid")
  }

  val nodeId2UUID: Map[String, UUID] = UUID2node.map(
    elem =>
      (
        elem._2.id.getOrElse(
          throw MissingParameterException(s"Node $elem._2 has no id")
        ),
        elem._1
      )
  )

  val UUID2line: Map[UUID, Lines] = pfGrid.lines match {
    case Some(lines) => lines.map(line => (UUID.randomUUID(), line)).toMap
    case None =>
      logger.debug("There are no lines in the grid")
      Map.empty
  }

  val UUID2switch: Map[UUID, Switches] = pfGrid.switches match {
    case Some(switches) =>
      switches.map(switch => (UUID.randomUUID(), switch)).toMap
    case None =>
      logger.debug("There are no switches in the grid")
      Map.empty
  }

  def nodeIdsToUUIDs(ids: Set[String]): Set[UUID] = {
    ids.map(id => nodeId2UUID(id))
  }
}
