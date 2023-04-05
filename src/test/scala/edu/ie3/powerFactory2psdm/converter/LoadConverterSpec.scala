/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter

import edu.ie3.powerFactory2psdm.common.ConverterTestData
import edu.ie3.powerFactory2psdm.exception.pf.{
  ElementConfigurationException,
  MissingParameterException
}
import edu.ie3.powerFactory2psdm.model.entity.Load.getIsScaled
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import edu.ie3.scalatest.QuantityMatchers

import scala.util.{Failure, Success}

private class LoadConverterSpec
    extends Matchers
    with AnyWordSpecLike
    with QuantityMatchers {

  "A load converter" should {
    val loadPair = ConverterTestData.getLoadPair("someLoad")
    val node = ConverterTestData.getNodePair("someNode").result
    implicit val quantityTolerance: Double = 1e-6

    "convert a load correctly" in {
      val actual = LoadConverter.convert(loadPair.input, node)
      val expected = loadPair.result
      actual.getId shouldBe expected.getId
      actual.getOperator shouldBe expected.getOperator
      actual.getOperationTime shouldBe expected.getOperationTime
      actual.getNode shouldBe expected.getNode
      actual.getqCharacteristics shouldBe expected.getqCharacteristics
      actual.getLoadProfile shouldBe expected.getLoadProfile
      actual.isDsm shouldBe expected.isDsm
      actual.geteConsAnnual shouldBe expected.geteConsAnnual
      actual.getsRated shouldBe expected.getsRated
      actual.getCosPhiRated shouldBe expected.getCosPhiRated
    }

    "convert a scaled load correctly" in {
      val scalingFactor = 0.5
      val scaledLoad = loadPair.input.copy(
        isScaled = true,
        scalingFactor = Some(scalingFactor)
      )
      val actual = LoadConverter.convert(scaledLoad, node)
      val expected = loadPair.result
      actual.getId shouldBe expected.getId
      actual.getOperator shouldBe expected.getOperator
      actual.getOperationTime shouldBe expected.getOperationTime
      actual.getNode shouldBe expected.getNode
      actual.getqCharacteristics shouldBe expected.getqCharacteristics
      actual.getLoadProfile shouldBe expected.getLoadProfile
      actual.isDsm shouldBe expected.isDsm
      actual.geteConsAnnual shouldBe expected.geteConsAnnual
      actual.getsRated should equalWithTolerance(
        expected.getsRated.multiply(0.5)
      )
      actual.getCosPhiRated shouldBe expected.getCosPhiRated
    }

    "determine whether scaling should be applied" in {
      getIsScaled(Some(1.0)) shouldBe Success(true)
      getIsScaled(Some(0.0)) shouldBe Success(false)
      getIsScaled(Some(2.0)) shouldBe Failure(
        ElementConfigurationException(
          s"The isScaled specifier: 2 should be either 0 or 1."
        )
      )
      getIsScaled(None) shouldBe Failure(
        MissingParameterException("The isScaled specifier is missing.")
      )
    }
  }
}
