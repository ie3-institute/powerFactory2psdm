/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter.types

import edu.ie3.powerFactory2psdm.common.ConverterTestData.getLineType
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class LineTypeConverterSpec extends Matchers with AnyWordSpecLike {

  "The line type converter" should {
    val conversionPair = getLineType("someLineType")

    "convert a line type correctly" in {
      val actual = LineTypeConverter.convert(conversionPair.input)
      val expected = conversionPair.result

      actual.getB shouldBe expected.getB
      actual.getG shouldBe expected.getG
      actual.getR shouldBe expected.getR
      actual.getX shouldBe expected.getX
      actual.getiMax shouldBe expected.getiMax
      actual.getvRated shouldBe expected.getvRated
    }

  }

}
