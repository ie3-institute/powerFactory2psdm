/**
 * © 2021. Johannes Hiry,
 **/

package edu.ie3.powerFactory2psdm.converter

import edu.ie3.powerFactory2psdm.exception.pf.ElementConfigurationException
import edu.ie3.powerFactory2psdm.model.entity.{Edge, Node}
import org.jgrapht.graph._

/** Builds a graph representation of the powerfactory grid
  */
object GridGraphBuilder {

  /** Builds up the graph topology of the grid. All nodes (represented by their
    * ids) and the connection between nodes by edges (lines and switches) are
    * added to the graph. The resulting subgraphs inside the graph represent the
    * subnets of the grid.
    *
    * @param nodes
    *   nodes of the power factory grid
    * @param edges
    *   edges (lines and switches) of the power factory grid
    * @return
    *   [[Multigraph]] of all the ids of the nodes and their connection
    */
  def build[I <: Edge](
      nodes: List[Node],
      edges: List[I]
  ): AsUnmodifiableGraph[String, DefaultEdge] = {

    val graph = new Multigraph[String, DefaultEdge](classOf[DefaultEdge])
    nodes.foreach(node => graph.addVertex(node.id))
    edges.map(edge => {
      graph.addEdge(edge.nodeAId, edge.nodeBId)
    })
    new AsUnmodifiableGraph(graph)
  }

  /** Unpacks the optional ids of two busses, connected by an edge.
    *
    * @param edgeId
    *   id of edge connecting busses with bus1Id and bus2Id
    * @param maybeBus1Id
    *   Option of id of bus 1
    * @param maybeBus2Id
    *   Option of id of bus 2
    * @return
    *   Tuple of the ids of bus 1 and bus 2
    */
  def unpackConnectedBusses(
      edgeId: String,
      maybeBus1Id: Option[String],
      maybeBus2Id: Option[String]
  ): (String, String) = maybeBus1Id.zip(maybeBus2Id) match {
    case Some((bus1Id, bus2Id)) => (bus1Id, bus2Id)
    case None =>
      throw ElementConfigurationException(
        s"Exception occurred while adding an edge. Exc: Edge with id: $edgeId is missing at least one connected node"
      )
  }
}
