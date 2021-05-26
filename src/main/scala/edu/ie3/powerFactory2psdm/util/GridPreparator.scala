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

  /**
    * Removes [[Switches]] from a [[PowerFactoryGrid]] that are only connected to a single node.
    * @param maybeSwitches
    * @return
    */
  def removeSinglyConnectedSwitches(
      maybeSwitches: Option[List[PowerFactoryGrid.Switches]]
  ): Option[List[PowerFactoryGrid.Switches]] =
    maybeSwitches.map(switches => {
      val (fullyConnected, singlyConnected) =
        switches.partition(isFullyConnectedSwitch)
      singlyConnected.foreach(
        switch =>
          logger.debug(
            s"Removed switch with id: ${switch.id.getOrElse("NO_ID")}, since it only has a single connection."
          )
      )
      fullyConnected
    })

  /**
    * Perform preparation of the [[PowerFactoryGrid]] before the actual conversion can happen.
    *
    * @param pfGrid the [[PowerFactoryGrid]] to prepare
    * @return the prepared [[PowerFactoryGrid]]
    */
  def prepare(pfGrid: PowerFactoryGrid): PowerFactoryGrid = {
    val filteredSwitches = removeSinglyConnectedSwitches(pfGrid.switches)
    pfGrid.copy(switches = filteredSwitches)
  }

}
