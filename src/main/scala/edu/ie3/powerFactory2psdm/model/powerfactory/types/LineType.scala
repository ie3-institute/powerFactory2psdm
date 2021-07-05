/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.model.powerfactory.types

import edu.ie3.powerFactory2psdm.exception.pf.{
  ElementConfigurationException,
  MissingParameterException
}
import edu.ie3.powerFactory2psdm.model.powerfactory.EntityModel
import edu.ie3.powerFactory2psdm.model.powerfactory.RawGridModel.LineTypes

import scala.annotation.tailrec

/**
  * Electrical line
  *
  * @param id identifier
  * @param vRated rated voltage in kA
  * @param iMax thermal current in kA
  * @param r specific resistance in Ohm/km
  * @param x specific reactance in Ohm/km
  * @param b phase-to-ground conductance in micro Siemens/km
  * @param g phase-to-ground conductance in micro Siemens/km
  */
case class LineType(
    id: String,
    vRated: Double,
    iMax: Double,
    r: Double,
    x: Double,
    b: Double,
    g: Double
) extends EntityModel

object LineType {

  def build(rawLineType: LineTypes): LineType = {
    val id = rawLineType.id.getOrElse(
      throw MissingParameterException(
        s"There is no id for line type $rawLineType"
      )
    )
    val vRated = rawLineType.uline.getOrElse(
      throw MissingParameterException(
        s"There is no rated voltage defined for line type: $id"
      )
    )
    val iMax = rawLineType.sline.getOrElse(
      throw MissingParameterException(
        s"There is no maximum thermal current defined for line type: $id"
      )
    )
    val r = rawLineType.rline.getOrElse(
      throw MissingParameterException(
        s"There is no specific resistance defined for line type: $id"
      )
    )
    val x = rawLineType.xline.getOrElse(
      throw MissingParameterException(
        s"There is no specific reactance defined for line type: $id"
      )
    )
    val b = rawLineType.bline.getOrElse(
      throw MissingParameterException(
        s"There is no phase-to-ground conductance defined for line type: $id"
      )
    )
    val g = rawLineType.gline.getOrElse(
      throw MissingParameterException(
        s"There is no phase-to-ground susceptance defined for line type: $id"
      )
    )

    LineType(
      id,
      vRated,
      iMax,
      r,
      x,
      b,
      g
    )
  }
}
