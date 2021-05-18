/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.util

import com.typesafe.scalalogging.LazyLogging
import edu.ie3.powerFactory2psdm.model.powerfactory.PowerFactoryGrid
import edu.ie3.powerFactory2psdm.model.powerfactory.PowerFactoryGrid.Switches

object GridPreparator extends LazyLogging {

  def isFullyConnectedSwitch(switch: Switches): Boolean =
    switch.bus1Id.isDefined & switch.bus2Id.isDefined

  def removeSinglyConnectedSwitches(
      maybeSwitches: Option[List[PowerFactoryGrid.Switches]]
  ): Option[List[PowerFactoryGrid.Switches]] = maybeSwitches match {
    case Some(switches) =>
      val (fullyConnected, singlyConnected) =
        switches.partition(isFullyConnectedSwitch)
      singlyConnected.foreach(
        switch =>
          logger.debug(
            s"Removed switch with id: ${switch.id.getOrElse("NO_ID")}, since it only has a single connection"
          )
      )
      Some(fullyConnected)
  }

  /**
    * Performs various preparations to the power factory grid before it can be transformed.
    */
  def prepare(pfGrid: PowerFactoryGrid): PowerFactoryGrid = {
    val filteredSwitches = removeSinglyConnectedSwitches(pfGrid.switches)
    pfGrid.copy(switches = filteredSwitches)
  }

}
