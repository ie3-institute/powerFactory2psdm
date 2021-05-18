/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter

import edu.ie3.powerFactory2psdm.exception.pf.{
  ElementConfigurationException,
  MissingGridElementException
}
import edu.ie3.powerFactory2psdm.model.powerfactory.PowerFactoryGrid.{
  Lines,
  Switches
}
import edu.ie3.powerFactory2psdm.model.powerfactory.PowerFactoryGridMaps
import org.jgrapht.graph._

import java.util.UUID

/**
  * Builds a graph representation of the powerfactory grid
  */
object GridGraphBuilder {

  def unpackConnectedBusses(
      edgeId: String,
      bus1Id: Option[String],
      bus2Id: Option[String]
  ): (String, String) = bus1Id.zip(bus2Id) match {
    case Some((bus1Id, bus2Id)) => (bus1Id, bus2Id)
    case None =>
      throw ElementConfigurationException(
        s"Exception occurred while adding an edge. Exc: Edge with id: $edgeId is missing at least one connected node"
      )
  }

  def build(
      pfGridMaps: PowerFactoryGridMaps
  ): Multigraph[UUID, DefaultEdge] = {
    val graph = new Multigraph[UUID, DefaultEdge](classOf[DefaultEdge])
    pfGridMaps.UUID2node.keys.foreach(uuid => graph.addVertex(uuid))
    val connectedBusIdPairs: Iterable[(String, String)] =
      (pfGridMaps.UUID2line.values ++ pfGridMaps.UUID2switch.values)
        .map {
          case edge: Lines =>
            unpackConnectedBusses(
              edge.id.getOrElse("NO_ID"),
              edge.bus1Id,
              edge.bus2Id
            )
          case edge: Switches =>
            unpackConnectedBusses(
              edge.id.getOrElse("NO_ID"),
              edge.bus1Id,
              edge.bus2Id
            )
        }

    connectedBusIdPairs.foreach { ids =>
      val (bus1Id, bus2Id) = ids
      val nodeAUUID = pfGridMaps.nodeId2UUID.getOrElse(
        bus1Id,
        throw MissingGridElementException(
          s"There is no node with id: $bus1Id in pfGridMaps"
        )
      )
      val nodeBUUID = pfGridMaps.nodeId2UUID.getOrElse(
        bus2Id,
        throw MissingGridElementException(
          s"There is no node with id: $bus2Id in pfGridMaps"
        )
      )
      graph.addEdge(nodeAUUID, nodeBUUID)
    }
    graph
  }
}
