/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.model.entity

import edu.ie3.powerFactory2psdm.exception.pf.MissingParameterException
import edu.ie3.powerFactory2psdm.model.RawPfGridModel.{Loads, LoadsLV, LoadsMV}
import edu.ie3.powerFactory2psdm.model.setting.ConversionPrefixes.ConversionPrefix
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class LoadSpec extends Matchers with AnyWordSpecLike {

  "A load" when {

    "being built with a regular load" should {
      val id = "someLoad"
      val input = Loads(
        id = Some(id),
        busId = Some("someNode"),
        pf_recap = Some(0d),
        coslini = Some(0.841241),
        slini = Some(10.2432),
        i_scale = Some(1),
        scale0 = Some(0.9)
      )
      val conversionPrefix = ConversionPrefix(1)

      "throw an exception building a node if the id is missing" in {
        val faulty = input.copy(id = None)
        val exc = intercept[MissingParameterException](
          Load.build(faulty, conversionPrefix)
        )
        exc.getMessage shouldBe s"Load $faulty has no defined id."
      }

      "throw an exception building a node if the node id is missing" in {
        val faulty = input.copy(busId = None)
        val exc = intercept[MissingParameterException](
          Load.build(faulty, conversionPrefix)
        )
        exc.getMessage shouldBe s"Load $id has no defined bus"
      }

      "throw an exception building a node if the apparent power value is missing" in {
        val faulty = input.copy(slini = None)
        val exc = intercept[MissingParameterException](
          Load.build(faulty, conversionPrefix)
        )
        exc.getMessage shouldBe s"Load $id has no defined apparent power"
      }

      "throw an exception building a node if the cos phi value is missing" in {
        val faulty = input.copy(coslini = None)
        val exc = intercept[MissingParameterException](
          Load.build(faulty, conversionPrefix)
        )
        exc.getMessage shouldBe s"Load $id has no defined cosinus phi"
      }

      "throw an exception building a node if the inductive/capacitive specifier is missing" in {
        val faulty = input.copy(pf_recap = None)
        val exc = intercept[MissingParameterException](
          Load.build(faulty, conversionPrefix)
        )
        exc.getMessage shouldBe s"Load $id has no defined inductive/capacitive specifier"
      }

      "be built correctly" in {
        val actual = Load.build(input, conversionPrefix)

        actual.id shouldBe "someLoad"
        actual.nodeId shouldBe "someNode"
        actual.s shouldBe 10.2432
        actual.cosphi shouldBe 0.841241
        actual.indCapFlag shouldBe 0d
      }

    }

    "being built with a lv load" should {

      val id = "someLoad"
      val input = LoadsLV(
        id = Some(id),
        busId = Some("someNode"),
        pf_recap = Some(0d),
        coslini = Some(0.841241),
        slini = Some(10.2432),
        i_scale = Some(1),
        scale0 = Some(0.9)
      )

      "throw an exception building a node if the id is missing" in {
        val faulty = input.copy(id = None)
        val exc = intercept[MissingParameterException](Load.build(faulty))
        exc.getMessage shouldBe s"LV Load $faulty has no defined id."
      }

      "throw an exception building a node if the node id is missing" in {
        val faulty = input.copy(busId = None)
        val exc = intercept[MissingParameterException](Load.build(faulty))
        exc.getMessage shouldBe s"LV Load $id has no defined bus"
      }

      "throw an exception building a node if the apparent power value is missing" in {
        val faulty = input.copy(slini = None)
        val exc = intercept[MissingParameterException](Load.build(faulty))
        exc.getMessage shouldBe s"LV Load $id has no defined apparent power"
      }

      "throw an exception building a node if the cos phi value is missing" in {
        val faulty = input.copy(coslini = None)
        val exc = intercept[MissingParameterException](Load.build(faulty))
        exc.getMessage shouldBe s"LV Load $id has no defined cosinus phi"
      }

      "throw an exception building a node if the inductive/capacitive specifier is missing" in {
        val faulty = input.copy(pf_recap = None)
        val exc = intercept[MissingParameterException](Load.build(faulty))
        exc.getMessage shouldBe s"LV Load $id has no defined inductive/capacitive specifier"
      }

      "be built correctly" in {
        val actual = Load.build(input)

        actual.id shouldBe "someLoad"
        actual.nodeId shouldBe "someNode"
        actual.s shouldBe 10243.2
        actual.cosphi shouldBe 0.841241
        actual.indCapFlag shouldBe 0d
      }

    }

    "being built with a MV load" should {
      val id = "someLoad"
      val input = LoadsMV(
        id = Some(id),
        busId = Some("someNode"),
        pf_recap = Some(0d),
        coslini = Some(0.841241),
        slini = Some(10.2432),
        i_scale = Some(1),
        scale0 = Some(0.9)
      )
      val conversionPrefix = ConversionPrefix(1)

      "throw an exception building a node if the id is missing" in {
        val faulty = input.copy(id = None)
        val exc = intercept[MissingParameterException](
          Load.build(faulty, conversionPrefix)
        )
        exc.getMessage shouldBe s"MV Load $faulty has no defined id."
      }

      "throw an exception building a node if the node id is missing" in {
        val faulty = input.copy(busId = None)
        val exc = intercept[MissingParameterException](
          Load.build(faulty, conversionPrefix)
        )
        exc.getMessage shouldBe s"MV Load $id has no defined bus"
      }

      "throw an exception building a node if the apparent power value is missing" in {
        val faulty = input.copy(slini = None)
        val exc = intercept[MissingParameterException](
          Load.build(faulty, conversionPrefix)
        )
        exc.getMessage shouldBe s"MV Load $id has no defined apparent power"
      }

      "throw an exception building a node if the cos phi value is missing" in {
        val faulty = input.copy(coslini = None)
        val exc = intercept[MissingParameterException](
          Load.build(faulty, conversionPrefix)
        )
        exc.getMessage shouldBe s"MV Load $id has no defined cosinus phi"
      }

      "throw an exception building a node if the inductive/capacitive specifier is missing" in {
        val faulty = input.copy(pf_recap = None)
        val exc = intercept[MissingParameterException](
          Load.build(faulty, conversionPrefix)
        )
        exc.getMessage shouldBe s"MV Load $id has no defined inductive/capacitive specifier"
      }

      "be built correctly" in {
        val actual = Load.build(input, conversionPrefix)

        actual.id shouldBe "someLoad"
        actual.nodeId shouldBe "someNode"
        actual.s shouldBe 10.2432
        actual.cosphi shouldBe 0.841241
        actual.indCapFlag shouldBe 0d
      }
    }
  }
}
