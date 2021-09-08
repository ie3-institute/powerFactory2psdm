/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.generator.types

import edu.ie3.datamodel.models.input.system.`type`.StorageTypeInput
import edu.ie3.powerFactory2psdm.config.model.BsConversionConfig.BsModelGeneration
import edu.ie3.powerFactory2psdm.converter.ConversionHelper
import edu.ie3.powerFactory2psdm.model.entity.StaticGenerator
import edu.ie3.powerFactory2psdm.util.RandomSampler
import edu.ie3.util.quantities.interfaces.DimensionlessRate
import tech.units.indriya.ComparableQuantity

import java.util.UUID
import javax.measure.quantity.{Dimensionless, Energy, Power, Time}
import edu.ie3.powerFactory2psdm.util.QuantityUtils._

object StorageTypeInputGenerator {

  def generate(
      statGen: StaticGenerator,
      params: BsModelGeneration
  ): StorageTypeInput = {
    val capex = RandomSampler.sample(params.capex).toEuro
    val opex = RandomSampler.sample(params.opex).toEuroPerMegaWattHour
    val eStorage: ComparableQuantity[Energy] =
      RandomSampler.sample(params.eStorage).toKiloWattHour
    val sRated: ComparableQuantity[Power] = statGen.sRated.toMegaVoltAmpere
    val cosPhiRated: Double = ConversionHelper.determineCosPhiRated(statGen)
    val pMax: ComparableQuantity[Power] =
      RandomSampler.sample(params.pMax).toKiloWatt
    val activePowerGradient: ComparableQuantity[DimensionlessRate] =
      RandomSampler.sample(params.activePowerGradient).toPercentPerHour
    val eta: ComparableQuantity[Dimensionless] =
      RandomSampler.sample(params.eta).toPercent
    val dod: ComparableQuantity[Dimensionless] =
      RandomSampler.sample(params.dod).toPercent
    val lifeTime: ComparableQuantity[Time] =
      RandomSampler.sample(params.lifeTime).toHour
    val lifeCycle: Int = RandomSampler.sample(params.lifeCycle).toInt

    new StorageTypeInput(
      UUID.randomUUID(),
      statGen.id + "_type",
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
