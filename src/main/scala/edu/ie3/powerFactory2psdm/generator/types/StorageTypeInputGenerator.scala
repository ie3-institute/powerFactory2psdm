/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.generator.types

import edu.ie3.datamodel.models.input.system.`type`.StorageTypeInput
import edu.ie3.powerFactory2psdm.config.model.BsConversionConfig.BatteryStorageModelGeneration
import edu.ie3.powerFactory2psdm.exception.pf.ElementConfigurationException
import edu.ie3.powerFactory2psdm.model.entity.StaticGenerator
import edu.ie3.powerFactory2psdm.util.RandomSampler
import edu.ie3.util.quantities.PowerSystemUnits
import edu.ie3.util.quantities.interfaces.{
  Currency,
  DimensionlessRate,
  EnergyPrice
}
import tech.units.indriya.ComparableQuantity
import tech.units.indriya.quantity.Quantities

import java.util.UUID
import javax.measure.quantity.{Dimensionless, Energy, Power, Time}

object StorageTypeInputGenerator {

  def generate(
      statGen: StaticGenerator,
      params: BatteryStorageModelGeneration
  ): StorageTypeInput = {
    val capex: ComparableQuantity[Currency] = Quantities.getQuantity(
      RandomSampler.sample(params.capex),
      PowerSystemUnits.EURO
    )
    val opex: ComparableQuantity[EnergyPrice] = Quantities.getQuantity(
      RandomSampler.sample(params.opex),
      PowerSystemUnits.EURO_PER_MEGAWATTHOUR
    )
    val eStorage: ComparableQuantity[Energy] = Quantities.getQuantity(
      RandomSampler.sample(params.eStorage),
      PowerSystemUnits.KILOWATTHOUR
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
    val pMax: ComparableQuantity[Power] = Quantities.getQuantity(
      RandomSampler.sample(params.pMax),
      PowerSystemUnits.KILOWATT
    )
    val activePowerGradient: ComparableQuantity[DimensionlessRate] =
      Quantities.getQuantity(
        RandomSampler.sample(params.activePowerGradient),
        PowerSystemUnits.PERCENT_PER_HOUR
      )
    val eta: ComparableQuantity[Dimensionless] = Quantities.getQuantity(
      RandomSampler.sample(params.eta),
      tech.units.indriya.unit.Units.PERCENT
    )
    val dod: ComparableQuantity[Dimensionless] = Quantities.getQuantity(
      RandomSampler.sample(params.dod),
      tech.units.indriya.unit.Units.PERCENT
    )
    val lifeTime: ComparableQuantity[Time] = Quantities.getQuantity(
      RandomSampler.sample(params.lifeTime),
      tech.units.indriya.unit.Units.HOUR
    )
    val lifeCycle: Int = RandomSampler.sample(params.lifeCycle).toInt

    new StorageTypeInput(
      UUID.randomUUID(),
      statGen.id,
      capex,
      opex,
      eStorage,
      sRated,
      cosPhiRated,
      pMax,
      activePowerGradient,
      eta,
      dod,
      lifeTime,
      lifeCycle
    )
  }

}
