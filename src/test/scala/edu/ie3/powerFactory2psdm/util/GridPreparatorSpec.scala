/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.util

import edu.ie3.powerFactory2psdm.model.RawPfGridModel.Switches
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class GridPreparatorSpec extends Matchers with AnyWordSpecLike {

  "The grid preparator" should {

    "filter out a singly connected switch" in {
      val singlyConnectedSwitch = Switches(
        Some("singlyConnectedSwitch"),
        Some(1.0),
        Some("someNodeId"),
        None
      )
      val anotherSinglyConnectedSwitch = Switches(
        Some("anotherSinglyConnectedSwitch"),
        Some(1.0),
        None,
        Some("someNodeId")
      )
      val fullyConnectedSwitch = Switches(
        Some("fullyConnectedSwitch"),
        Some(1.0),
        Some("someNodeId"),
        Some("aDifferentNodeId")
      )
      val filteredSwitches = GridPreparator.removeSinglyConnectedSwitches(
        Some(
          List(
            singlyConnectedSwitch,
            anotherSinglyConnectedSwitch,
            fullyConnectedSwitch
          )
        )
      )
      filteredSwitches shouldEqual Some(List(fullyConnectedSwitch))
    }
  }
}
