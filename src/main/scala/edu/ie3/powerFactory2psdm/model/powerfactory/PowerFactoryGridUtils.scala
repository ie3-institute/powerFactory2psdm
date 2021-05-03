/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.model.powerfactory

import edu.ie3.powerFactory2psdm.exception.pf.MissingGridElementException
import edu.ie3.powerFactory2psdm.model.powerfactory.PowerFactoryGrid.Nodes

object PowerFactoryGridUtils {

  def getNodesMap(pfGrid: PowerFactoryGrid): Map[String, Nodes] = {
    pfGrid.nodes match {
      case Some(pfNodes) =>
        pfNodes
          .map(
            node =>
              (
                node.loc_name
                  .getOrElse(
                    throw new RuntimeException(s"Node $node has no id")
                  ),
                node
              )
          )
          .toMap
      case None =>
        throw MissingGridElementException("There are no nodes in the Grid")
    }
  }

}
