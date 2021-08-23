/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.model.powerfactory

import edu.ie3.powerFactory2psdm.exception.pf.MissingParameterException
import edu.ie3.powerFactory2psdm.model.powerfactory.RawPfGridModel.Lines
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class LineSpec extends Matchers with AnyWordSpecLike {
  "A Line " should {

    val id = "SomeLine.ElmLne"

    val input = Lines(
      id = Some(id),
      bus1Id = Some("SomeBusA"),
      bus2Id = Some("SomeBusB")
    )

    "throw an exception if the id is missing" in {
      val line = input.copy(id = None)
      val exc = intercept[MissingParameterException](Line.build(line))
      exc.getMessage shouldBe s"There is no id for line $line"
    }

    "throw an exception if the bus1Id is missing" in {
      val id = "BrokenLine1.ElmLne"
      val line = input.copy(id = Some(id), bus1Id = None)
      val exc = intercept[MissingParameterException](Line.build(line))
      exc.getMessage shouldBe s"Line: $id has no defined node a"
    }

    "throw an exception if the bus2Id is missing" in {
      val id = "BrokenLine2.ElmLne"
      val line = input.copy(id = Some(id), bus2Id = None)
      val exc = intercept[MissingParameterException](Line.build(line))
      exc.getMessage shouldBe s"Line: $id has no defined node b"
    }

    "build a fully configured line correctly" in {
      val line = Line.build(input)
      line.id shouldBe "SomeLine.ElmLne"
      line.nodeAId shouldBe "SomeBusA"
      line.nodeBId shouldBe "SomeBusB"
    }

  }

}
