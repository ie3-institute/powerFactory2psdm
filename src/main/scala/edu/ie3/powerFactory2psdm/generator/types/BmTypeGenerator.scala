/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.generator.types

import edu.ie3.datamodel.models.input.system.`type`.BmTypeInput
import edu.ie3.powerFactory2psdm.config.model.BmConversionConfig.BmModelGeneration
import edu.ie3.powerFactory2psdm.converter.ConversionHelper
import edu.ie3.powerFactory2psdm.model.entity.StaticGenerator
import edu.ie3.powerFactory2psdm.util.QuantityUtils.RichQuantityDouble
import edu.ie3.powerFactory2psdm.util.RandomSampler
import edu.ie3.util.quantities.interfaces.{Currency, EnergyPrice}
import tech.units.indriya.ComparableQuantity

import java.util.UUID
import javax.measure.quantity.Power

object BmTypeGenerator {

  /** Generates a [[BmTypeInput]] for a [[BmInput]] model off of a
    * [[StaticGenerator]]. As a static generator does not hold all parameters
    * necessary, the other parameters are generated via the defined generation
    * methods for every parameter.
    *
    * @param statGen
    *   base model of the generation
    * @param params
    *   parameters for generating missing parameters
    * @return
    *   a [[BmTypeInput]]
    */
  def convert(
      statGen: StaticGenerator,
      params: BmModelGeneration
  ): BmTypeInput = {

    val capex: ComparableQuantity[Currency] =
      RandomSampler.sample(params.capex).toEuro
    val opex: ComparableQuantity[EnergyPrice] =
      RandomSampler.sample(params.opex).toEuroPerMegaWattHour
    val sRated: ComparableQuantity[Power] = statGen.sRated.toMegaVoltAmpere
    val cosPhiRated: Double = ConversionHelper.determineCosPhiRated(statGen)
    val activePowerGradient =
      RandomSampler.sample(params.activePowerGradient).toPercentPerHour
    val etaConv = RandomSampler.sample(params.etaConv).toPercent
    val id = s"BM_Type_${sRated}MVA"

    new BmTypeInput(
      UUID.randomUUID(),
      id,
      capex,
      opex,
      activePowerGradient,
      sRated,
      cosPhiRated,
      etaConv
    )
  }
}
