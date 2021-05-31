/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter

import edu.ie3.powerFactory2psdm.model.powerfactory.{
  PowerFactoryGrid,
  PowerFactoryGridMaps
}
import edu.ie3.powerFactory2psdm.util.GridPreparator

/**
  * Functionalities to transform an exported and then parsed PowerFactory grid to the PSDM.
  */
case object GridConverter {

  def convert(pfGrid: PowerFactoryGrid) = {
    val gridElements = convertGridElements(pfGrid)
  }

  /**
    * Converts the grid elements of the PowerFactory grid
    * @param rawPfGrid the raw parsed PowerFactoryGrid
    */
  def convertGridElements(rawPfGrid: PowerFactoryGrid): Unit = {
    val pfGrid = GridPreparator.prepare(rawPfGrid)
    val pfGridMaps = PowerFactoryGridMaps(pfGrid)
    val graph = GridGraphBuilder.build(pfGridMaps)
    val subnets = SubnetBuilder.buildSubnets(graph, pfGridMaps.uuid2Node)
  }
}
