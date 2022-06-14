/**
 * © 2021. Johannes Hiry,
 **/

package edu.ie3.powerFactory2psdm.model.entity

import edu.ie3.powerFactory2psdm.exception.pf.MissingParameterException
import edu.ie3.powerFactory2psdm.model.RawPfGridModel.Lines
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class LineSpec extends Matchers with AnyWordSpecLike {
  "A Line " should {

    val id = "SomeLine.ElmLne"

    val input = Lines(
      id = Some(id),
      bus1Id = Some("SomeBusA"),
      bus2Id = Some("SomeBusB"),
      GPScoords = Some(
        List(
          Some(List(Some(11.1123), Some(52.1425))),
          Some(List(Some(11.1153), Some(52.1445)))
        )
      ),
      dline = Some(1.5),
      typeId = Some("SomeLineType")
    )

    "throw an exception if the id is missing" in {
      val line = input.copy(id = None)
      val exc = intercept[MissingParameterException](Line.build(line, Map()))
      exc.getMessage shouldBe s"There is no id for line $line"
    }

    "throw an exception if the bus1Id is missing" in {
      val id = "BrokenLine1.ElmLne"
      val line = input.copy(id = Some(id), bus1Id = None)
      val exc = intercept[MissingParameterException](Line.build(line, Map()))
      exc.getMessage shouldBe s"Line: $id has no defined node a"
    }

    "throw an exception if the bus2Id is missing" in {
      val id = "BrokenLine2.ElmLne"
      val line = input.copy(id = Some(id), bus2Id = None)
      val exc = intercept[MissingParameterException](Line.build(line, Map()))
      exc.getMessage shouldBe s"Line: $id has no defined node b"
    }

    "build a fully configured line correctly" in {
      val line = Line.build(input, Map())
      line.id shouldBe "SomeLine.ElmLne"
      line.nodeAId shouldBe "SomeBusA"
      line.nodeBId shouldBe "SomeBusB"
    }

  }

}
