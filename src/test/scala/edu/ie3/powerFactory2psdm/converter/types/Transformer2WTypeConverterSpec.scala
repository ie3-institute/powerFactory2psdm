/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter.types

import edu.ie3.powerFactory2psdm.common.ConverterTestData.getTransformer2WTypePair
import edu.ie3.powerFactory2psdm.exception.pf.ConversionException
import edu.ie3.scalatest.QuantityMatchers
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

import math.abs

class Transformer2WTypeConverterSpec
    extends Matchers
    with AnyWordSpecLike
    with QuantityMatchers {
  class Transformer2WTypeConverterSpec extends Matchers with AnyWordSpecLike {

    "A Transformer2wTypeConverter" should {
      val conversionPair = getTransformer2WTypePair("SomeTrafo2WType")
      val input = conversionPair.input
      val expected = conversionPair.result
      implicit val quantityTolerance: Double = 1e-6

      "convert a transformer type correctly" in {
        val actual = Transformer2WTypeConverter.convert(input)

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
          Transformer2WTypeConverter.convert(invalidInput)
        )
        thrown.getMessage shouldBe s"Short circuit experiment calculations of 2w transformer type: ${input.id} is not possible due to faulty " +
          s"parameters. The short circuit resistance can't exceed the short circuit impedance."
      }

      "throw an exception, when the input model does not allow to calculate no load circuit parameters correctly" in {
        val invalidInput = input.copy(pFe = 1000)
        val thrown = intercept[ConversionException](
          Transformer2WTypeConverter.convert(invalidInput)
        )
        thrown.getMessage shouldBe s"No load experiment calculations of 2w transformer type: ${input.id} is not possible due to faulty parameters." +
          s"The no load conductance can't exceed the no load admittance."
      }

    }
  }
}
