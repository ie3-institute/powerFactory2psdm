/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter

import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.system.FixedFeedInInput
import edu.ie3.datamodel.models.input.system.characteristic.CosPhiFixed
import edu.ie3.powerFactory2psdm.exception.pf.ConversionException
import edu.ie3.powerFactory2psdm.model.powerfactory.StaticGenerator
import edu.ie3.util.quantities.PowerSystemUnits.MEGAVOLTAMPERE
import tech.units.indriya.quantity.Quantities

import java.util.{Locale, UUID}

object FixedFeedInConverter {

  def convert(input: StaticGenerator, node: NodeInput): FixedFeedInInput = {

    val cosPhi = input.indCapFlag match {
      case 0 => input.cosPhi
      case 1 => -input.cosPhi
      case _ =>
        throw ConversionException(
          "The leading/lagging specifier should be either 0 or 1 - I am confused!"
        )
    }
    val varCharacteristicString =
      "cosPhiFixed:{(0.0,%#.2f)}".formatLocal(Locale.ENGLISH, input.cosPhi)
    val s = Quantities.getQuantity(input.sRated, MEGAVOLTAMPERE)

    new FixedFeedInInput(
      UUID.randomUUID(),
      input.id,
      node,
      new CosPhiFixed(varCharacteristicString),
      s,
      cosPhi
    )
  }

}
