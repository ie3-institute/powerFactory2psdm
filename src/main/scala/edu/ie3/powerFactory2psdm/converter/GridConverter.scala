/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter

import edu.ie3.powerFactory2psdm.model.powerfactory.{PowerFactoryGrid, PowerFactoryGridMaps}
import edu.ie3.powerFactory2psdm.util.GridPreparator
import org.jgrapht.alg.connectivity.BiconnectivityInspector

case object GridConverter {

  def convert(pfGrid: PowerFactoryGrid) = {
    val gridElements = convertGridElements(pfGrid)
  }

  def convertGridElements(rawPfGrid: PowerFactoryGrid): Unit = {
    val pfGrid = GridPreparator.prepare(rawPfGrid)
    val pfGridMaps = new PowerFactoryGridMaps(pfGrid)
    val graph = GridGraphBuilder.build(pfGridMaps)
    val subgrids = new BiconnectivityInspector(graph).getConnectedComponents

    subgrids.forEach(subgrid => {
      println("---------Subgrid-----------")
      subgrid.vertexSet().forEach(nodeUUID =>
        println(pfGridMaps.UUID2node(nodeUUID).id.getOrElse("NO_ID")))
      println("")
    })
    println("")
  }
}
