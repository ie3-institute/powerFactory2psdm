/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter

import edu.ie3.powerFactory2psdm.exception.pf.GridConfigurationException
import edu.ie3.powerFactory2psdm.exception.pf.{
  MissingGridElementException,
  MissingParameterException
}
import edu.ie3.powerFactory2psdm.model.powerfactory.PowerFactoryGrid.{
  Lines,
  Nodes
}
import org.jgrapht.graph.Multigraph

/**
  * Builds a graph representation of the powerfactory grid
  */
object GridGraphBuilder {

  def build(
      maybeNodes: Option[List[Nodes]],
      maybeLines: Option[List[Lines]],
      nodesMap: Map[String, Nodes]
  ): Multigraph[Nodes, Lines] = maybeNodes match {
    case Some(nodes) =>
      val graph = new Multigraph[Nodes, Lines](classOf[Lines])
      nodes.foreach(node => graph.addVertex(node))

      maybeLines match {
        case Some(lines) =>
          lines.foreach(
            line =>
              line.conElms match {
                case Some(conElms) if conElms.size == 2 =>
                  conElms match {
                    // TODO: Can a line connect other elements than just nodes ?
                    case List(Some(nodeA), Some(nodeB)) =>
                      (nodeA.loc_name, nodeB.loc_name) match {
                        case (Some(nodeAId), Some(nodeBId)) =>
                          graph
                            .addEdge(nodesMap(nodeAId), nodesMap(nodeBId), line)
                        case (None, Some(_)) =>
                          throw MissingParameterException(
                            s"Node $nodeA is missing an ID"
                          )
                        case (Some(_), None) =>
                          throw MissingParameterException(
                            s"Node $nodeB is missing an ID"
                          )
                      }
                  }
                case Some(_) =>
                  throw GridConfigurationException(
                    s"There are more or less than two elements connected to line $line"
                  )
              }
          )
      }

      // TODO: Can nodes be connected by something else than lines other than transformers ? -> Switches ?!

      graph

    case None =>
      throw MissingGridElementException("There are no nodes in the Grid")
  }
}
