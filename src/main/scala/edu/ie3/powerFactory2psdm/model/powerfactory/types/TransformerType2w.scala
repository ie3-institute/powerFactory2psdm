/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.model.powerfactory.types

import edu.ie3.powerFactory2psdm.exception.pf.MissingParameterException
import edu.ie3.powerFactory2psdm.model.powerfactory.RawGridModel.TrafoTypes2w

/**
  * Transformer type
  *
  * @param id       Identifier
  * @param sRated   Rated apparent power in MVA
  * @param vRatedA  Rated voltage of the high voltage winding in kW
  * @param vRatedB  Rated voltage of the low voltage winding in kW
  * @param dV       Voltage magnitude deviation per tap position in %
  * @param dPhi     Voltage angle deviation per tap position in °
  * @param tapSide  Selection of winding, where the tap changer is installed (0 = OS, 1 = US).
  * @param tapNeutr Neutral tap position
  * @param tapMin   Minimum available tap position
  * @param tapMax   Maximum available tap position
  * @param uk       Short circuit current in pu
  * @param iNoLoad  No load current in %
  * @param pFe      Iron losses in kW
  * @param pCu      Copper losses in kW
  */
case class TransformerType2w(
    id: String,
    sRated: Double,
    vRatedA: Double,
    vRatedB: Double,
    dV: Double,
    dPhi: Double,
    tapSide: Double,
    tapNeutr: Double,
    tapMin: Double,
    tapMax: Double,
    uk: Double,
    iNoLoad: Double,
    pFe: Double,
    pCu: Double
) {

  def build(rawType: TrafoTypes2w): TransformerType2w = {
    val id = rawType.id.getOrElse(
      throw MissingParameterException(
        s"There is no id for transformer-type: $rawType"
      )
    )

    val sRated = rawType.strn.getOrElse(
      throw MissingParameterException(
        s"There is no rated apparent power for transformer-type: $id"
      )
    )

    val vRatedA = rawType.utrn_h.getOrElse(
      throw MissingParameterException(
        s"There is no voltage of high winding side for transformer-type: $id"
      )
    )

    val vRatedB = rawType.utrn_l.getOrElse(
      throw MissingParameterException(
        s"There is no voltage of low winding side for transformer-type: $id"
      )
    )

    val dV = rawType.dutap.getOrElse(
      throw MissingParameterException(
        s"There is no voltage magnitude deviation per tap position for transfomer type: $id"
      )
    )

    val dPhi = rawType.phitr.getOrElse(
      throw MissingParameterException(
        s"There is no voltage angle deviation per tap position for transformer-type: $id"
      )
    )

    val tapSide = rawType.tap_side.getOrElse(
      throw MissingParameterException(
        s"There is no selection of winding where tap changer is installed for transformer-type: $id"
      )
    )

    val tapNeutr = rawType.nntap0.getOrElse(
      throw MissingParameterException(
        s"There is no neutral tap position defined for transformer-type: $id"
      )
    )

    val tapMin = rawType.ntpmn.getOrElse(
      throw MissingParameterException(
        s"There is no minmum tap position defined for transformer-type: $id"
      )
    )

    val tapMax = rawType.ntpmx.getOrElse(
      throw MissingParameterException(
        s"There is no maximum tap position defined for transformer-type: $id"
      )
    )

    val uk = rawType.uktr.getOrElse(
      throw MissingParameterException(
        s"There is no short circuit voltage defined for transformer-type: $id"
      )
    )

    val iNoLoad = rawType.curmg.getOrElse(
      throw MissingParameterException(
        s"There is no no load current defined for transformer-type: $id"
      )
    )

    val pFe = rawType.pfe.getOrElse(
      throw MissingParameterException(
        s"There is no iron loss defined for transformer-type: $id"
      )
    )

    val pCu = rawType.pcutr.getOrElse(
      throw MissingParameterException(
        s"There is no iron loss defined for transformer-type: $id"
      )
    )

    TransformerType2w(
      id,
      sRated,
      vRatedA,
      vRatedB,
      dV,
      dPhi,
      tapSide,
      tapNeutr,
      tapMin,
      tapMax,
      uk,
      iNoLoad,
      pFe,
      pCu
    )
  }

}
