package edu.ie3.powerFactory2psdm.util

import com.typesafe.scalalogging.LazyLogging
import edu.ie3.powerFactory2psdm.exception.pf.ElementConfigurationException
import edu.ie3.powerFactory2psdm.model.powerfactory.PowerFactoryGrid
import edu.ie3.powerFactory2psdm.model.powerfactory.PowerFactoryGrid.{ConElms, Nodes, Switches}

object GridPreparator extends LazyLogging {

  /**
   * Checks if a switch is only connected to a single element.
   */
  def isSinglyConnectedSwitch(
     switch: Switches
   ): Boolean = {
    switch.conElms
      .getOrElse(
        throw ElementConfigurationException(
          s"Switch ${switch.id.getOrElse("NO_ID")} isn't connected to anything"
        )
      )
      .flatten
      .size == 1
  }

  /**
   * Filters out switches that are only connected to a single Element. These switches commonly occur in non-used
   * connections of substations, so they are no reason to throw an exception, but should be filtered out
   */
  def removeSinglyConnetedSwitches(maybeSwitchs: Option[List[Switches]]): Option[List[Switches]] = maybeSwitchs match {
    case Some(switches) =>
      val(singlyConnectedSwitch, remainder) = switches.partition(isSinglyConnectedSwitch)
      val singlyConnectedSwitchIds = singlyConnectedSwitch.map(node => node.id.getOrElse("NO_ID"))
      logger.debug(s"Nodes with the ids $singlyConnectedSwitchIds are filtered out since they are substation helper nodes")
      Some(remainder)

    case None => None
  }

  def getConElmIds(conElms: List[List[Option[ConElms]]]): Set[String] = {
    conElms.flatten.flatten.map(conElm => conElm.id.getOrElse("NO_ID")).toSet
  }

  /**
   * Checks if a node is connected by an edge to other nodes in the grid.
   * TODO: Is computationally expensive, if performed often in bigger consider more performant solution
   */
  def isConnectedByAnEdge(node: Nodes, pfGrid: PowerFactoryGrid): Boolean = {
    val nodeId = node.id.getOrElse("NO_ID")
    val connectedBySwitches = pfGrid.switches match {
      case None => false
      case Some(switches) =>
        val conElms =  getConElmIds(switches.flatMap(switch => switch.conElms))
        conElms.contains(nodeId)
    }
    val connectedByLines = pfGrid.lines match {
      case None => false
      case Some(lines) =>
        val conElms =  getConElmIds(lines.flatMap(line => line.conElms))
        conElms.contains(nodeId)
    }
    connectedBySwitches | connectedByLines
  }

  /**
   * Checks if a node is an internal helper node in a substation.
   */
  def isSubstationHelperNode(
    node: Nodes,
    pfGrid: PowerFactoryGrid
  ): Boolean = {
    val id = node.id.getOrElse(throw ElementConfigurationException(s"$node does not contain an ID"))
    val isInSubstation = id.split("""\\""").init.last.endsWith(".ElmTrfstat")
    val isHelperNode = node.iUsage.getOrElse(throw ElementConfigurationException("")) == 2
    isInSubstation && isHelperNode && !isConnectedByAnEdge(node, pfGrid)
  }

  /**
   * Filters out internal helper node in PowerFactory substations.
   */
  def removeSubstationHelperNodes(pfGrid:PowerFactoryGrid): Option[List[Nodes]] = pfGrid.nodes match{
    case Some(nodes) =>
      val (helperNodes, remainingNodes) = nodes.partition(isSubstationHelperNode(_, pfGrid))
      val helperNodeIds = helperNodes.map(node => node.id.getOrElse("NO_ID"))
      logger.debug(s"Nodes with the ids $helperNodeIds are filtered out since they are substation helper nodes")
      Some(remainingNodes)
    case None => None
  }

  /**
   * Performs various preparations to the power factory grid before it can be transformed.
   */
  def prepare(pfGrid: PowerFactoryGrid): PowerFactoryGrid = {
    val filteredSwitches = removeSinglyConnetedSwitches(pfGrid.switches)
    val filteredNodes = removeSubstationHelperNodes(pfGrid)
    pfGrid.copy(switches=filteredSwitches, nodes=filteredNodes)
  }

}
