/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.model.entity

import edu.ie3.powerFactory2psdm.exception.pf.{
  MissingParameterException,
  TestException
}
import edu.ie3.powerFactory2psdm.model.RawPfGridModel.Switches
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class SwitchSpec extends Matchers with AnyWordSpecLike {
  "A switch " should {

    val id = "Some_Switch.ElmCoup"

    val input = Switches(
      id = Some(id),
      bus1Id = Some("SomeBusA"),
      bus2Id = Some("SomeBusB")
    )

    "throw an exception when building if the id is missing" in {
      val switch = input.copy(id = None)
      val exc = intercept[MissingParameterException](Switch.maybeBuild(switch))
      exc.getMessage shouldBe s"There is no id for switch $switch"
    }

    "return None when building if the bus1Id is missing" in {
      val id = "Broken_Switch1.ElmLne"
      val switch = Switch.maybeBuild(input.copy(id = Some(id), bus1Id = None))
      switch shouldBe None
    }

    "return None when building if the bus2Id is missing" in {
      val id = "Broken_Switch2.ElmLne"
      val switch = Switch.maybeBuild(input.copy(id = Some(id), bus2Id = None))
      switch shouldBe None
    }

    "build a fully configured switch correctly" in {
      val switch = Switch
        .maybeBuild(input)
        .getOrElse(throw TestException("We shouldn't get None here!"))
      switch.id shouldBe "Some_Switch.ElmCoup"
      switch.nodeAId shouldBe "SomeBusA"
      switch.nodeBId shouldBe "SomeBusB"
    }

  }

}
