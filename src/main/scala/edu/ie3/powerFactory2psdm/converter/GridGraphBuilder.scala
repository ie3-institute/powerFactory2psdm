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

  /**
   * Checks if a switch is only connected to a single element. These switches commonly occur in non-used connections of
   * substations, so they are no reason to throw an exception, but should be filtered out
   */
  def isSinglyConnectedSwitch(
      switch: Switches
  ): Boolean = {
    switch.conElms
      .getOrElse(
        throw ElementConfigurationException(s"Switch ${switch.id.getOrElse("NO_ID")} isn't connected to anything")
      )
      .flatten
      .size == 1
  }

  def conElms2nodeUuids(
      conElms: List[ConElms],
      pfGridMaps: PowerFactoryGridMaps
  ): Try[(UUID, UUID)] = {
    conElms match {
      case List(nodeAConElem, nodeBConElem) =>
        nodeAConElem.id.zip(nodeBConElem.id) match {
          case Some((nodeAId, nodeBId)) =>
            Success(
              pfGridMaps.nodeId2UUID(nodeAId),
              pfGridMaps.nodeId2UUID(nodeBId)
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
            "The edge has more or less than two connected elements."
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

    pfGridMaps.UUID2node.keys.foreach(uuid => graph.addVertex(uuid))

    (pfGridMaps.UUID2line.values ++ pfGridMaps.UUID2switch.values.filter(
      switch => !isSinglyConnectedSwitch(switch)
    )).map {
        case edge: Lines =>
          (edge.id, edge.conElms)
        case edge: Switches =>
          (edge.id, edge.conElms)
      }
      .foreach {
        case (id, conElms) =>
          maybeConElms2nodeUuids(
            conElms,
            pfGridMaps,
            addingEdgeException(id.getOrElse("NO_ID_GIVEN"))
          ).map { case (nodeA, nodeB) => graph.addEdge(nodeA, nodeB) }
      }
    graph
  }
}
