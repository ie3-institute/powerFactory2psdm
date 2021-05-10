/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.model.powerfactory

import edu.ie3.powerFactory2psdm.exception.pf.{MissingGridElementException, MissingParameterException}
import edu.ie3.powerFactory2psdm.model.powerfactory.PowerFactoryGrid.{Lines, Nodes, Switches, Trafos2w}

import java.util.UUID
import com.typesafe.scalalogging.LazyLogging
import org.jgrapht.alg.connectivity.BiconnectivityInspector
import org.jgrapht.graph.{DefaultEdge, Multigraph}

import scala.jdk.CollectionConverters.CollectionHasAsScala
import scala.util.{Failure, Success, Try}

class PowerFactoryGridMaps(pfGrid: PowerFactoryGrid) extends LazyLogging {

  val uuid2node: Map[UUID, Nodes] = pfGrid.nodes match {
    case Some(nodes) => nodes.map(node => (UUID.randomUUID(), node)).toMap
    case None =>
      throw MissingGridElementException("There are no nodes in the Grid")
  }

  val uuid2line: Map[UUID, Lines] = pfGrid.lines match {
    case Some(lines) => lines.map(line => (UUID.randomUUID(), line)).toMap
    case None =>
      logger.debug("There are no lines in the grid")
      Map.empty
  }

  val uuid2trafo2w: Map[UUID, Trafos2w] = pfGrid.trafos2w match {
    case Some(trafos2w) =>
      trafos2w.map(trafo2w => (UUID.randomUUID(), trafo2w)).toMap
    case None =>
      logger.debug("There are no transformes in the grid")
      Map.empty
  }

  val uuid2switch: Map[UUID, Switches] = pfGrid.switches match {
    case Some(switches) =>
      switches.map(switch => (UUID.randomUUID(), switch)).toMap
    case None =>
      logger.debug("There are no switches in the grid")
      Map.empty
  }

  def findNodeUuidFromLocName(locName: String): Try[UUID] = {
    this.uuid2node
      .find {
        case (_, node) if node.loc_name.contains(locName) => true
        case _                                            => false
      }
      .map { case (uuid, _) => Success(uuid) }
      .getOrElse(
        Failure(
          new IllegalArgumentException(
            s"The uuid to pf-node map does not contain a node with id $locName"
          )
        )
      )
  }
}

object PowerFactoryGridMaps {

  def uuid2subnet(graph: Multigraph[UUID, DefaultEdge]): Map[UUID, Int] = {
    val subgraphs = new BiconnectivityInspector(graph).getConnectedComponents.asScala.toSeq
    subgraphs
      .foldLeft(Seq[(UUID, Int)]())(
        (acc, elem) => {
          val id = if (acc.isEmpty) 0 else acc.head._2 + 1
          (elem.vertexSet().asScala.map(uuid => (uuid, id)) ++ acc).toSeq
        }
      )
      .toMap
  }
}
