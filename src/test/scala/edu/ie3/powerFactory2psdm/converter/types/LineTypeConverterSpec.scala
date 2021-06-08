///*
// * © 2021. TU Dortmund University,
// * Institute of Energy Systems, Energy Efficiency and Energy Economics,
// * Research group Distribution grid planning and operation
// */
//
//package edu.ie3.powerFactory2psdm.converter.types
//
//import edu.ie3.datamodel.models.StandardUnits
//import edu.ie3.powerFactory2psdm.common.ConverterTestData
//import edu.ie3.powerFactory2psdm.exception.pf.ElementConfigurationException
//import edu.ie3.powerFactory2psdm.model.powerfactory.RawGridModel.LineTypes
//import org.scalatest.matchers.should.Matchers
//import org.scalatest.wordspec.AnyWordSpecLike
//import tech.units.indriya.quantity.Quantities
//
//class LineTypeConverterSpec
//    extends Matchers
//    with AnyWordSpecLike
//    with ConverterTestData {
//
//  "The line type converter" should {
//    val testLineType = LineTypes(
//      id = Some("testLineType"),
//      bline = Some(151.51515197753906),
//      gline = Some(1.543),
//      rline = Some(6.753542423248291),
//      sline = Some(1.0),
//      uline = Some(132.0),
//      xline = Some(20.61956214904785)
//    )
//
//    "should throw an exception if there is no phase to ground conductance defined" in {
//      val faultyLineType = testLineType.copy(bline = None)
//      val thrown = intercept[ElementConfigurationException](
//        LineTypeConverter.convert(faultyLineType)
//      )
//      thrown.getMessage shouldBe s"There is no phase-to-ground condcutance defined for line type: testLineType"
//    }
//
//    "should throw an exception if there is no phase to ground susceptance defined" in {
//      val faultyLineType = testLineType.copy(gline = None)
//      val thrown = intercept[ElementConfigurationException](
//        LineTypeConverter.convert(faultyLineType)
//      )
//      thrown.getMessage shouldBe s"There is no phase-to-ground susceptance defined for line type: testLineType"
//    }
//
//    "should throw an exception if there is no specific resistance defined" in {
//      val faultyLineType = testLineType.copy(rline = None)
//      val thrown = intercept[ElementConfigurationException](
//        LineTypeConverter.convert(faultyLineType)
//      )
//      thrown.getMessage shouldBe s"There is no specific resistance defined for line type: testLineType"
//    }
//
//    "should throw an exception if there is no specific reactance defined" in {
//      val faultyLineType = testLineType.copy(xline = None)
//      val thrown = intercept[ElementConfigurationException](
//        LineTypeConverter.convert(faultyLineType)
//      )
//      thrown.getMessage shouldBe s"There is no specific reactance defined for line type: testLineType"
//    }
//
//    "should throw an exception if there is no maximum thermal current defined" in {
//      val faultyLineType = testLineType.copy(sline = None)
//      val thrown = intercept[ElementConfigurationException](
//        LineTypeConverter.convert(faultyLineType)
//      )
//      thrown.getMessage shouldBe s"There is no maximum thermal current defined for line type: testLineType"
//    }
//
//    "should throw an exception if there is no rated voltage defined" in {
//      val faultyLineType = testLineType.copy(uline = None)
//      val thrown = intercept[ElementConfigurationException](
//        LineTypeConverter.convert(faultyLineType)
//      )
//      thrown.getMessage shouldBe s"There is no rated voltage defined for line type: testLineType"
//    }
//
//    "convert a line type correctly" in {
//      val actual = LineTypeConverter.convert(testLineType)
//      actual.getB shouldBe Quantities.getQuantity(
//        151.51515197753906,
//        StandardUnits.ADMITTANCE_PER_LENGTH
//      )
//      actual.getG shouldBe Quantities.getQuantity(
//        1.543,
//        StandardUnits.ADMITTANCE_PER_LENGTH
//      )
//      actual.getR shouldBe Quantities.getQuantity(
//        6.753542423248291,
//        StandardUnits.IMPEDANCE_PER_LENGTH
//      )
//      actual.getX shouldBe Quantities.getQuantity(
//        20.61956214904785,
//        StandardUnits.IMPEDANCE_PER_LENGTH
//      )
//      actual.getiMax shouldBe Quantities.getQuantity(
//        1000,
//        StandardUnits.ELECTRIC_CURRENT_MAGNITUDE
//      )
//      actual.getvRated shouldBe Quantities.getQuantity(
//        132.0,
//        StandardUnits.RATED_VOLTAGE_MAGNITUDE
//      )
//    }
//
//  }
//
//}
