/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.util

import com.typesafe.scalalogging.LazyLogging
import edu.ie3.powerFactory2psdm.model.RawPfGridModel
import edu.ie3.powerFactory2psdm.model.RawPfGridModel.Switches

object GridPreparator extends LazyLogging {

  /** Perform preparation of the [[RawPfGridModel]] before the actual conversion
    * can happen.
    *
    * @param pfGrid
    *   the [[RawPfGridModel]] to prepare
    * @return
    *   the prepared [[RawPfGridModel]]
    */
  def prepare(pfGrid: RawPfGridModel): RawPfGridModel = {
    val filteredSwitches = removeSinglyConnectedSwitches(pfGrid.switches)
    pfGrid.copy(switches = filteredSwitches)
  }

  /** Removes [[Switches]] from a [[RawPfGridModel]] that are only connected to
    * a single node.
    *
    * @param maybeSwitches
    * @return
    */
  def removeSinglyConnectedSwitches(
      maybeSwitches: Option[List[RawPfGridModel.Switches]]
  ): Option[List[RawPfGridModel.Switches]] =
    maybeSwitches.map(switches => {
      val (fullyConnected, singlyConnected) =
        switches.partition(isFullyConnectedSwitch)
      singlyConnected.foreach(switch =>
        logger.debug(
          s"Removed switch with id: ${switch.id.getOrElse("NO_ID")}, since it only has a single connection."
        )
      )
      fullyConnected
    })

  def isFullyConnectedSwitch(switch: Switches): Boolean =
    switch.bus1Id.isDefined & switch.bus2Id.isDefined
}
