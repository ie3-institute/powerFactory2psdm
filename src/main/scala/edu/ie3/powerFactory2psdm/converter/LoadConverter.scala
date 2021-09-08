/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter

import edu.ie3.datamodel.models.{BdewLoadProfile, OperationTime}
import edu.ie3.datamodel.models.input.{NodeInput, OperatorInput}
import edu.ie3.datamodel.models.input.system.LoadInput
import edu.ie3.datamodel.models.input.system.characteristic.CosPhiFixed
import edu.ie3.powerFactory2psdm.model.powerfactory.Load
import edu.ie3.util.quantities.PowerSystemUnits.{KILOWATTHOUR, MEGAVOLTAMPERE}
import tech.units.indriya.quantity.Quantities

import java.util.{Locale, UUID}

object LoadConverter {

  def convert(input: Load, node: NodeInput): LoadInput = {
    val id = input.id
    val cosPhi = input.indCapFlag match {
      case 0.0 => input.cosphi
      case 1.0 => -input.cosphi
    }
    val s = Quantities.getQuantity(input.s, MEGAVOLTAMPERE)
    val eCons = Quantities.getQuantity(0d, KILOWATTHOUR)
    val varCharacteristicString =
      "cosPhiFixed:{(0.0,%#.2f)}".formatLocal(Locale.ENGLISH, cosPhi)

    new LoadInput(
      UUID.randomUUID(),
      id,
      OperatorInput.NO_OPERATOR_ASSIGNED,
      OperationTime.notLimited(),
      node,
      new CosPhiFixed(varCharacteristicString),
      BdewLoadProfile.H0,
      false,
      eCons,
      s,
      cosPhi
    )
  }

}
