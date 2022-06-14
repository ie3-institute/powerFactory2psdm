/**
 * © 2021. Johannes Hiry,
 **/

package edu.ie3.powerFactory2psdm.model.entity

import edu.ie3.powerFactory2psdm.exception.pf.MissingParameterException
import edu.ie3.powerFactory2psdm.model.RawPfGridModel.{ConElms, Nodes}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class NodeSpec extends Matchers with AnyWordSpecLike {
  "A Node " should {

    val conElm = ConElms(
      Some(
        "SomeLine.ElmLne"
      ),
      Some(
        "ElmLne"
      )
    )

    val id = "SomeBus.ElmTerm"

    val input = Nodes(
      id = Some(id),
      locName = Some(id),
      vtarget = Some(1.0),
      uknom = Some(132.0),
      conElms = Some(List(Some(conElm))),
      GPSlat = Some(1.0),
      GPSlon = Some(1.0)
    )

    "throw an exception if the id is missing" in {
      val node = input.copy(id = None)
      val exc = intercept[MissingParameterException](Node.build(node))
      exc.getMessage shouldBe s"There is no id for node $node"
    }

    "throw an exception if the unsafe id is missing" in {
      val node = input.copy(locName = None)
      val exc = intercept[MissingParameterException](Node.build(node))
      exc.getMessage shouldBe s"There is no unsafe id for node $node"
    }

    "throw an exception if the nominal voltage is missing" in {
      val id = "BrokenBus1.ElmTerm"
      val node = input.copy(id = Some(id), uknom = None)
      val exc = intercept[MissingParameterException](Node.build(node))
      exc.getMessage shouldBe s"Node: $id has no defined nominal voltage"
    }

    "throw an exception if the target voltage is missing" in {
      val id = "BrokenBus2.ElmTerm"
      val node = input.copy(id = Some(id), vtarget = None)
      val exc = intercept[MissingParameterException](Node.build(node))
      exc.getMessage shouldBe s"Node: $id has no defined target voltage"
    }

    "throw an exception if there is no connected element" in {
      val id = "BrokenBus3.ElmTerm"
      val node = input.copy(id = Some(id), conElms = None)
      val exc = intercept[MissingParameterException](Node.build(node))
      exc.getMessage shouldBe s"Node: $id has no connected elements"
    }

    "build a fully configured node correctly" in {
      val node = Node.build(input)
      node.id shouldBe "SomeBus.ElmTerm"
      node.vTarget shouldBe 1.0
      node.nominalVoltage shouldBe 132.0
      node.lat shouldBe Some(1.0)
      node.lon shouldBe Some(1.0)
    }

  }

}
