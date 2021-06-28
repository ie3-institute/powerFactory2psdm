/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter.types

import edu.ie3.powerFactory2psdm.common.ConverterTestData.getTransformer2WTypePair
import edu.ie3.powerFactory2psdm.exception.pf.ConversionException
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

import math.abs

class TransformerType2WConverterSpec extends Matchers with AnyWordSpecLike {

  "A Transformer2wTypeConverter" should {
    val conversionPair = getTransformer2WTypePair("SomeTrafo2WType")
    val input = conversionPair.input
    val expected = conversionPair.result
    val testingTolerance = 1e-3

    "convert a transformer type correctly" in {
      val actual = TransformerType2WConverter.convert(input)

      actual.getId shouldBe expected.getId
      abs(
        actual.getrSc
          .subtract(expected.getrSc)
          .getValue
          .doubleValue()
      ) < testingTolerance shouldBe true
      abs(
        actual.getxSc
          .subtract(expected.getxSc)
          .getValue
          .doubleValue()
      ) < testingTolerance shouldBe true
      abs(
        actual.getsRated
          .subtract(expected.getsRated)
          .getValue
          .doubleValue()
      ) < testingTolerance shouldBe true
      abs(
        actual.getvRatedA
          .subtract(expected.getvRatedA)
          .getValue
          .doubleValue()
      ) < testingTolerance shouldBe true
      abs(
        actual.getvRatedB
          .subtract(expected.getvRatedB)
          .getValue
          .doubleValue()
      ) < testingTolerance shouldBe true
      abs(
        actual
          .getgM()
          .subtract(expected.getgM())
          .getValue
          .doubleValue()
      ) < testingTolerance shouldBe true
      abs(
        actual
          .getbM()
          .subtract(expected.getbM())
          .getValue
          .doubleValue()
      ) < testingTolerance shouldBe true
      abs(
        actual
          .getdV()
          .subtract(expected.getdV)
          .getValue
          .doubleValue()
      ) < testingTolerance shouldBe true
      abs(
        actual.getdPhi
          .subtract(expected.getdPhi)
          .getValue
          .doubleValue()
      ) < testingTolerance shouldBe true
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
