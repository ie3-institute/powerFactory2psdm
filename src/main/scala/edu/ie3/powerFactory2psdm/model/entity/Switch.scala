/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.model.entity

import com.typesafe.scalalogging.LazyLogging
import edu.ie3.powerFactory2psdm.exception.pf.MissingParameterException
import edu.ie3.powerFactory2psdm.model
import edu.ie3.powerFactory2psdm.model.RawPfGridModel.Switches

/** Electrical Switch
  *
  * @param id
  *   identifier
  * @param nodeAId
  *   id of one of the connected nodes
  * @param nodeBId
  *   id of one of the connected nodes
  */
final case class Switch(
    id: String,
    nodeAId: String,
    nodeBId: String,
    onOff: Int
) extends EntityModel
    with Edge

object Switch extends LazyLogging {

  /** Builds an Option that probably contains a [[Switch]]
    *
    * Note: We are not throwing an Exception if one connection of the switches
    * is missing, since unused singly connected switches in PowerFactoryGrid
    * substations are a thing. In that case we don't build it and return None.
    *
    * @param rawSwitch
    *   the schema generated switch
    * @return
    *   a [[Switch]]
    */
  def maybeBuild(rawSwitch: Switches): Option[Switch] = {
    val id = rawSwitch.id.getOrElse(
      throw MissingParameterException(s"There is no id for switch $rawSwitch")
    )
    val onOff = rawSwitch.onOff
      .getOrElse(
        throw MissingParameterException(
          s"Switch: $id has no defined open/closed state"
        )
      )
      .toInt
    (rawSwitch.bus1Id, rawSwitch.bus2Id) match {
      case (Some(bus1Id), Some(bus2Id)) =>
        Some(
          Switch(
            id,
            bus1Id,
            bus2Id,
            onOff
          )
        )
      case (None, Some(_)) | (Some(_), None) =>
        logger.warn(
          s"Switch: $id is not being built, as it's only connected to a single node"
        )
        None
      case _ =>
        throw MissingParameterException(
          s"Switch: $id is not connected to any node"
        )
    }
  }
}
