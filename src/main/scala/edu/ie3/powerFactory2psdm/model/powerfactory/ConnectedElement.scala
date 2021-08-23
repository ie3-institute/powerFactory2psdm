/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.model.powerfactory

import edu.ie3.powerFactory2psdm.exception.pf.MissingParameterException
import edu.ie3.powerFactory2psdm.model.powerfactory.RawPfGridModel.ConElms

/**
  * Data type that represents a ConnectedElement
  *
  * @param id identifier
  * @param pfCls PowerFactory class that element represents
  */
case class ConnectedElement(
    id: String,
    pfCls: String
) extends EntityModel

object ConnectedElement {
  def build(conElm: ConElms): ConnectedElement = {
    val id = conElm.id.getOrElse(
      throw MissingParameterException(
        s"There is no id for the connected element: $conElm"
      )
    )
    val pfCls = conElm.pfCls.getOrElse(
      throw MissingParameterException(
        s"There is no PowerFactory class mentioned for connected element: $conElm"
      )
    )
    ConnectedElement(id, pfCls)
  }
}
