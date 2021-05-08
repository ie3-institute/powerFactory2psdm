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
import org.jgrapht.alg.connectivity.BiconnectivityInspector

case object GridConverter {

  def convert(pfGrid: PowerFactoryGrid) = {
    val gridElements = convertGridElements(pfGrid)
  }

  def convertGridElements(pfGrid: PowerFactoryGrid): Unit = {
    val pfGridMaps = new PowerFactoryGridMaps(pfGrid)
    val graph = GridGraphBuilder.build(pfGridMaps)
    val inspect = new BiconnectivityInspector(graph)
    println("")

  }
}
