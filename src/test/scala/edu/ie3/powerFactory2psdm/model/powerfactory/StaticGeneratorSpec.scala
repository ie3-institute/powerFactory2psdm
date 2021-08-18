/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.model.powerfactory

import edu.ie3.powerFactory2psdm.common.ConverterTestData
import edu.ie3.powerFactory2psdm.config.ConversionConfig
import edu.ie3.powerFactory2psdm.exception.pf.MissingParameterException
import edu.ie3.powerFactory2psdm.model.powerfactory.RawGridModel.StatGen
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class StaticGeneratorSpec extends Matchers with AnyWordSpecLike {

  "A static generator should" should {
    val id = "someStatGen"
    val input = StatGen(
      id = Some(id),
      busId = Some("someNode"),
      sgn = Some(11),
      sgini = Some(13),
      cosn = Some(0.84),
      cosgini = Some(0.91),
      pf_recap = Some(0.0),
      cCategory = Some("Statischer Generator")
    )

    "throw an exception if the id is missing" in {
      val faulty = input.copy(id = None)
      val exc =
        intercept[MissingParameterException](
          StaticGenerator.build(
            faulty,
            ConversionConfig.loadFlowSource,
            ConversionConfig.loadFlowSource
          )
        )
      exc.getMessage shouldBe s"There is no id for static generator: $faulty"
    }

    "throw an exception if the bus id is missing" in {
      val faulty = input.copy(busId = None)
      val exc =
        intercept[MissingParameterException](
          StaticGenerator.build(
            faulty,
            ConversionConfig.loadFlowSource,
            ConversionConfig.loadFlowSource
          )
        )
      exc.getMessage shouldBe s"There is no id of a connected bus for static generator: $id"
    }

    "throw an exception if the rated power is missing" in {
      val faulty = input.copy(sgn = None)
      val exc =
        intercept[MissingParameterException](
          StaticGenerator.build(
            faulty,
            ConversionConfig.loadFlowSource,
            ConversionConfig.loadFlowSource
          )
        )
      exc.getMessage shouldBe s"There is no rated power defined for static generator: $id"
    }

    "throw an exception if the cos phi value is missing" in {
      val faulty = input.copy(cosgini = None)
      val exc =
        intercept[MissingParameterException](
          StaticGenerator.build(
            faulty,
            ConversionConfig.loadFlowSource,
            ConversionConfig.loadFlowSource
          )
        )
      exc.getMessage shouldBe s"There is no cos phi defined for static generator: $id"
    }

    "throw an exception if the inductive capacitive flag is missing" in {
      val faulty = input.copy(pf_recap = None)
      val exc =
        intercept[MissingParameterException](
          StaticGenerator.build(
            faulty,
            ConversionConfig.loadFlowSource,
            ConversionConfig.loadFlowSource
          )
        )
      exc.getMessage shouldBe s"There is no inductive capacitive specifier defined for static generator: $id"
    }

    "throw an exception if category specifier is missing" in {
      val faulty = input.copy(cCategory = None)
      val exc =
        intercept[MissingParameterException](
          StaticGenerator.build(
            faulty,
            ConversionConfig.loadFlowSource,
            ConversionConfig.loadFlowSource
          )
        )
      exc.getMessage shouldBe s"There is no category specifier defined for static generator: $id"
    }

    "throw an exception if the sRatedSource is wrongly configured" in {
      val faulty = input.copy(cCategory = None)
      val exc =
        intercept[IllegalArgumentException](
          StaticGenerator
            .build(faulty, "some gibberish", ConversionConfig.loadFlowSource)
        )
      exc.getMessage shouldBe StaticGenerator.paramSourceException.getMessage
    }

    "throw an exception if the cosPhiSource is wrongly configured" in {
      val faulty = input.copy(cCategory = None)
      val exc =
        intercept[IllegalArgumentException](
          StaticGenerator
            .build(faulty, ConversionConfig.loadFlowSource, "some gibberish")
        )
      exc.getMessage shouldBe StaticGenerator.paramSourceException.getMessage
    }

    "build a correctly configured static generator" in {
      val result = StaticGenerator.build(
        input,
        ConversionConfig.loadFlowSource,
        ConversionConfig.loadFlowSource
      )
      result.id shouldBe "someStatGen"
      result.busId shouldBe "someNode"
      result.sRated shouldBe 13
      result.cosPhi shouldBe 0.91
      result.indCapFlag shouldBe 0
    }
  }
}
