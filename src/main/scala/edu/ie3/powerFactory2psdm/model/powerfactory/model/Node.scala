package edu.ie3.powerFactory2psdm.model.powerfactory.model

import edu.ie3.powerFactory2psdm.exception.pf.MissingParameterException
import edu.ie3.powerFactory2psdm.model.powerfactory.PowerFactoryGrid.{ConElms, Nodes}

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
    val id = rawNode.id.getOrElse(
      throw MissingParameterException(s"Node: $rawNode has no id")
    )
    val nominalVoltage = rawNode.uknom.getOrElse(
      throw  MissingParameterException(s"Node: $id has no defined nominal voltage")
    )
    val vTarget = rawNode.vtarget.getOrElse(
      throw  MissingParameterException(s"Node: $id has no defined rated Voltage"))
    val conElms = rawNode.conElms.getOrElse(
      throw  MissingParameterException(s"Node: $id has no connected elements")
    ).flatten

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
