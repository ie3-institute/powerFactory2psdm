/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.model.powerfactory

import edu.ie3.powerFactory2psdm.exception.pf.MissingParameterException
import edu.ie3.powerFactory2psdm.model.powerfactory.RawGridModel.{
  Loads,
  LoadsLV,
  LoadsMV
}

/**
 * Electrical load
 *
 * @param id identifier
 * @param s apparent power in MVA
 * @param cosphi cosinus phi value
 */
final case class Load(
    id: String,
    s: Double,
    cosphi: Double
) extends EntityModel

object Load {

  def build(input: Loads): Load = {
    val id = input.id.getOrElse(
      throw MissingParameterException(s"Load $input has no defined id.")
    )
    val s = input.slini.getOrElse(
      throw MissingParameterException(s"Load $id has no defined apparent power")
    )
    val cosphi = input.coslini.getOrElse(
      throw MissingParameterException(s"Load $id has no defined cosinus phi")
    )
    Load(id, s, cosphi)
  }

  def build(input: LoadsLV): Load = {
    val id = input.id.getOrElse(
      throw MissingParameterException(s"LV Load $input has no defined id.")
    )
    val s = input.slini.getOrElse(
      throw MissingParameterException(
        s"LV Load $id has no defined apparent power"
      )
    ) / 1000
    val p = input.plini.getOrElse(
      throw MissingParameterException(
        s"LV Load $id has no defined active power"
      )
    ) / 1000

    val cosphi = input.coslini.getOrElse(
      throw MissingParameterException(s"LV Load $id has no defined cosinus phi")
    )
    Load(id, s, cosphi)
  }

  def build(input: LoadsMV): Load = {
    val id = input.id.getOrElse(
      throw MissingParameterException(s"MV Load $input has no defined id.")
    )
    val s = input.slini.getOrElse(
      throw MissingParameterException(
        s"MV Load $id has no defined apparent power"
      )
    )
    val p = input.plini.getOrElse(
      throw MissingParameterException(
        s"MV Load $id has no defined active power"
      )
    )

    val cosphi = input.coslini.getOrElse(
      throw MissingParameterException(s"MV Load $id has no defined cosinus phi")
    )
    Load(id, s, cosphi)
  }
}
