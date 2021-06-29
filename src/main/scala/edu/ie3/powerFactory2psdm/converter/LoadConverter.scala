package edu.ie3.powerFactory2psdm.converter

import edu.ie3.datamodel.models.OperationTime
import edu.ie3.datamodel.models.StandardLoadProfile.DefaultLoadProfiles
import edu.ie3.datamodel.models.input.{NodeInput, OperatorInput}
import edu.ie3.datamodel.models.input.system.LoadInput
import edu.ie3.datamodel.models.input.system.characteristic.CosPhiFixed
import edu.ie3.powerFactory2psdm.model.powerfactory.Load
import edu.ie3.util.quantities.PowerSystemUnits.{KILOWATTHOUR, MEGAVAR, MEGAVOLTAMPERE, MEGAWATT}
import tech.units.indriya.quantity.Quantities

import java.util.{Locale, UUID}

object LoadConverter {

  def convert(input: Load, node: NodeInput) = {
    val id = input.id
    val cosphi = input.cosphi
    val s = Quantities.getQuantity(input.s, MEGAVOLTAMPERE)
//    val p = Quantities.getQuantity(input.p, MEGAWATT)
//    val q = input.q match {
//      case Some(x) => x
//      case None => {
//        val qVal = math.sqrt(input.s * input.s - input.p * input.p)
//        Quantities.getQuantity(qVal, MEGAVAR)
//      }
//    }
    val eCons = Quantities.getQuantity(0d, KILOWATTHOUR)
    val varCharacteristicString =
      "cosPhiFixed:{(0.0,%#.2f)}".formatLocal(Locale.ENGLISH, cosphi)

    new LoadInput(
      UUID.randomUUID(),
      id,
      OperatorInput.NO_OPERATOR_ASSIGNED,
      OperationTime.notLimited(),
      node,
      new CosPhiFixed(varCharacteristicString),
      DefaultLoadProfiles.NO_STANDARD_LOAD_PROFILE,
      false,
      eCons,
      s,
      cosphi
    )
  }

}
