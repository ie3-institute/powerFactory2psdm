/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.model.entity

import edu.ie3.powerFactory2psdm.exception.pf.MissingParameterException
import edu.ie3.powerFactory2psdm.model.RawPfGridModel.Lines

/** Electrical line
  *
  * @param id
  *   identifier
  * @param nodeAId
  *   id of connected node
  * @param nodeBId
  *   id of connected node
  * @param typeId
  *   id of the corresponding line type
  * @param lineSections
  *   optional list of line sections the line consists of
  * @param length
  *   length of the line
  * @param gpsCoords
  *   optional list of gps coordinates of geo position of the line
  */
final case class Line(
    id: String,
    nodeAId: String,
    nodeBId: String,
    typeId: Option[String],
    lineSections: Option[List[LineSection]],
    length: Double,
    gpsCoords: Option[List[(Double, Double)]]
) extends EntityModel
    with Edge

object Line {
  def build(
      rawLine: Lines,
      lineSectionsMap: Map[String, List[LineSection]]
  ): Line = {
    val id = rawLine.id.getOrElse(
      throw MissingParameterException(s"There is no id for line $rawLine")
    )
    val nodeAId = rawLine.bus1Id.getOrElse(
      throw MissingParameterException(s"Line: $id has no defined node a")
    )
    val nodeBId = rawLine.bus2Id.getOrElse(
      throw MissingParameterException(s"Line: $id has no defined node b")
    )
    val typId = rawLine.typeId
    val lineSections = lineSectionsMap.get(id)
    val length = rawLine.dline.getOrElse(
      throw MissingParameterException(
        s"Line: $id has no defined length"
      )
    )
    val gpsCoords: Option[List[(Double, Double)]] = rawLine.GPScoords match {
      case Some(List(Some(Nil))) => None
      case Some(coords) =>
        Option(coords.flatten.map { case List(Some(lat), Some(lon)) =>
          (lat, lon)
        })
      case None => None
    }

    Line(
      id,
      nodeAId,
      nodeBId,
      typId,
      lineSections,
      length,
      gpsCoords
    )
  }
}
