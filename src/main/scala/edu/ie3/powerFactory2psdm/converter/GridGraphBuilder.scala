/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter

import edu.ie3.powerFactory2psdm.exception.pf.GridConfigurationException
import edu.ie3.powerFactory2psdm.exception.pf.MissingParameterException
import edu.ie3.powerFactory2psdm.model.powerfactory.PowerFactoryGrid.{ConElms, Lines, Switches}
import edu.ie3.powerFactory2psdm.model.powerfactory.PowerFactoryGridMaps
import org.jgrapht.graph._

import java.util.UUID

/**
  * Builds a graph representation of the powerfactory grid
  */
object GridGraphBuilder {

  // todo extract loc_name extractions
  def conELms2nodeUuids(maybeConElms: List[Option[ConElms]], pfGridMaps: PowerFactoryGridMaps): List[Option[UUID]] = {
    val nodeIds: List[String] =
      for {
        maybeConELm <- maybeConElms
        conElm <- maybeConELm
        loc_name <- conElm.loc_name
      } yield loc_name

    nodeIds.map(
      loc_name =>
        pfGridMaps.uuid2node.find{
          case (_, node) if node.loc_name.contains(loc_name) => true
          case _ => false
        }.map(uuid2node => uuid2node._1))
  }

  // todo prevent using "Any" ?
  def addEdge(nodeUuids: List[Option[UUID]], edge: Any, graph: Multigraph[UUID, DefaultEdge]): Unit = nodeUuids match {
    case List(Some(uuid1), Some(uuid2)) => graph.addEdge(uuid1, uuid2)
    case _ => throw GridConfigurationException(
      s"Couldn't correctly identify UUIDs for $edge with identified node uuids: $nodeUuids"
    )
  }

    // todo NOTE: Pattern matching um Elemente zu extrahieren
  def build(
             pfGridMaps: PowerFactoryGridMaps
           ): Multigraph[UUID, DefaultEdge] = {

    val graph = new Multigraph[UUID, DefaultEdge](classOf[DefaultEdge])

    pfGridMaps.uuid2node.keys.foreach(uuid => graph.addVertex(uuid))

    val conNodes: Iterable[List[Option[ConElms]]] =
      (pfGridMaps.uuid2line ++ pfGridMaps.uuid2switch)
        .flatMap(edge =>
          edge match {
              case (uuid, line: Lines) => line.conElms
              case (uuid, switch: Switches)  => switch.conElms
            })

    conNodes.foreach(conElms =>
      conElms match {
        case conElms.flatten.size == 2 =>
          val nodeUuids: List[Option[UUID]] = conELms2uuids(conElms, pfGridMaps)
          addEdge(nodeUuids, edge, graph)

        case Some(_) =>
          throw GridConfigurationException(
            s"There are more or less than two elements connected to line $edge"
          )

        case None =>
          throw MissingParameterException(
            s"The line $edge has no connected elements"
          )
      }
    )

      edge
    )

    // TODO: Extend to include switches as edges
    graph



//          match {
//            // TODO: Can a line connect other elements than just nodes ?
//            case List(Some(nodeA), Some(nodeB)) =>
//              (nodeA.loc_name, nodeB.loc_name) match {
//                case (Some(nodeAId), Some(nodeBId)) =>
//                  graph
//                    .addEdge(nodesMap(nodeAId), nodesMap(nodeBId), line)
//                case (None, Some(_)) =>
//                  throw MissingParameterException(
//                    s"Node $nodeA is missing an ID"
//                  )
//                case (Some(_), None) =>
//                  throw MissingParameterException(
//                    s"Node $nodeB is missing an ID"
//                  )
//              }
//          }



//    maybeLines match {
//      case Some(lines) =>
//        lines.foreach(
//          line =>
//            line.conElms match {
//              case Some(conElms) if conElms.size == 2 =>
//                conElms match {
//                  // TODO: Can a line connect other elements than just nodes ?
//                  case List(Some(nodeA), Some(nodeB)) =>
//                    (nodeA.loc_name, nodeB.loc_name) match {
//                      case (Some(nodeAId), Some(nodeBId)) =>
//                        graph
//                          .addEdge(nodesMap(nodeAId), nodesMap(nodeBId), line)
//                      case (None, Some(_)) =>
//                        throw MissingParameterException(
//                          s"Node $nodeA is missing an ID"
//                        )
//                      case (Some(_), None) =>
//                        throw MissingParameterException(
//                          s"Node $nodeB is missing an ID"
//                        )
//                    }
//                }
//              case Some(_) =>
//                throw GridConfigurationException(
//                  s"There are more or less than two elements connected to line $line"
//                )
//            }
//        )
//    }

  }
}
