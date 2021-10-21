/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.model.entity

import edu.ie3.powerFactory2psdm.exception.pf.MissingParameterException
import edu.ie3.powerFactory2psdm.model.RawPfGridModel.Loads
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class LoadSpec extends Matchers with AnyWordSpecLike {

  "A load" should {
    val id = "someLoad"
    val input = Loads(
      id = Some(id),
      bus1Id = Some("someNode"),
      u0 = Some(1d),
      pf_recap = Some(0d),
      coslini = Some(0.841241),
      slini = Some(10.2432)
    )

    "throw an exception building a node if the id is missing" in {
      val faulty = input.copy(id = None)
      val exc = intercept[MissingParameterException](Load.build(faulty))
      exc.getMessage shouldBe s"Load $faulty has no defined id."
    }

    "throw an exception building a node if the node id is missing" in {
      val faulty = input.copy(bus1Id = None)
      val exc = intercept[MissingParameterException](Load.build(faulty))
      exc.getMessage shouldBe s"Load $id has no defined bus"
    }

    "throw an exception building a node if the apparent power value is missing" in {
      val faulty = input.copy(slini = None)
      val exc = intercept[MissingParameterException](Load.build(faulty))
      exc.getMessage shouldBe s"Load $id has no defined apparent power"
    }

    "throw an exception building a node if the cos phi value is missing" in {
      val faulty = input.copy(coslini = None)
      val exc = intercept[MissingParameterException](Load.build(faulty))
      exc.getMessage shouldBe s"Load $id has no defined cosinus phi"
    }

    "throw an exception building a node if the inductive/capacitive specifier is missing" in {
      val faulty = input.copy(pf_recap = None)
      val exc = intercept[MissingParameterException](Load.build(faulty))
      exc.getMessage shouldBe s"Load $id has no defined inductive/capacitive specifier"
    }

    "be built correctly" in {
      val actual = Load.build(input)

      actual.id shouldBe "someLoad"
      actual.nodeId shouldBe "someNode"
      actual.s shouldBe 10.2432
      actual.cosphi shouldBe 0.841241
      actual.indCapFlag shouldBe 0d
    }

  }

}
