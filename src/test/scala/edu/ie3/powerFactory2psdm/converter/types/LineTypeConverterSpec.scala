/**
 * © 2021. Johannes Hiry,
 **/

package edu.ie3.powerFactory2psdm.converter.types

import edu.ie3.datamodel.models.input.connector.`type`.LineTypeInput
import edu.ie3.powerFactory2psdm.common.ConverterTestData.getLineTypePair
import edu.ie3.powerFactory2psdm.model.entity.LineSection
import edu.ie3.powerFactory2psdm.util.QuantityUtils.RichQuantityDouble
import edu.ie3.scalatest.QuantityMatchers.equalWithTolerance
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

import java.util.UUID

class LineTypeConverterSpec extends Matchers with AnyWordSpecLike {

  "The line type converter" should {
    val conversionPair = getLineTypePair("someLineType")

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

    "generate a line type from line sections" in {
      val lineTypes = Map(
        "lineTypeA" -> new LineTypeInput(
          UUID.randomUUID(),
          "lineTypeA",
          1.asMicroSiemensPerKilometre,
          1.asMicroSiemensPerKilometre,
          1.asOhmPerKilometre,
          1.asOhmPerKilometre,
          1.asKiloAmpere,
          20.asKiloVolt
        ),
        "lineTypeB" -> new LineTypeInput(
          UUID.randomUUID(),
          "lineTypeB",
          2.asMicroSiemensPerKilometre,
          2.asMicroSiemensPerKilometre,
          2.asOhmPerKilometre,
          2.asOhmPerKilometre,
          2.asKiloAmpere,
          20.asKiloVolt
        )
      )
      val lineSections = List(
        LineSection(
          id = "lineSectionA",
          length = 1,
          typeId = "lineTypeA"
        ),
        LineSection(
          id = "lineSectionB",
          length = 1,
          typeId = "lineTypeB"
        )
      )

      implicit val quantityTolerance: Double = 1e-6

      val actual =
        LineTypeConverter.convert("testLine", 2, lineSections, lineTypes)
      actual.getB should equalWithTolerance(1.5.asMicroSiemensPerKilometre)
      actual.getG should equalWithTolerance(1.5.asMicroSiemensPerKilometre)
      actual.getR should equalWithTolerance(1.5.asOhmPerKilometre)
      actual.getX should equalWithTolerance(1.5.asOhmPerKilometre)
      actual.getiMax should equalWithTolerance(1000.asAmpere)
      actual.getvRated should equalWithTolerance(20.asKiloVolt)
    }
  }
}
