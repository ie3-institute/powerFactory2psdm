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

  /**
    * Converts optional lat and lon values of PowerFactory elements to the expected position description of the PSDM.
    *
    * @param maybeLat optional lat value
    * @param maybeLon optional lon value
    * @return position as described by a [[Point]]
    */
  def convert(maybeLat: Option[Double], maybeLon: Option[Double]): Point = {
    maybeLat.zip(maybeLon) match {
      // if no coord is specifically set in the power factory grid, lat and lon are 0.0
      case Some((0.0, 0.0)) =>
        NodeInput.DEFAULT_GEO_POSITION
      case Some((lat, lon)) =>
        GeoUtils.DEFAULT_GEOMETRY_FACTORY.createPoint(new Coordinate(lon, lat))
      case None =>
        NodeInput.DEFAULT_GEO_POSITION
    }
  }
}
