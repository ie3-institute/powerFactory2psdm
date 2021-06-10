/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.model.powerfactory

import edu.ie3.powerFactory2psdm.exception.pf.{
  ElementConfigurationException,
  MissingParameterException
}
import edu.ie3.powerFactory2psdm.model.powerfactory.RawGridModel.Lines

/**
  * Electrical line
  *
  * @param id identifier
  * @param nodeAId id of connected node
  * @param nodeBId id of connected node
  */
final case class Line(
    id: String,
    nodeAId: String,
    nodeBId: String
) extends EntityModel
    with Edge

object Line {
  def build(rawLine: Lines): Line = {
    val id = rawLine.id match {
      case Some(id) if EntityModel.isUniqueId(id) => id
      case Some(id) =>
        throw ElementConfigurationException(s"ID: $id is not unique")
      case None =>
        throw MissingParameterException(s"There is no id for line $rawLine")
    }
    val nodeAId = rawLine.bus1Id.getOrElse(
      throw MissingParameterException(s"Line: $id has no defined node a")
    )
    val nodeBId = rawLine.bus2Id.getOrElse(
      throw MissingParameterException(s"Line: $id has no defined node b")
    )
    Line(
      id,
      nodeAId,
      nodeBId
    )
  }
}
