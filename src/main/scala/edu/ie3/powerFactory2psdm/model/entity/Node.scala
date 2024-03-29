/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.model.entity

import edu.ie3.powerFactory2psdm.exception.pf.MissingParameterException
import edu.ie3.powerFactory2psdm.model.RawPfGridModel.Nodes

/** Electrical node
  *
  * @param id
  *   unique identifier that is based on the unsafe id but unique
  * @param unsafeId
  *   name of a node that is set within the "Name" section of the PF GUI but
  *   might not be unique
  * @param nominalVoltage
  *   nominal voltage in kV
  * @param vTarget
  *   target voltage in p.u.
  * @param lat
  *   latitude
  * @param lon
  *   longitude
  * @param conElms
  *   connected elements to the node
  */
final case class Node(
    id: String,
    unsafeId: String,
    nominalVoltage: Double,
    vTarget: Double,
    lat: Option[Double],
    lon: Option[Double],
    conElms: List[ConnectedElement]
) extends EntityModel

object Node {

  /** Build a [[Node]] from a raw [[Nodes]]
    *
    * @param rawNode
    *   raw schema generated node
    * @return
    *   [[Node]]
    */
  def build(rawNode: Nodes): Node = {
    val id = rawNode.id.getOrElse(
      throw MissingParameterException(s"There is no id for node $rawNode")
    )
    val unsafeId = rawNode.locName.getOrElse(
      throw MissingParameterException(
        s"There is no unsafe id for node $rawNode"
      )
    )
    val nominalVoltage = rawNode.uknom.getOrElse(
      throw MissingParameterException(
        s"Node: $id has no defined nominal voltage"
      )
    )
    val vTarget = rawNode.vtarget.getOrElse(
      throw MissingParameterException(
        s"Node: $id has no defined target voltage"
      )
    )
    val conElms = rawNode.conElms
      .getOrElse(
        throw MissingParameterException(s"Node: $id has no connected elements")
      )
      .flatten
      .map(conElm => ConnectedElement.build(conElm))

    Node(
      id,
      unsafeId,
      nominalVoltage,
      vTarget,
      rawNode.GPSlat,
      rawNode.GPSlon,
      conElms
    )
  }
}
