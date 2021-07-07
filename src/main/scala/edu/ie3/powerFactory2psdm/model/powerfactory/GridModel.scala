/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.model.powerfactory

import com.typesafe.scalalogging.LazyLogging
import edu.ie3.powerFactory2psdm.exception.pf.{GridConfigurationException, MissingParameterException}
import edu.ie3.powerFactory2psdm.model.powerfactory.RawGridModel.{LineTypes, Lines, Nodes, PowerPlants, Switches, Trafos2w}

final case class GridModel(
    nodes: List[Node],
    lineTypes: List[LineType],
    lines: List[Line],
    switches: List[Switch],
    powerPlants: List[PowerPlant]
)

object GridModel extends LazyLogging {
  def build(rawGrid: RawGridModel): GridModel = {
    val rawNodes = rawGrid.nodes.getOrElse(
      throw GridConfigurationException("There are no nodes in the grid.")
    )
    val rawLines = rawGrid.lines.getOrElse({
      logger.debug("There are no lines in the grid.")
      List.empty[Lines]
    })
    val rawLineTypes = rawGrid.lineTypes.getOrElse({
      logger.debug("There are no lines in the grid.")
      List.empty[LineTypes]
    })
    val rawSwitches = rawGrid.switches.getOrElse({
      logger.debug("There are no switches in the grid.")
      List.empty[Switches]
    })
    val rawTrafos2W = rawGrid.trafos2w.getOrElse({
      logger.debug("There are no switches in the grid.")
      List.empty[Trafos2w]
    })
    val rawPowerPlants = rawGrid.powerPlants.getOrElse({
      logger.debug("There are no power plants in the grid")
      List.empty[PowerPlants]
    })

    val models = rawNodes ++ rawLines ++ rawLineTypes ++ rawSwitches ++ rawTrafos2W ++ rawPowerPlants
    val modelIds = models.map {
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
      case powerPlant: PowerPlants =>
        powerPlant.id.getOrElse(
          throw MissingParameterException(
            s"Power plant $powerPlant has no defined id"
          )
        )
    }
    val uniqueIds = modelIds.distinct
    if (uniqueIds.size < modelIds.size) {
      val duplicateIds = modelIds.diff(uniqueIds)
      throw GridConfigurationException(
        s"Can't build grid as there are grid elements with duplicated ids: $duplicateIds"
      )
    }

    val nodes = rawNodes.map(Node.build)
    val lines = rawLines.map(line => Line.build(line))
    val lineTypes = rawLineTypes.map(LineType.build)
    val switches = rawSwitches.flatMap(Switch.maybeBuild)
    val powerPlants = rawPowerPlants.map(PowerPlant.build)

    GridModel(
      nodes,
      lineTypes,
      lines,
      switches,
      powerPlants
    )
  }

}
