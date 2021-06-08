/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.model.powerfactory

import edu.ie3.powerFactory2psdm.exception.pf.{ElementConfigurationException, MissingParameterException}
import edu.ie3.powerFactory2psdm.model.powerfactory.RawGridModel.{ConElms, Nodes}

/**
  * Electrical node
  *
  * @param id identifier
  * @param nominalVoltage nominal voltage in kV
  * @param vTarget rated voltage in p.u.
  * @param lat latitude
  * @param lon longitude
  * @param conElms connected elements to the node
  */
final case class Node(
    id: String,
    nominalVoltage: Double,
    vTarget: Double,
    lat: Option[Double],
    lon: Option[Double],
    conElms: List[ConElms]
) extends EntityModel

object Node {

  /**
    * Build a node from a raw [[Nodes]]
    *
    * @param rawNode
    * @return
    */
  def build(rawNode: Nodes): Node = {
    val id = rawNode.id match {
      case Some(id) if EntityModel.isUniqueId(id) => id
      case Some(id) => throw ElementConfigurationException(s"ID: $id is not unique")
      case None => throw MissingParameterException(s"There is no id for node $rawNode")
    }
    val nominalVoltage = rawNode.uknom.getOrElse(
      throw MissingParameterException(
        s"Node: $id has no defined nominal voltage"
      )
    )
    val vTarget = rawNode.vtarget.getOrElse(
      throw MissingParameterException(s"Node: $id has no defined rated Voltage")
    )
    val conElms = rawNode.conElms
      .getOrElse(
        throw MissingParameterException(s"Node: $id has no connected elements")
      )
      .flatten

    Node(
      id,
      nominalVoltage,
      vTarget,
      rawNode.GPSlat,
      rawNode.GPSlon,
      conElms
    )
  }
}
