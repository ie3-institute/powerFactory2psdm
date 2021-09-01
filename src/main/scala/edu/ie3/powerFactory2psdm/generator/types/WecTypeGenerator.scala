/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.generator.types

import edu.ie3.datamodel.models.input.system.WecInput
import edu.ie3.datamodel.models.input.system.`type`.WecTypeInput
import edu.ie3.datamodel.models.input.system.characteristic.WecCharacteristicInput
import edu.ie3.powerFactory2psdm.config.ConversionConfig.WecModelGeneration
import edu.ie3.powerFactory2psdm.exception.pf.ElementConfigurationException
import edu.ie3.powerFactory2psdm.model.entity.StaticGenerator
import edu.ie3.powerFactory2psdm.util.RandomSampler
import edu.ie3.util.quantities.PowerSystemUnits
import edu.ie3.util.quantities.interfaces.{Currency, EnergyPrice}
import tech.units.indriya.ComparableQuantity
import tech.units.indriya.quantity.Quantities
import tech.units.indriya.unit.Units.{METRE, PERCENT, SQUARE_METRE}

import java.util.UUID
import javax.measure.quantity.{Area, Dimensionless, Length, Power}

object WecTypeGenerator {

  /** Generates a [[WecTypeInput]] for a [[WecInput]] model off of a
    * [[StaticGenerator]]. As a static generator does not hold all parameters
    * necessary, the other parameters are generated via the defined generation
    * methods for every parameter.
    *
    * @param statGen
    *   base model of the generation
    * @param params
    *   parameters for generating missing parameters
    * @return
    *   a [[WecTypeInput]]
    */
  def convert(
      statGen: StaticGenerator,
      params: WecModelGeneration
  ): WecTypeInput = {

    val capex: ComparableQuantity[Currency] = Quantities.getQuantity(
      RandomSampler.sample(params.capex),
      PowerSystemUnits.EURO
    )
    val opex: ComparableQuantity[EnergyPrice] = Quantities.getQuantity(
      RandomSampler.sample(params.opex),
      PowerSystemUnits.EURO_PER_MEGAWATTHOUR
    )
    val sRated: ComparableQuantity[Power] =
      Quantities.getQuantity(statGen.sRated, PowerSystemUnits.MEGAVOLTAMPERE)
    val cosPhiRated: Double = statGen.indCapFlag match {
      case 0 => statGen.cosPhi
      case 1 => -statGen.cosPhi
      case _ =>
        throw ElementConfigurationException(
          s"The inductive capacitive specifier of the static generator: ${statGen.id} should be either 0 or 1"
        )
    }
    val cpCharacteristic: WecCharacteristicInput = new WecCharacteristicInput(
      params.cpCharacteristic
    )
    val etaConv: ComparableQuantity[Dimensionless] =
      Quantities.getQuantity(RandomSampler.sample(params.etaConv), PERCENT)
    val rotorArea: ComparableQuantity[Area] = Quantities.getQuantity(
      RandomSampler.sample(params.rotorArea),
      SQUARE_METRE
    )
    val hubHeight: ComparableQuantity[Length] =
      Quantities.getQuantity(RandomSampler.sample(params.hubHeight), METRE)
    val id: String = s"WEC_Type_${sRated}MVA_${rotorArea}m2_${hubHeight}m"

    new WecTypeInput(
      UUID.randomUUID(),
      id,
      capex,
      opex,
      sRated,
      cosPhiRated,
      cpCharacteristic,
      etaConv,
      rotorArea,
      hubHeight
    )
  }

}
