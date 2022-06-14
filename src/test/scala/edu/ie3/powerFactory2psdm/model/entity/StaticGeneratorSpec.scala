/**
 * © 2021. Johannes Hiry,
 **/

package edu.ie3.powerFactory2psdm.model.entity

import edu.ie3.powerFactory2psdm.config.ConversionConfigUtils.{
  BasicDataSource,
  LoadFlowSource
}
import edu.ie3.powerFactory2psdm.exception.pf.MissingParameterException
import edu.ie3.powerFactory2psdm.model.RawPfGridModel.StatGen
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
      pfRecap = Some(0.0),
      cCategory = Some("Statischer Generator")
    )

    "throw an exception if the id is missing" in {
      val faulty = input.copy(id = None)
      val exc =
        intercept[MissingParameterException](
          StaticGenerator.build(
            faulty,
            LoadFlowSource,
            LoadFlowSource
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
            LoadFlowSource,
            LoadFlowSource
          )
        )
      exc.getMessage shouldBe s"There is no id of a connected bus for static generator: $id"
    }

    "throw an exception if the rated power is missing" in {
      val faulty = input.copy(sgini = None)
      val exc =
        intercept[MissingParameterException](
          StaticGenerator.build(
            faulty,
            LoadFlowSource,
            LoadFlowSource
          )
        )
      exc.getMessage shouldBe s"There is no rated power [load flow] defined for static generator: $id"
    }

    "throw an exception if the cos phi value is missing" in {
      val faulty = input.copy(cosgini = None)
      val exc =
        intercept[MissingParameterException](
          StaticGenerator.build(
            faulty,
            LoadFlowSource,
            LoadFlowSource
          )
        )
      exc.getMessage shouldBe s"There is no cos phi [load flow] defined for static generator: $id"
    }

    "throw an exception if the inductive capacitive flag is missing" in {
      val faulty = input.copy(pfRecap = None)
      val exc =
        intercept[MissingParameterException](
          StaticGenerator.build(
            faulty,
            LoadFlowSource,
            LoadFlowSource
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
            LoadFlowSource,
            LoadFlowSource
          )
        )
      exc.getMessage shouldBe s"There is no category specifier defined for static generator: $id"
    }

    "build a correctly configured static generator utilising load flow params" in {
      val result = StaticGenerator.build(
        input,
        LoadFlowSource,
        LoadFlowSource
      )
      result.id shouldBe "someStatGen"
      result.busId shouldBe "someNode"
      result.sRated shouldBe 13
      result.cosPhi shouldBe 0.91
      result.indCapFlag shouldBe 0
    }

    "build a correctly configured static generator utilising basic data params" in {
      val result = StaticGenerator.build(
        input,
        BasicDataSource,
        BasicDataSource
      )
      result.id shouldBe "someStatGen"
      result.busId shouldBe "someNode"
      result.sRated shouldBe 11
      result.cosPhi shouldBe 0.84
      result.indCapFlag shouldBe 0
    }
  }
}
