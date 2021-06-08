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
  * @param typId type identifier
  * @param nodeAId id of connected node
  * @param nodeBId id of connected node
  * @param length length of the line
  */
final case class Line(
    id: String,
    typId: String,
    nodeAId: String,
    nodeBId: String,
    length: Double
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
    val typId = rawLine.typId.getOrElse(
      throw MissingParameterException(s"Line: $id has no defined type")
    )
    val nodeAId = rawLine.bus1Id.getOrElse(
      throw MissingParameterException(s"Line: $id has no defined node a")
    )
    val nodeBId = rawLine.bus2Id.getOrElse(
      throw MissingParameterException(s"Line: $id has no defined node b")
    )
    val length = rawLine.dline.getOrElse(
      throw MissingParameterException(s"Line: $id has no defined length")
    )
    Line(
      id,
      typId,
      nodeAId,
      nodeBId,
      length
    )
  }
}
