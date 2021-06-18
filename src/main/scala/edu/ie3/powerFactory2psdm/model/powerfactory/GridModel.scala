/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.model.powerfactory

import com.typesafe.scalalogging.LazyLogging
import edu.ie3.powerFactory2psdm.exception.pf.{
  ElementConfigurationException,
  MissingGridElementException
}
import edu.ie3.powerFactory2psdm.model.powerfactory.RawGridModel.{
  Lines,
  Nodes,
  Switches
}
import org.apache.logging.log4j.core.tools.picocli.CommandLine.TypeConversionException

import scala.annotation.tailrec

final case class GridModel(
    nodes: List[Node],
    lineTypes: List[LineType],
    lines: List[Line],
    switches: List[Switch]
)

object GridModel extends LazyLogging {
  def build(rawGrid: RawGridModel): GridModel = {
    val (nodes, takenIds) = buildModels(
      rawGrid.nodes
        .getOrElse(
          throw MissingGridElementException("There are no nodes in the grid.")
        ),
      Set.empty
    )
    val lineTypes = rawGrid.lineTypes match {
      case Some(lineTypes) => lineTypes.map(LineType.build)
      case None =>
        logger.debug("There are no lines in the grid.")
        List[LineType]()
    }
    val (lines, takenIds2) = rawGrid.lines match {
      case Some(lines) => Line.buildLines(lines, takenIds)
      case None =>
        logger.debug("There are no lines in the grid.")
        (List[Line](), takenIds)
    }
    val (switches, takenIds3) = rawGrid.switches match {
      case Some(switches) =>
        val (switches, taken) = Switch.buildSwitches(switches, takenIds2)
        (switches.flatten, taken)
      case None =>
        logger.debug("There are no switches in the grid.")
        (List[Switch](), takenIds2)
    }

    GridModel(
      nodes,
      lineTypes,
      lines,
      switches
    )
  }

// TODO how the fuck do i generalize the building of models while checking for uniqueness of ids ???
  def buildModels[C <: EntityModel](
      rawModels: List[Any],
      takenIds: Set[String],
      models: List[C] = List()
  ): (List[C], Set[String]) = {
    if (rawModels.isEmpty) {
      return (models, takenIds)
    }
    val rawModel = rawModels.head
    rawModel match {
      case node: Nodes =>
        val id = node.id.getOrElse(
          throw ElementConfigurationException(s"There is no id for node $node")
        )
        throwForTakenId(takenIds, id)
        buildModels(
          rawModels.tail,
          takenIds + id,
          Node.build(node) :: models
        )
      case line: Lines => ???
      case _           => throw new IllegalArgumentException("")
    }

  }

  def throwForTakenId(takenIds: Set[String], id: String) =
    if (takenIds contains id)
      throw ElementConfigurationException(s"ID: $id is not unique")

}
