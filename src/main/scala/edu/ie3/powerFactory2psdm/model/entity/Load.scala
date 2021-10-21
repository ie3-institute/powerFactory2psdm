/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.model.entity

import edu.ie3.powerFactory2psdm.exception.pf.MissingParameterException
import edu.ie3.powerFactory2psdm.model.RawPfGridModel.{Loads, LoadsLV, LoadsMV}
import edu.ie3.powerFactory2psdm.model.entity.EntityModel

/** Electrical load
  *
  * @param id
  *   identifier
  * @param s
  *   apparent power in MVA
  * @param cosphi
  *   cosinus phi value
  */
final case class Load(
    id: String,
    nodeId: String,
    s: Double,
    cosphi: Double,
    indCapFlag: Int
) extends EntityModel

object Load {

  def build(input: Loads): Load = {
    val id = input.id.getOrElse(
      throw MissingParameterException(s"Load $input has no defined id.")
    )
    val nodeId = input.bus1Id.getOrElse(
      throw MissingParameterException(s"Load $id has no defined bus")
    )
    val s = input.slini.getOrElse(
      throw MissingParameterException(s"Load $id has no defined apparent power")
    )
    val cosphi = input.coslini.getOrElse(
      throw MissingParameterException(s"Load $id has no defined cosinus phi")
    )
    val indCap = input.pf_recap
      .getOrElse(
        throw MissingParameterException(
          s"Load $id has no defined inductive/capacitive specifier"
        )
      )
      .toInt
    Load(id, nodeId, s, cosphi, indCap)
  }

  def build(input: LoadsLV): Load = {
    val id = input.id.getOrElse(
      throw MissingParameterException(s"LV Load $input has no defined id.")
    )
    val nodeId = input.bus1Id.getOrElse(
      throw MissingParameterException(s"LV Load $id has no defined bus")
    )
    val s = input.slini.getOrElse(
      throw MissingParameterException(
        s"LV Load $id has no defined apparent power"
      )
    ) / 1000
    val cosphi = input.coslini.getOrElse(
      throw MissingParameterException(s"LV Load $id has no defined cosinus phi")
    )
    val indCap = input.pf_recap
      .getOrElse(
        throw MissingParameterException(
          s"LV Load $id has no defined inductive/capacitive specifier"
        )
      )
      .toInt
    Load(id, nodeId, s, cosphi, indCap)
  }

  def build(input: LoadsMV): Load = {
    val id = input.id.getOrElse(
      throw MissingParameterException(s"MV Load $input has no defined id.")
    )
    val nodeId = input.bus1Id.getOrElse(
      throw MissingParameterException(s"MV Load $id has no defined bus")
    )
    val s = input.slini.getOrElse(
      throw MissingParameterException(
        s"MV Load $id has no defined apparent power"
      )
    )
    val cosphi = input.coslini.getOrElse(
      throw MissingParameterException(s"MV Load $id has no defined cosinus phi")
    )
    val indCap = input.pf_recap
      .getOrElse(
        throw MissingParameterException(
          s"MV Load $id has no defined inductive/capacitive specifier"
        )
      )
      .toInt
    Load(id, nodeId, s, cosphi, indCap)
  }
}
