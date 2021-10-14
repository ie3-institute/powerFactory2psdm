/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter

import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.connector.SwitchInput
import edu.ie3.powerFactory2psdm.converter.NodeConverter.getNode
import edu.ie3.powerFactory2psdm.exception.pf.ConversionException
import edu.ie3.powerFactory2psdm.model.entity.Switch

import java.util.UUID
import scala.util.{Failure, Success}

object SwitchConverter {

  def convert(
      rawSwitch: Switch,
      nodeId2nodeInput: Map[String, NodeInput]
  ): SwitchInput = {
    val closed = rawSwitch.onOff match {
      case 1 => true
      case 0 => false
      case _ =>
        throw ConversionException(
          s"The on/off parameter of switch ${rawSwitch.id} should be 1 or 0"
        )
    }
    (
      getNode(rawSwitch.nodeAId, nodeId2nodeInput),
      getNode(rawSwitch.nodeBId, nodeId2nodeInput)
    ) match {
      case (Success(nodeA), Success(nodeB)) =>
        new SwitchInput(
          UUID.randomUUID(),
          rawSwitch.id,
          nodeA,
          nodeB,
          closed
        )
      case (Failure(exc), _) =>
        throw ConversionException(
          s"Can't retrieve ${rawSwitch.nodeAId} for switch ${rawSwitch.id}",
          exc
        )
      case (_, Failure(exc)) =>
        throw ConversionException(
          s"Can't retrieve ${rawSwitch.nodeBId} for switch ${rawSwitch.id}",
          exc
        )
    }
  }
}
