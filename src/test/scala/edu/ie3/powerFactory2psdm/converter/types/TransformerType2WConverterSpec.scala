/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter.types

import edu.ie3.powerFactory2psdm.common.ConverterTestData.getTransformer2wType
import edu.ie3.powerFactory2psdm.exception.pf.ConversionException
import edu.ie3.scalatest.QuantityMatchers
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

import math.abs

class TransformerType2WConverterSpec
    extends Matchers
    with AnyWordSpecLike
    with QuantityMatchers {

  "A Transformer2wTypeConverter" should {
    val conversionPair = getTransformer2wType("SomeTrafo2wType")
    val input = conversionPair.input
    val expected = conversionPair.result
    implicit val quantityTolerance: Double = 1e-3

    "convert a transformer type correctly" in {
      val actual = TransformerType2WConverter.convert(input)

      actual.getId shouldBe expected.getId

      actual.getvRatedA should equalWithTolerance(expected.getvRatedA)
      actual.getvRatedB should equalWithTolerance(expected.getvRatedB)
      actual.getdV should equalWithTolerance(expected.getdV)
      actual.getdPhi should equalWithTolerance(expected.getdPhi)

      actual.getrSc should equalWithTolerance(expected.getrSc)
      actual.getxSc should equalWithTolerance(expected.getxSc)
      actual.getgM should equalWithTolerance(expected.getgM)

      actual.isTapSide shouldBe expected.isTapSide
      actual.getTapNeutr shouldBe expected.getTapNeutr
      actual.getTapMin shouldBe expected.getTapMin
      actual.getTapMax shouldBe expected.getTapMax
    }

    "throw an exception, when the input model does not allow to calculate short circuit parameters correctly" in {
      val invalidInput = input.copy(pCu = 3000)
      val thrown = intercept[ConversionException](
        TransformerType2WConverter.convert(invalidInput)
      )
      thrown.getMessage shouldBe s"Short circuit experiment calculations of 2w transformer type: ${input.id} not possible."
    }

    "throw an exception, when the input model does not allow to calculate no load circuit parameters correctly" in {
      val invalidInput = input.copy(pFe = 1000)
      val thrown = intercept[ConversionException](
        TransformerType2WConverter.convert(invalidInput)
      )
      thrown.getMessage shouldBe s"No load experiment calculations of 2w transformer type: ${input.id} not possible."
    }

  }
}
