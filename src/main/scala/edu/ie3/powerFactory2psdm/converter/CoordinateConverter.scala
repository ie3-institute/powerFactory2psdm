/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter

import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.util.geo.GeoUtils
import org.locationtech.jts.geom.{Coordinate, Point}

case object CoordinateConverter {

  def convert(maybeLat: Option[Double], maybeLon: Option[Double]): Point = {
    val maybePoint: Option[Point] = for {
      lat <- maybeLat
      lon <- maybeLon
    } yield GeoUtils.DEFAULT_GEOMETRY_FACTORY.createPoint(
      new Coordinate(lat, lon)
    )

    maybePoint.getOrElse(NodeInput.DEFAULT_GEO_POSITION)
  }
}
