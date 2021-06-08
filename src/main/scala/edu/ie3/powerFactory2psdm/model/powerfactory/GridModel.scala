/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.model.powerfactory

import com.typesafe.scalalogging.LazyLogging
import edu.ie3.powerFactory2psdm.exception.pf.MissingGridElementException

final case class GridModel(
    nodes: List[Node],
    lineTypes: List[LineType],
    lines: List[Line],
    switches: List[Switch]
)

object GridModel extends LazyLogging {
  def build(rawGrid: RawGridModel): GridModel = {
    val nodes = rawGrid.nodes
      .getOrElse(
        throw MissingGridElementException("There are no nodes in the grid.")
      )
      .map(Node.build)
    val lineTypes = rawGrid.lineTypes match {
      case Some(lineTypes) => lineTypes.map(LineType.build)
      case None =>
        logger.debug("There are no lines in the grid.")
        List[LineType]()
    }
    val lines = rawGrid.lines match {
      case Some(lines) => lines.map(Line.build)
      case None =>
        logger.debug("There are no lines in the grid.")
        List[Line]()
    }
    val switches = rawGrid.switches match {
      case Some(switches) => switches.flatMap(Switch.maybeBuild)
      case None =>
        logger.debug("There are no switches in the grid.")
        List[Switch]()
    }

    GridModel(
      nodes,
      lineTypes,
      lines,
      switches
    )
  }
}
