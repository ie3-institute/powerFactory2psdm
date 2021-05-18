/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.util

import edu.ie3.powerFactory2psdm.model.powerfactory.PowerFactoryGrid.Switches
import org.scalatest.{Matchers, WordSpecLike}

class GridPreparatorSpec extends Matchers with WordSpecLike {

  "The grid preparator" should {

    "filter out a singly connected switch" in {
      val singlyConnectedSwitch = Switches(
        Some("singlyConnectedSwitch"),
        Some(".ElmCoup"),
        Some("someNodeId"),
        None
      )
      val fullyConnectedSwitch = Switches(
        Some("fullyConnectedSwitch"),
        Some(".ElmCoup"),
        Some("someNodeId"),
        Some("aDifferentNodeId")
      )
      val filteredSwitches = GridPreparator.removeSinglyConnectedSwitches(
        Some(
          List(singlyConnectedSwitch, fullyConnectedSwitch)
        )
      )
      filteredSwitches shouldEqual Some(List(fullyConnectedSwitch))
    }
  }
}
