/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.model.powerfactory

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
      cosgini = Some(0.91),
      pf_recap = Some(0.0)
    )

    "throw an exception if the id is missing" in {
      val faulty = input.copy(id = None)
      val exc =
        intercept[MissingParameterException](StaticGenerator.build(faulty))
      exc.getMessage shouldBe s"There is no id for static generator: $faulty"
    }

    "throw an exception if the bus id is missing" in {
      val faulty = input.copy(busId = None)
      val exc =
        intercept[MissingParameterException](StaticGenerator.build(faulty))
      exc.getMessage shouldBe s"There is no id of a connected bus for static generator: $id"
    }

    "throw an exception if the rated power is missing" in {
      val faulty = input.copy(sgn = None)
      val exc =
        intercept[MissingParameterException](StaticGenerator.build(faulty))
      exc.getMessage shouldBe s"There is no rated power defined for static generator: $id"
    }

    "throw an exception if the cos phi value is missing" in {
      val faulty = input.copy(cosgini = None)
      val exc =
        intercept[MissingParameterException](StaticGenerator.build(faulty))
      exc.getMessage shouldBe s"There is no cos phi defined for static generator: $id"
    }

    "throw an exception if the inductive capacitive flag is missing" in {
      val faulty = input.copy(pf_recap = None)
      val exc =
        intercept[MissingParameterException](StaticGenerator.build(faulty))
      exc.getMessage shouldBe s"There is no inductive capacitive specifier defined for static generator: $id"
    }

    "build a correctly configured static generator" in {
      val result = StaticGenerator.build(input)
      result.id shouldBe "SomeStatGen"
      result.busId shouldBe "SomeNode"
      result.sRated shouldBe 11
      result.cosPhi shouldBe 0.91
      result.indCapFlag shouldBe 0
    }
  }
}
