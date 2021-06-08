package edu.ie3.powerFactory2psdm.model.powerfactory

import edu.ie3.powerFactory2psdm.common.ConverterTestData
import edu.ie3.powerFactory2psdm.exception.pf.{MissingParameterException, TestException}
import edu.ie3.powerFactory2psdm.model.powerfactory.RawGridModel.{LineTypes, Switches}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class LineTypeSpec extends Matchers with AnyWordSpecLike with ConverterTestData {
  "A line type" should {

    val id = "SomeSwitch.ElmCoup"

    val input = LineTypes(
      id = Some("testLineType"),
      uline = Some(132.0),
      sline = Some(1.0),
      rline = Some(6.753542423248291),
      xline = Some(20.61956214904785),
      bline = Some(151.51515197753906),
      gline = Some(1.543)
    )
    "throw an exception when building if the id is missing" in {
      val lineType = input.copy(id = None)
      val exc = intercept[MissingParameterException](LineType.build(lineType))
      exc.getMessage shouldBe s"There is no id for line type $lineType"
    }

    "throw an exception when building if no rated voltage is defined" in {
      val id = "BrokenLineType1"
      val lineType = input.copy(id = Some(id), uline = None)
      val exc = intercept[MissingParameterException](LineType.build(lineType))
      exc.getMessage shouldBe s"There is no rated voltage defined for line type: $id"
    }

    "throw an exception when building no thermal current is defined" in {
      val id = "BrokenLineType2"
      val lineType = input.copy(id = Some(id), sline = None)
      val exc = intercept[MissingParameterException](LineType.build(lineType))
      exc.getMessage shouldBe s"There is no maximum thermal current defined for line type: $id"
    }

    "throw an exception when building if no specific resistance is defined" in {
      val id = "BrokenLineType3"
      val lineType = input.copy(id = Some(id), rline = None)
      val exc = intercept[MissingParameterException](LineType.build(lineType))
      exc.getMessage shouldBe  s"There is no specific resistance defined for line type: $id"
    }

    "throw an exception when building if no specific reactance is defined" in {
      val id = "BrokenLineType4"
      val lineType = input.copy(id = Some(id), xline = None)
      val exc = intercept[MissingParameterException](LineType.build(lineType))
      exc.getMessage shouldBe  s"There is no specific reactance defined for line type: $id"
    }

    "throw an exception when building if no phase-to-ground conductance is defined" in {
      val id = "BrokenLineType5"
      val lineType = input.copy(id = Some(id), bline = None)
      val exc = intercept[MissingParameterException](LineType.build(lineType))
      exc.getMessage shouldBe  s"There is no phase-to-ground conductance defined for line type: $id"
    }

    "throw an exception when building if no phase-to-ground susceptance is defined" in {
      val id = "BrokenLineType6"
      val lineType = input.copy(id = Some(id), gline = None)
      val exc = intercept[MissingParameterException](LineType.build(lineType))
      exc.getMessage shouldBe  s"There is no phase-to-ground susceptance defined for line type: $id"
    }
  }
}