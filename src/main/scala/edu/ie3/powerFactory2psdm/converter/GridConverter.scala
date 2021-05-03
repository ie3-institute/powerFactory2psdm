/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter

import edu.ie3.powerFactory2psdm.exception.pf.MissingGridElementException
import edu.ie3.powerFactory2psdm.model.powerfactory.PowerFactoryGrid
import edu.ie3.powerFactory2psdm.model.powerfactory.PowerFactoryGrid.Nodes
import org.jgrapht.alg.connectivity.BiconnectivityInspector
import edu.ie3.powerFactory2psdm.model.powerfactory.PowerFactoryGridUtils

case object GridConverter {

  def convert(pfGrid: PowerFactoryGrid) = {
    val gridElements = convertGridElements(pfGrid)
  }

  def convertGridElements(pfGrid: PowerFactoryGrid): Unit = {
    val pfNodesMap: Map[String, Nodes] =
      PowerFactoryGridUtils.getNodesMap(pfGrid)
    val graph = GridGraphBuilder.build(pfGrid.nodes, pfGrid.lines, pfNodesMap)

  }

}
