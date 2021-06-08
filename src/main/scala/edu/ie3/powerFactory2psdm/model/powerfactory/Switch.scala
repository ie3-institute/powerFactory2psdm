/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.model.powerfactory

import com.typesafe.scalalogging.LazyLogging
import edu.ie3.powerFactory2psdm.exception.pf.MissingParameterException
import edu.ie3.powerFactory2psdm.model.powerfactory.RawGridModel.Switches

/**
  * Electrical Switch
  *
  * @param id identifier
  * @param nodeAId id of one of the connected nodes
  * @param nodeBId id of one of the connected nodes
  */
final case class Switch(
    id: String,
    nodeAId: String,
    nodeBId: String
) extends EntityModel
    with Edge

object Switch extends LazyLogging {

  /**
    * Builds an Option that probably contains a [[Switch]]
    *
    * Note: We are not throwing an Exception if one connection of the switches is missing, since unused singly connected
    * switches in PowerFactoryGrid substations are a thing. In that case we don't build it and return None.
    *
    * @param rawSwitch the schema generated switch
    * @return a [[Switch]]
    */
  def maybeBuild(rawSwitch: Switches): Option[Switch] = {
    val id = rawSwitch.id.getOrElse(
      throw MissingParameterException(s"Switch: $rawSwitch is missng an id.")
    )
    val nodeAId = rawSwitch.bus1Id match {
      case Some(nodeAId) => nodeAId
      case None =>
        logger.warn(
          s"Switch: $id has not been built as it is missing its bus1Id"
        )
        return None
    }
    val nodeBId = rawSwitch.bus2Id match {
      case Some(nodeBId) => nodeBId
      case None =>
        logger.warn(
          s"Switch: $id has not been built as it is missing its bus2Id"
        )
        return None
    }
    Some(
      Switch(
        id,
        nodeAId,
        nodeBId
      )
    )

  }
}
