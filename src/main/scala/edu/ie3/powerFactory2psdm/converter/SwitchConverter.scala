/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter

import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.connector.SwitchInput
import edu.ie3.powerFactory2psdm.exception.pf.ConversionException
import edu.ie3.powerFactory2psdm.model.powerfactory.Switch

import java.util.UUID

object SwitchConverter {

  def convert(
      rawSwitch: Switch,
      nodeId2nodeInput: Map[String, NodeInput]
  ): SwitchInput = {
    val nodeA = nodeId2nodeInput.getOrElse(
      rawSwitch.nodeAId,
      throw ConversionException(
        s"Can't find ${rawSwitch.nodeAId} for switch ${rawSwitch.id}"
      )
    )
    val nodeB = nodeId2nodeInput.getOrElse(
      rawSwitch.nodeBId,
      throw ConversionException(
        s"Can't find ${rawSwitch.nodeBId} for switch ${rawSwitch.id}"
      )
    )
    val closed = rawSwitch.onOff match {
      case 1d => true
      case 0d => false
      case _ =>
        throw ConversionException(
          s"The on/off parameter of switch ${rawSwitch.id} should be 1 or 0"
        )
    }

    new SwitchInput(
      UUID.randomUUID(),
      rawSwitch.id,
      nodeA,
      nodeB,
      closed
    )
  }

}
