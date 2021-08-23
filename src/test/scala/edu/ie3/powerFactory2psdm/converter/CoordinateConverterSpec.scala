/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter

import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.util.geo.GeoUtils
import org.locationtech.jts.geom.{Coordinate, Point}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class CoordinateConverterSpec extends Matchers with AnyWordSpecLike {

  "A coordinate converter" should {

    "convert the given values to the proper coordinate" in {
      val maybeTestLat = Some(40.415634765229235)
      val maybeTestLon = Some(-3.7071948736763316)
      val actual = CoordinateConverter.convert(maybeTestLat, maybeTestLon)
      val expected = GeoUtils.DEFAULT_GEOMETRY_FACTORY.createPoint(
        new Coordinate(-3.7071948736763316, 40.415634765229235)
      )
      actual shouldBe expected
    }

    "return the default geo position for default PowerFactory values" in {
      val maybeDefaultLat = Some(0.0)
      val maybeDefaultLon = Some(0.0)
      CoordinateConverter.convert(
        maybeDefaultLat,
        maybeDefaultLon
      ) shouldBe NodeInput.DEFAULT_GEO_POSITION
    }

    "return the default geo position when given None" in {
      val actual = CoordinateConverter.convert(None, None)
      actual shouldBe NodeInput.DEFAULT_GEO_POSITION
    }
  }
}
