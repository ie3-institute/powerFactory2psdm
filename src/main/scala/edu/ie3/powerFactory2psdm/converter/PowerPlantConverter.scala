package edu.ie3.powerFactory2psdm.converter

import edu.ie3.datamodel.models.OperationTime
import edu.ie3.datamodel.models.input.system.FixedFeedInInput
import edu.ie3.datamodel.models.input.system.characteristic.CosPhiFixed
import edu.ie3.datamodel.models.input.{NodeInput, OperatorInput}
import edu.ie3.powerFactory2psdm.exception.pf.ConversionException
import edu.ie3.powerFactory2psdm.model.powerfactory.PowerPlant
import edu.ie3.util.quantities.PowerSystemUnits.MEGAVOLTAMPERE
import tech.units.indriya.quantity.Quantities

import java.util.{Locale, UUID}

object PowerPlantConverter {

  def convert(input: PowerPlant, node: NodeInput): FixedFeedInInput = {

    val cosPhi = input.indCap match {
      case 0 => input.cosPhi
      case 1 => - input.cosPhi
      case _ => throw ConversionException("The inductive capacitive specifier should be either 0 or 1 - I am confused!")
    }
    val varCharacteristicString = "cosPhiFixed:{(0.0,%#.2f)}".formatLocal(Locale.ENGLISH, input.cosPhi)
    val s = Quantities.getQuantity(input.s, MEGAVOLTAMPERE)

    new FixedFeedInInput(
      UUID.randomUUID(),
      input.id,
      OperatorInput.NO_OPERATOR_ASSIGNED,
      OperationTime.notLimited(),
      node,
      new CosPhiFixed(varCharacteristicString),
      s,
      cosPhi
    )
  }

}
