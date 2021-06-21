/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.model.powerfactory

import com.typesafe.scalalogging.LazyLogging
import edu.ie3.powerFactory2psdm.model.powerfactory.types.LineType
import edu.ie3.powerFactory2psdm.exception.pf.{
  GridConfigurationException,
  MissingParameterException
}
import edu.ie3.powerFactory2psdm.model.powerfactory.RawGridModel.{
  LineTypes,
  Lines,
  Nodes,
  Switches,
  Trafos2w
}

final case class GridModel(
    nodes: List[Node],
    lineTypes: List[LineType],
    lines: List[Line],
    switches: List[Switch]
)

object GridModel extends LazyLogging {
  def build(rawGrid: RawGridModel): GridModel = {

    val models =
      (rawGrid.nodes ++ rawGrid.lines ++ rawGrid.switches ++ rawGrid.trafos2w).flatten
    val modelIds: Iterable[String] = models.map {
      case node: Nodes =>
        node.id.getOrElse(
          throw MissingParameterException(s"Node $node has no defined id")
        )
      case line: Lines =>
        line.id.getOrElse(
          throw MissingParameterException(s"Line $line has no defined id")
        )
      case lineType: LineTypes =>
        lineType.id.getOrElse(
          throw MissingParameterException(
            s"Line type $lineType has no defined id"
          )
        )
      case switch: Switches =>
        switch.id.getOrElse(
          throw MissingParameterException(s"Switch $switch has no defined id")
        )
      case trafo2w: Trafos2w =>
        trafo2w.id.getOrElse(
          throw MissingParameterException(
            s"Transformer $trafo2w has no defined id"
          )
        )
    }
    val duplicateIds =
      modelIds.groupBy(identity).collect { case (x, List(_, _, _*)) => x }
    if (duplicateIds.nonEmpty) {
      throw GridConfigurationException(
        s"Can't build grid as there are grid elements with duplicated ids: $duplicateIds"
      )
    }

    val nodes = rawGrid.nodes match {
      case Some(nodes) => nodes.map(Node.build)
      case None =>
        throw GridConfigurationException("There are no nodes in the grid.")
    }
    val lines = rawGrid.lines match {
      case Some(lines) => lines.map(Line.build)
      case None =>
        logger.debug("There are no lines in the grid.")
        List.empty
    }
    val lineTypes = rawGrid.lineTypes match {
      case Some(lineTypes) => lineTypes.map(LineType.build)
      case None =>
        logger.debug("There are no lines in the grid.")
        List.empty
    }
    val switches = rawGrid.switches match {
      case Some(switches) => switches.flatMap(Switch.maybeBuild)
      case None =>
        logger.debug("There are no switches in the grid.")
        List.empty
    }

    GridModel(
      nodes,
      lineTypes,
      lines,
      switches
    )
  }
}
