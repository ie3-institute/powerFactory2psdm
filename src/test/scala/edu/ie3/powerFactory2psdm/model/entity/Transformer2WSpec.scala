/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.model.entity

import edu.ie3.powerFactory2psdm.exception.pf.MissingParameterException
import edu.ie3.powerFactory2psdm.model.RawPfGridModel.Trafos2w
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class Transformer2WSpec extends Matchers with AnyWordSpecLike {

  "A transformer" should {

    val id = "SomeTransformer"
    val input = Trafos2w(
      id = Some(id),
      busLvId = Some("SomeLvNode"),
      busHvId = Some("SomeHvNode"),
      typeId = Some("SomeTransformerType"),
      nntap = Some(-1.0),
      ntrcn = Some(1.0),
      cPtapc = None
    )

    "throw an exception if the id is missing" in {
      val trafo = input.copy(id = None)
      val exc = intercept[MissingParameterException](Transformer2W.build(trafo))
      exc.getMessage shouldBe s"There is no id for 2w trafo: $trafo"
    }

    "throw an exception if the lv bus id is missing" in {
      val trafo = input.copy(busLvId = None)
      val exc = intercept[MissingParameterException](Transformer2W.build(trafo))
      exc.getMessage shouldBe s"Trafo2w: $id has no lv bus id."
    }

    "throw an exception if the hv bus id is missing" in {
      val trafo = input.copy(busHvId = None)
      val exc = intercept[MissingParameterException](Transformer2W.build(trafo))
      exc.getMessage shouldBe s"Trafo2w: $id has no hv bus id."
    }

    "throw an exception if the type id is missing" in {
      val trafo = input.copy(typeId = None)
      val exc = intercept[MissingParameterException](Transformer2W.build(trafo))
      exc.getMessage shouldBe s"Trafo2w: $id has no type. Transformer conversion without specified types is not supported."
    }

    "throw an exception if the tap pos is missing" in {
      val trafo = input.copy(nntap = None)
      val exc = intercept[MissingParameterException](Transformer2W.build(trafo))
      exc.getMessage shouldBe s"Trafo2w: $id has no tap position."
    }

    "throw an exception if the auto tap signifier is missing" in {
      val trafo = input.copy(ntrcn = None)
      val exc = intercept[MissingParameterException](Transformer2W.build(trafo))
      exc.getMessage shouldBe s"Trafo2w: $id has no auto tap signifier."
    }

    "build a transformer model correctly" in {
      val trafo = Transformer2W.build(input)
      trafo.id shouldBe "SomeTransformer"
      trafo.typeId shouldBe "SomeTransformerType"
      trafo.nodeLvId shouldBe "SomeLvNode"
      trafo.nodeHvId shouldBe "SomeHvNode"
      trafo.tapPos shouldBe -1
      trafo.autoTap shouldBe 1
      trafo.extTapControl shouldBe None
    }

  }

}
