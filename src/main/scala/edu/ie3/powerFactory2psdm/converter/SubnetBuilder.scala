/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter

import com.typesafe.scalalogging.LazyLogging
import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.voltagelevels.{
  GermanVoltageLevelUtils,
  VoltageLevel
}
import edu.ie3.powerFactory2psdm.exception.pf.{
  ElementConfigurationException,
  GridConfigurationException
}
import edu.ie3.powerFactory2psdm.model.Subnet
import edu.ie3.powerFactory2psdm.model.powerfactory.PowerFactoryGrid.Nodes
import org.jgrapht.alg.connectivity.BiconnectivityInspector
import org.jgrapht.graph.{DefaultEdge, Multigraph}
import tech.units.indriya.quantity.Quantities.getQuantity

import java.util.UUID
import scala.jdk.CollectionConverters.CollectionHasAsScala

object SubnetBuilder extends LazyLogging {

  /**
    * Takes the grid graph and builds up the different [[Subnet]]s from it.
    *
    * @param gridGraph the built grid graph represented by the UUIDS of the nodes and the lines connecting them
    * @param uuid2Node the mapping between UUID and the corresponding node
    * @return the list of all subnets of the grid
    */
  def buildSubnets(
      gridGraph: Multigraph[UUID, DefaultEdge],
      uuid2Node: Map[UUID, Nodes]
  ): List[Subnet] = {
    new BiconnectivityInspector(gridGraph).getConnectedComponents.asScala.toList.zipWithIndex map {
      case (subgraph, index) =>
        buildSubnet(
          index,
          subgraph.vertexSet().asScala.toSet,
          uuid2Node
        )
    }
  }

  /**
    * Builds a [[Subnet]] after checking if all nodes have the same nominal voltage
    *
    * @param subnetId  id of the subnet
    * @param nodeIds   UUIDS of all nodes that live in the subnet
    * @param uuid2node mapping between UUID and node
    * @return the built [[Subnet]]
    */
  def buildSubnet(
      subnetId: Int,
      nodeIds: Set[UUID],
      uuid2node: Map[UUID, Nodes]
  ): Subnet = {
    val nodes = nodeIds.map(uuid => uuid2node(uuid)).toList
    val nomVoltage = getNomVoltage(
      nodes.headOption.getOrElse(
        throw GridConfigurationException("There are no nodes in the subnet!")
      )
    )

    val divergences = nodes
      .filter(node => Math.abs(nomVoltage - getNomVoltage(node)) > 0.001)
      .map { node =>
        node.id.getOrElse("NO_ID") + " -> " + node.uknom.getOrElse(
          "NO_NOMVOLT"
        )
      }
    if (divergences.nonEmpty)
      throw ElementConfigurationException(
        s"There are the following divergences from the nominal voltage $nomVoltage : $divergences"
      )

    val voltLvl = GermanVoltageLevelUtils.parse(
      getQuantity(nomVoltage, StandardUnits.RATED_VOLTAGE_MAGNITUDE)
    )
    Subnet(subnetId, nodeIds, voltLvl)
  }

  def getNomVoltage(node: Nodes): Double =
    node.uknom.getOrElse(
      throw ElementConfigurationException(
        s"Node: ${node.id.getOrElse("NO_ID")} has no defined nominal voltage."
      )
    )

}
