package edu.ie3.powerFactory2psdm.converter

import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.system.PvInput
import edu.ie3.datamodel.models.input.system.characteristic.ReactivePowerCharacteristic
import edu.ie3.powerFactory2psdm.config.ConversionConfig.PvParams
import edu.ie3.powerFactory2psdm.model.powerfactory.StaticGenerator
import tech.units.indriya.ComparableQuantity
import tech.units.indriya.quantity.Quantities
import edu.ie3.datamodel.models.StandardUnits.{AZIMUTH, EFFICIENCY, S_RATED}
import edu.ie3.powerFactory2psdm.exception.pf.ElementConfigurationException
import edu.ie3.powerFactory2psdm.util.RandomSampler.sample

import java.util.UUID
import javax.measure.quantity.{Angle, Dimensionless, Power}

object PvConverter {


  def convert(input: StaticGenerator, node: NodeInput, params: PvParams): PvInput = {
    // reactive power characteristic to follow
    // todo look into technische Anschlussrichtlinien
    val qCharacteristics: ReactivePowerCharacteristic = ???
    val albedo: Double = sample(params.albedo)
    val azimuth: ComparableQuantity[Angle] =
      Quantities.getQuantity(sample(params.azimuth), AZIMUTH)
    val etaConv: ComparableQuantity[Dimensionless] = Quantities.getQuantity(sample(params.etaConv), EFFICIENCY)
    val height: ComparableQuantity[Angle] = ???
    val kG: Double = sample(params.kG)
    val kT: Double = sample(params.kT)
    val sRated: ComparableQuantity[Power] = Quantities.getQuantity(input.sRated, S_RATED)
    val cosPhiRated = input.indCapFlag match {
      case 0 => input.cosPhi
      case 1 => -input.cosPhi
      case _ => throw ElementConfigurationException(
        s"The inductive capacitive specifier of the static generator: ${input.id} should be either 0 or 1"
      )
    }

    new PvInput(
      UUID.randomUUID(),
      input.id,
      node,
      qCharacteristics,
      albedo,
      azimuth,
      etaConv,
      height,
      kG,
      kT,
      false,
      sRated,
      cosPhiRated
    )
  }


  // todo: implement Conversion with .ElmPvSys

}
