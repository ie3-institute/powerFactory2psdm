package edu.ie3.powerFactory2psdm.converter.types

import edu.ie3.powerFactory2psdm.common.ConverterTestData.getTransformer2wType
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class Transformer2wTypeConverterSpec extends Matchers with AnyWordSpecLike{

  "A Transformer2wTypeConverter" should {
    val conversionPair = getTransformer2wType("SomeTrafo2wType")
    val input = conversionPair.input
    val expected = conversionPair.result

    "convert a transformer type correctly" in {
      val actual = TransformerType2wConverter.convert(input)

      // actual.getId shouldBe expected.getId
      actual.getrSc.subtract(expected.getrSc)
      // actual.getxSc shouldBe expected.getxSc
      actual.getsRated shouldBe expected.getsRated
      actual.getvRatedA shouldBe expected.getvRatedA
      actual.getvRatedB shouldBe expected.getvRatedB
      actual.getgM shouldBe expected.getgM
      actual.getbM shouldBe expected.getbM
      actual.getdV shouldBe expected.getdV
      actual.getdPhi shouldBe expected.getdPhi
      actual.getTapNeutr shouldBe expected.getTapNeutr
      actual.getTapMin shouldBe expected.getTapMin
      actual.getTapMax shouldBe expected.getTapMax
    }

  }

}

//actual.getrSc.subtract(expected.getrSc).getValue.doubleValue() < testingTolerance shouldBe true
//actual.getxSc.subtract(expected.getxSc).getValue.doubleValue() < testingTolerance shouldBe true
//actual.getsRated.subtract(expected.getsRated).getValue.doubleValue() < testingTolerance shouldBe true
//actual.getvRatedA.subtract(expected.getvRatedA).getValue.doubleValue() < testingTolerance shouldBe true
//actual.getvRatedB.subtract(expected.getvRatedB).getValue.doubleValue() < testingTolerance shouldBe true
//actual.getgM.subtract(expected.getgM).getValue.doubleValue() < testingTolerance shouldBe true
//actual.getbM.subtract(expected.getbM).getValue.doubleValue() < testingTolerance shouldBe true
//actual.getdV.subtract(expected.getdV).getValue.doubleValue() < testingTolerance shouldBe true
//actual.getdPhi.subtract(expected.getdPhi).getValue.doubleValue() < testingTolerance shouldBe true
