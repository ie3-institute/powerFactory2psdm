/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.model.powerfactory

import edu.ie3.powerFactory2psdm.exception.pf.{
  ElementConfigurationException,
  MissingParameterException
}
import edu.ie3.powerFactory2psdm.model.powerfactory.RawGridModel.{
  ConElms,
  Nodes
}

import scala.annotation.tailrec

/**
  * Electrical node
  *
  * @param id identifier
  * @param nominalVoltage nominal voltage in kV
  * @param vTarget target voltage in p.u.
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
    conElms: List[ConnectedElement]
) extends EntityModel

object Node {

  /**
    * Build a [[Node]] from a raw [[Nodes]]
    *
    * @param rawNode raw schema generated node
    * @return [[Node]]
    */
  def build(rawNode: Nodes): Node = {
    val id = rawNode.id.getOrElse(
      throw MissingParameterException(s"There is no id for node $rawNode")
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
      nominalVoltage,
      vTarget,
      rawNode.GPSlat,
      rawNode.GPSlon,
      conElms
    )
  }
}
