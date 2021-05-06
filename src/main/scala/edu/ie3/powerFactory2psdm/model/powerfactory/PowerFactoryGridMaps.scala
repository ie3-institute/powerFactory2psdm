/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.model.powerfactory

import edu.ie3.powerFactory2psdm.exception.pf.MissingGridElementException
import edu.ie3.powerFactory2psdm.model.powerfactory.PowerFactoryGrid.{Lines, Nodes, Switches, Trafos2w}
import java.util.UUID

import com.typesafe.scalalogging.LazyLogging

class PowerFactoryGridMaps(pfGrid: PowerFactoryGrid) extends LazyLogging {
  // todo final
  val uuid2node: Map[UUID, Nodes] = pfGrid.nodes match {
    case Some(nodes) => nodes.map(node => (UUID.randomUUID(), node)).toMap
    case None => throw MissingGridElementException("There are no nodes in the Grid")
  }

  final val uuid2line: Map[UUID, Lines] = pfGrid.lines match {
    case Some(lines) => lines.map(line => (UUID.randomUUID(), line)).toMap
    case None =>
      logger.debug("There are no lines in the grid")
      Map()
  }

  final val uuid2trafo2w: Map[UUID, Trafos2w] = pfGrid.trafos2w match {
  case Some(trafos2w) => trafos2w.map(trafo2w => (UUID.randomUUID(), trafo2w)).toMap
  case None =>
    logger.debug("There are no transformes in the grid")
    Map()
  }

  final val uuid2switch: Map[UUID, Switches] = pfGrid.switches match {
  case Some(switches) => switches.map(switch => (UUID.randomUUID(), switch)).toMap
  case None =>
    logger.debug("There are no switches in the grid")
    Map()
  }


  // TODO: still neccessary?
  def getNodesMap(pfGrid: PowerFactoryGrid): Map[String, Nodes] = {
    pfGrid.nodes match {
      case Some(pfNodes) =>
        pfNodes
          .map(
            node =>
              (
                node.loc_name
                  .getOrElse(
                    throw new RuntimeException(s"Node $node has no id")
                  ),
                node
              )
          )
          .toMap
      case None =>
        throw MissingGridElementException("There are no nodes in the Grid")
    }
  }
}