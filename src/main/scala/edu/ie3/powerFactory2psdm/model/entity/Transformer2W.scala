/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.model.entity

import edu.ie3.powerFactory2psdm.exception.pf.MissingParameterException
import edu.ie3.powerFactory2psdm.model.RawPfGridModel.Trafos2w

case class Transformer2W(
    override val id: String,
    nodeHvId: String,
    nodeLvId: String,
    typeId: String,
    tapPos: Double,
    autoTap: Double,
    extTapCont: Option[String]
) extends EntityModel

object Transformer2W {

  def build(rawTrafo: Trafos2w): Transformer2W = {
    val id = rawTrafo.id.getOrElse(
      throw MissingParameterException(s"There is no id for 2w trafo: $rawTrafo")
    )
    val busHvId = rawTrafo.busHvId.getOrElse(
      throw MissingParameterException(s"Trafo2w: $id has no hv bus id.")
    )
    val busLvId = rawTrafo.busLvId.getOrElse(
      throw MissingParameterException(s"Trafo2w: $id has no lv bus id.")
    )
    val typeId = rawTrafo.typeId.getOrElse(
      throw MissingParameterException(s"Trafo2w: $id has no type id.")
    )
    val tapPos = rawTrafo.nntap.getOrElse(
      throw MissingParameterException(s"Trafo2w: $id has no tap position.")
    )
    val autoTap = rawTrafo.ntrcn.getOrElse(
      throw MissingParameterException(
        s"Trafo2w: $id has no auto tap signifier."
      )
    )
    val extTapCont = rawTrafo.cPtapc
    Transformer2W(
      id,
      busHvId,
      busLvId,
      typeId,
      tapPos,
      autoTap,
      extTapCont
    )
  }

}
