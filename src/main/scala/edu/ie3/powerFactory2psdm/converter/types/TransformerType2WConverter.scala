/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter.types

import edu.ie3.datamodel.models.input.connector.`type`.Transformer2WTypeInput
import edu.ie3.powerFactory2psdm.exception.pf.{
  ConversionException,
  ElementConfigurationException
}
import edu.ie3.powerFactory2psdm.model.entity.types.TransformerType2W
import edu.ie3.powerFactory2psdm.util.QuantityUtil.RichQuantityDouble
import math.{pow, sqrt}
import java.util.UUID

object TransformerType2WConverter {

  def convert(input: TransformerType2W): Transformer2WTypeInput = {

    val sRated = input.sRated * 1e6
    val vRatedA = input.vRatedA * 1e3
    val vRatedB = input.vRatedB * 1e3
    val pCu = input.pCu * 1e3
    val pFe = input.pFe * 1e3
    val uk = (input.uk / 100) * vRatedA
    val iRated = sRated / (math.sqrt(3) * vRatedA)
    val iNoLoadNom = (input.iNoLoad / 100) * iRated

    // short circuit experiment
    val rSc = pCu / (3 * pow(iRated, 2))
    val zSc = (uk / sqrt(3)) / iRated
    if (rSc > zSc) {
      throw ConversionException(
        s"Short circuit experiment calculations of 2w transformer type: ${input.id} is not possible due to faulty " +
          s"parameters. The short circuit resistance can't exceed the short circuit impedance."
      )
    }
    val xSc = sqrt(pow(zSc, 2) - pow(rSc, 2))

    // no load experiment
    val yNoLoad = iNoLoadNom / (vRatedA / sqrt(3))
    val gNoLoad = pFe / pow(vRatedA, 2)
    if (gNoLoad > yNoLoad) {
      throw ConversionException(
        s"No load experiment calculations of 2w transformer type: ${input.id} is not possible due to faulty parameters." +
          s"The no load conductance can't exceed the no load admittance."
      )
    }
    val bNoLoad = sqrt((pow(yNoLoad, 2) - pow(gNoLoad, 2)).doubleValue)

    val tapSide = input.tapSide match {
      case 0 => false
      case 1 => true
      case _ =>
        throw ElementConfigurationException(
          s"The tap side of the transformer-type ${input.id} is neither 0 nor 1 - I am confused!"
        )
    }

    new Transformer2WTypeInput(
      UUID.randomUUID(),
      input.id,
      rSc.toOhm,
      xSc.toOhm,
      sRated.toMegaVoltAmpere,
      vRatedA.toVolt,
      vRatedB.toVolt,
      gNoLoad.toSiemens,
      bNoLoad.toSiemens,
      input.dV.toPercent,
      input.dPhi.toDegreeGeom,
      tapSide,
      input.tapNeutr.toInt,
      input.tapMin.toInt,
      input.tapMax.toInt
    )

  }

}
