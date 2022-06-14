/**
 * © 2021. Johannes Hiry,
 **/

package edu.ie3.powerFactory2psdm.model.entity

import edu.ie3.powerFactory2psdm.exception.pf.MissingParameterException
import edu.ie3.powerFactory2psdm.model.RawPfGridModel.Trafos2w

/** A 2 winding transformer
  *
  * @param id
  *   its identification
  * @param nodeHvId
  *   the id of its hv node
  * @param nodeLvId
  *   the id of its lv node
  * @param typeId
  *   the id of its type
  * @param tapPos
  *   the current tap position
  * @param autoTap
  *   signifier for automatic tap changing ()
  * @param extTapControl
  *   optional identification for any external tap controller of the model
  */
final case class Transformer2W(
    override val id: String,
    nodeHvId: String,
    nodeLvId: String,
    typeId: String,
    tapPos: Int,
    autoTap: Int,
    extTapControl: Option[String]
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
      throw MissingParameterException(
        s"Trafo2w: $id has no type. Transformer conversion without specified types is not supported."
      )
    )
    val tapPos = rawTrafo.nntap
      .getOrElse(
        throw MissingParameterException(s"Trafo2w: $id has no tap position.")
      )
      .toInt
    val autoTap = rawTrafo.ntrcn
      .getOrElse(
        throw MissingParameterException(
          s"Trafo2w: $id has no auto tap signifier."
        )
      )
      .toInt
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
