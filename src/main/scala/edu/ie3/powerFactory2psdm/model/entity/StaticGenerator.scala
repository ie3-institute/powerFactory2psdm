/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.model.entity

import edu.ie3.powerFactory2psdm.exception.pf.MissingParameterException
import edu.ie3.powerFactory2psdm.model.RawPfGridModel.StatGen

final case class StaticGenerator(
    id: String,
    busId: String,
    sRated: Double,
    cosPhi: Double,
    indCapFlag: Int,
    category: String
) extends EntityModel

object StaticGenerator {

  def build(input: StatGen): StaticGenerator = {
    val id = input.id.getOrElse(
      throw MissingParameterException(
        s"There is no id for static generator: $input"
      )
    )
    val busId = input.busId.getOrElse(
      throw MissingParameterException(
        s"There is no id of a connected bus for static generator: $id"
      )
    )
    val sRated = input.sgn.getOrElse(
      throw MissingParameterException(
        s"There is no rated power defined for static generator: $id"
      )
    )
    val cosPhi = input.cosgini.getOrElse(
      throw MissingParameterException(
        s"There is no cos phi defined for static generator: $id"
      )
    )
    val indCapFlag = input.pf_recap
      .getOrElse(
        throw MissingParameterException(
          s"There is no inductive capacitive specifier defined for static generator: $id"
        )
      )
      .toInt
    val category = input.cCategory.getOrElse(
      throw MissingParameterException(
        s"There is no category specifier defined for static generator: $id"
      )
    )

    StaticGenerator(id, busId, sRated, cosPhi, indCapFlag, category)
  }

}
