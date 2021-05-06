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
import scala.util.{Failure, Success, Try}

/**
 * Builds a graph representation of the powerfactory grid
 */
object GridGraphBuilder {

  def findNodeFromLoc(locName: String,
                      pfGridMaps: PowerFactoryGridMaps): Try[UUID] = {
    pfGridMaps.uuid2node.find {
      case (_, node) if node.loc_name.contains(locName) => true
      case _ => false
    }.map { case (uuid, _) => Success(uuid) }.getOrElse(
      // todo exception string if uuid2node map does not contain loc
      Failure(new IllegalArgumentException("")))
  }

  // todo extract loc_name extractions
  def conELms2nodeUuids(conElms: List[ConElms],
                        pfGridMaps: PowerFactoryGridMaps): Try[(UUID, UUID)] = {
    conElms match {
      case List(nodeAConElem, nodeBConElem) =>
        nodeAConElem.loc_name.zip(nodeBConElem.loc_name) match {
          case Some((nodeALocName, nodeBLocName)) =>
            findNodeFromLoc(nodeALocName, pfGridMaps).flatMap(nodeAUuid =>
              findNodeFromLoc(nodeBLocName, pfGridMaps).map((nodeAUuid, _)))
          case None =>
            // todo failure string nodeA or node B not defined
            Failure(new IllegalArgumentException(""))
        }
      case invalidList =>
        // todo more or less than two con elements in conElems List -> more than two or less than two connected elements
        Failure(new IllegalArgumentException(""))
    }


    //    conElms.flatten.map(conElem => {
    //      conElem.loc_name match {
    //        case Some(locName) =>
    //          pfGridMaps.uuid2node.find {
    //            case (_, node) if node.loc_name.contains(locName) => true
    //            case _ => false
    //          }.map { case (uuid, _) => Success(uuid) }.getOrElse(
    //            // todo exception string if uuid2node map does not contain loc
    //            Failure(new IllegalArgumentException("")))
    //        case None =>
    //          // todo throw exception or log error if connected lement loc_name is not defined
    //          Failure(new IllegalArgumentException(""))
    //      }
    //    })
    //    match {
    //      case List(Success(nodeA), Success(nodeB)) =>
    //        Success((nodeA, nodeB))
    //      case invalidList =>
    //
    //        // throw new Exception more or less than two nodes
    //        Failure(new IllegalArgumentException("")) // todo exception string
    //    }


    //      for {
    //        maybeConELm <- conElms
    //        conElm <- maybeConELm
    //        loc_name <- conElm.loc_name
    //      } yield loc_name

    //    nodeIds.map(
    //      loc_name =>
    //        pfGridMaps.uuid2node.find {
    //          case (_, node) if node.loc_name.contains(loc_name) => true
    //          case _ => false
    //        }.map(uuid2node => uuid2node._1))
  }


  //  // todo prevent using "Any" ?
  //  def addEdge(nodeUuids: List[Option[UUID]],
  //              edge: Any,
  //              graph: Multigraph[UUID, DefaultEdge]): Unit = nodeUuids match {
  //    case List(Some(uuid1), Some(uuid2)) =>
  //      graph.addEdge(uuid1, uuid2)
  //    case _ => throw GridConfigurationException(
  //      s"Couldn't correctly identify UUIDs for $edge with identified node uuids: $nodeUuids"
  //    )
  //  }


  def edges(maybeConElms: Option[List[Option[ConElms]]],
            pfGridMaps: PowerFactoryGridMaps,
            ex: Throwable => IllegalArgumentException
           ): Option[(UUID, UUID)] = {
    maybeConElms.map(conElem =>
      conELms2nodeUuids(conElem.flatten, pfGridMaps) match {
        case Failure(exception) =>
          throw ex(exception)
        case Success((nodeA, nodeB)) =>
          (nodeA, nodeB)
      })
  }

  private val exception: String => Throwable => IllegalArgumentException = edgeName => ex =>
    new IllegalArgumentException(s"Exception occurred while adding " +
      s"$edgeName to grid graph. Exception: $ex")


  // todo NOTE: Pattern matching um Elemente zu extrahieren
  def build(
             pfGridMaps: PowerFactoryGridMaps
           ): Multigraph[UUID, DefaultEdge] = {

    val graph = new Multigraph[UUID, DefaultEdge](classOf[DefaultEdge])

    pfGridMaps.uuid2node.keys.foreach(uuid => graph.addVertex(uuid))

    (pfGridMaps.uuid2line ++ pfGridMaps.uuid2switch).values.map {
      case edge: Lines =>
        (edge.loc_name, edge.conElms)
      case edge: Switches =>
        (edge.loc_name, edge.conElms)
    }.foreach {
      case (locName, conElms) =>
        edges(conElms, pfGridMaps,
          exception(locName.getOrElse("NO_LOC_NAME_GIVEN")))
          .map { case (nodeA, nodeB) => graph.addEdge(nodeA, nodeB) }
    }

    graph


    //
    //      .foreach {
    //      case (_, edge: Lines) =>
    //        edges(edge.conElms, pfGridMaps, exception(edge.loc_name.getOrElse("NO_LOC_NAME_GIVEN"))
    //        ).map { case (nodeA, nodeB) => graph.addEdge(nodeA, nodeB) }
    //      case (_, edge: Switches) =>
    //        edges(edge.conElms, pfGridMaps,
    //          exception(edge.loc_name.getOrElse("NO_LOC_NAME_GIVEN"))
    //        ).map { case (nodeA, nodeB) => graph.addEdge(nodeA, nodeB) }
    //    }

    //      .flatMap(edge =>
    //      edge match {
    //        case (uuid, line: Lines) if line.conElms.isDefined =>
    //          line.conElms.ge
    //        case (uuid, lines: Lines) =>
    //        case (uuid, switch: Switches) =>
    //          switch.conElms
    //        case (uuid, switch: Switches) =>
    //      })


    //    val conNodes: Iterable[List[Option[ConElms]]] =
    //      (pfGridMaps.uuid2line ++ pfGridMaps.uuid2switch)
    //        .flatMap(edge =>
    //          edge match {
    //            case (uuid, line: Lines) => line.conElms
    //            case (uuid, switch: Switches) => switch.conElms
    //          })

    //    conNodes.foreach(conElms =>
    //      conElms match {
    //        case conElms.flatten.size == 2 =>
    //          val nodeUuids: List[Option[UUID]] = conELms2uuids(conElms, pfGridMaps)
    //          addEdge(nodeUuids, edge, graph)
    //
    //        case Some(_) =>
    //          throw GridConfigurationException(
    //            s"There are more or less than two elements connected to line $edge"
    //          )
    //
    //        case None =>
    //          throw MissingParameterException(
    //            s"The line $edge has no connected elements"
    //          )
    //      }
    //    )
    //
    //    edge
    //    )
    //
    //    // TODO: Extend to include switches as edges
    //    graph


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

    //  }
  }
}
