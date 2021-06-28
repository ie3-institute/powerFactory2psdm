/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.model.powerfactory

import edu.ie3.powerFactory2psdm.exception.pf.MissingParameterException
import edu.ie3.powerFactory2psdm.model.powerfactory.RawGridModel.TrafoTypes2w
import edu.ie3.powerFactory2psdm.model.powerfactory.types.Transformer2wType
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class Transformer2wTypeSpec extends Matchers with AnyWordSpecLike {

  "A Transformer2wTypeSpec" should {

    val id = "Some2wTransformerType"

    val input = TrafoTypes2w(
      id = Some(id),
      strn = Some(40),
      utrn_h = Some(10),
      utrn_l = Some(0.4),
      dutap = Some(1.2),
      phitr = Some(12),
      tap_side = Some(0),
      nntap0 = Some(-1),
      ntpmn = Some(-3),
      ntpmx = Some(5),
      uktr = Some(5.1345),
      curmg = Some(1),
      pfe = Some(10),
      pcutr = Some(13)
    )

    "throw an exception when trying to build a TransformerType without an id" in {
      val faulty1 = input.copy(id = None)
      val exc = intercept[MissingParameterException](
        Transformer2wType.build(faulty1)
      )
      exc.getMessage shouldBe s"There is no id for transformer-type: $faulty1"
    }

    "throw an exception when trying to build a TransformerType without parameter strn" in {
      val faulty2 = input.copy(strn = None)
      val exc = intercept[MissingParameterException](
        Transformer2wType.build(faulty2)
      )
      exc.getMessage shouldBe s"There is no rated apparent power for transformer-type: $id"
    }

    "throw an exception when trying to build a TransformerType without parameter utrn_h" in {
      val faulty3 = input.copy(utrn_h = None)
      val exc = intercept[MissingParameterException](
        Transformer2wType.build(faulty3)
      )
      exc.getMessage shouldBe s"There is no voltage of high winding side for transformer-type: $id"
    }

    "throw an exception when trying to build a TransformerType without parameter utrn_l" in {
      val faulty4 = input.copy(utrn_l = None)
      val exc = intercept[MissingParameterException](
        Transformer2wType.build(faulty4)
      )
      exc.getMessage shouldBe s"There is no voltage of low winding side for transformer-type: $id"
    }

    "throw an exception when trying to build a TransformerType without parameter dutap" in {
      val faulty5 = input.copy(dutap = None)
      val exc = intercept[MissingParameterException](
        Transformer2wType.build(faulty5)
      )
      exc.getMessage shouldBe s"There is no voltage magnitude deviation per tap position for transfomer type: $id"
    }

    "throw an exception when trying to build a TransformerType without parameter dV" in {
      val faulty6 = input.copy(phitr = None)
      val exc = intercept[MissingParameterException](
        Transformer2wType.build(faulty6)
      )
      exc.getMessage shouldBe s"There is no voltage angle deviation per tap position for transformer-type: $id"
    }

    "throw an exception when trying to build a TransformerType without parameter tap_side" in {
      val faulty7 = input.copy(tap_side = None)
      val exc = intercept[MissingParameterException](
        Transformer2wType.build(faulty7)
      )
      exc.getMessage shouldBe s"There is no selection of winding where tap changer is installed for transformer-type: $id"
    }

    "throw an exception when trying to build a TransformerType without parameter nntap0" in {
      val faulty8 = input.copy(nntap0 = None)
      val exc = intercept[MissingParameterException](
        Transformer2wType.build(faulty8)
      )
      exc.getMessage shouldBe s"There is no neutral tap position defined for transformer-type: $id"
    }

    "throw an exception when trying to build a TransformerType without parameter ntpmn" in {
      val faulty9 = input.copy(ntpmn = None)
      val exc = intercept[MissingParameterException](
        Transformer2wType.build(faulty9)
      )
      exc.getMessage shouldBe s"There is no minmum tap position defined for transformer-type: $id"
    }

    "throw an exception when trying to build a TransformerType without parameter ntpmx" in {
      val faulty10 = input.copy(ntpmx = None)
      val exc = intercept[MissingParameterException](
        Transformer2wType.build(faulty10)
      )
      exc.getMessage shouldBe s"There is no maximum tap position defined for transformer-type: $id"
    }

    "throw an exception when trying to build a TransformerType without parameter uktr" in {
      val faulty11 = input.copy(uktr = None)
      val exc = intercept[MissingParameterException](
        Transformer2wType.build(faulty11)
      )
      exc.getMessage shouldBe s"There is no short circuit voltage defined for transformer-type: $id"
    }

    "throw an exception when trying to build a TransformerType without parameter curmg" in {
      val faulty12 = input.copy(curmg = None)
      val exc = intercept[MissingParameterException](
        Transformer2wType.build(faulty12)
      )
      exc.getMessage shouldBe s"There is no no load current defined for transformer-type: $id"
    }

    "throw an exception when trying to build a TransformerType without parameter pfe" in {
      val faulty13 = input.copy(pfe = None)
      val exc = intercept[MissingParameterException](
        Transformer2wType.build(faulty13)
      )
      exc.getMessage shouldBe s"There is no iron loss defined for transformer-type: $id"
    }

    "throw an exception when trying to build a TransformerType without parameter pcutr" in {
      val faulty13 = input.copy(pcutr = None)
      val exc = intercept[MissingParameterException](
        Transformer2wType.build(faulty13)
      )
      exc.getMessage shouldBe s"There is no iron loss defined for transformer-type: $id"
    }

    "build a correctly configured TransformerType2w" in {
      val actual = Transformer2wType.build(input)
      actual.id shouldBe "Some2wTransformerType"
      actual.sRated shouldBe 40
      actual.vRatedA shouldBe 10
      actual.vRatedB shouldBe 0.4
      actual.dV shouldBe 1.2
      actual.dPhi shouldBe 12
      actual.tapSide shouldBe 0
      actual.tapNeutr shouldBe -1
      actual.tapMin shouldBe -3
      actual.tapMax shouldBe 5
      actual.uk shouldBe 5.1345
      actual.iNoLoad shouldBe 1
      actual.pFe shouldBe 10
      actual.pCu shouldBe 13
    }

  }

}
