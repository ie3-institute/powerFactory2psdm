/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter

import edu.ie3.powerFactory2psdm.exception.pf.{
  ElementConfigurationException,
  MissingParameterException
}
import edu.ie3.powerFactory2psdm.model.powerfactory.PowerFactoryGrid.{
  ConElms,
  Lines,
  Switches
}
import edu.ie3.powerFactory2psdm.model.powerfactory.PowerFactoryGridMaps
import org.jgrapht.graph._

import java.util.UUID
import scala.util.{Failure, Success, Try}

/**
  * Builds a graph representation of the powerfactory grid
  */
object GridGraphBuilder {

  def conElms2nodeUuids(
      conElms: List[ConElms],
      pfGridMaps: PowerFactoryGridMaps
  ): Try[(UUID, UUID)] = {
    conElms match {
      case List(nodeAConElem, nodeBConElem) =>
        nodeAConElem.loc_name.zip(nodeBConElem.loc_name) match {
          case Some((nodeALocName, nodeBLocName)) =>
            pfGridMaps
              .findNodeUuidFromLocName(nodeALocName)
              .flatMap(
                nodeAUuid =>
                  pfGridMaps
                    .findNodeUuidFromLocName(nodeBLocName)
                    .map((nodeAUuid, _))
              )
          case None =>
            Failure(
              MissingParameterException(
                "The connected elements do not contain an id."
              )
            )
        }
      case _ =>
        Failure(
          ElementConfigurationException(
            "There are more or less connected elements for the edge."
          )
        )
    }
  }

  def maybeConElms2nodeUuids(
      maybeConElms: Option[List[Option[ConElms]]],
      pfGridMaps: PowerFactoryGridMaps,
      ex: Throwable => IllegalArgumentException
  ): Option[(UUID, UUID)] = {
    maybeConElms.map(
      conElem =>
        conElms2nodeUuids(conElem.flatten, pfGridMaps) match {
          case Failure(exception) => //
            throw ex(exception)
          case Success((nodeA, nodeB)) =>
            (nodeA, nodeB)
        }
    )
  }

  private val addingEdgeException
      : String => Throwable => IllegalArgumentException = edgeName =>
    ex =>
      new IllegalArgumentException(
        s"Exception occurred while adding " +
          s"$edgeName to grid graph. Exception: $ex"
      )

  def build(
      pfGridMaps: PowerFactoryGridMaps
  ): Multigraph[UUID, DefaultEdge] = {

    val graph = new Multigraph[UUID, DefaultEdge](classOf[DefaultEdge])

    pfGridMaps.uuid2node.keys.foreach(uuid => graph.addVertex(uuid))

    (pfGridMaps.uuid2line ++ pfGridMaps.uuid2switch).values
      .map {
        case edge: Lines =>
          (edge.loc_name, edge.conElms)
        case edge: Switches =>
          (edge.loc_name, edge.conElms)
      }
      .foreach {
        case (locName, conElms) =>
          maybeConElms2nodeUuids(
            conElms,
            pfGridMaps,
            addingEdgeException(locName.getOrElse("NO_LOC_NAME_GIVEN"))
          ).map { case (nodeA, nodeB) => graph.addEdge(nodeA, nodeB) }
      }
    graph
  }
}
