/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.generator

import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.system.PvInput
import edu.ie3.datamodel.models.input.system.characteristic.ReactivePowerCharacteristic
import edu.ie3.powerFactory2psdm.config.ConversionConfig.PvModelGeneration
import edu.ie3.powerFactory2psdm.converter.ConversionHelper.{
  determineCosPhiRated,
  convertQCharacteristic
}
import edu.ie3.powerFactory2psdm.model.entity.StaticGenerator
import edu.ie3.powerFactory2psdm.exception.pf.ElementConfigurationException
import edu.ie3.powerFactory2psdm.model.entity.StaticGenerator
import edu.ie3.powerFactory2psdm.util.RandomSampler.sample
import edu.ie3.util.quantities.PowerSystemUnits.{DEGREE_GEOM, MEGAVOLTAMPERE}
import tech.units.indriya.ComparableQuantity
import tech.units.indriya.quantity.Quantities
import tech.units.indriya.unit.Units.PERCENT

import java.util.UUID
import javax.measure.quantity.{Angle, Dimensionless, Power}

object PvInputGenerator {

  /** Generates a [[PvInput]] model from a [[StaticGenerator]]. As a static
    * generator does not hold all parameters necessary, the other parameters are
    * generated via the defined generation methods for every parameter.
    *
    * @param input
    *   base model for generating a [[PvInput]]
    * @param node
    *   the node the input is connected to
    * @param params
    *   parameters for generating missing parameters
    * @return
    *   a [[PvInput]]
    */
  def generate(
      input: StaticGenerator,
      node: NodeInput,
      params: PvModelGeneration
  ): PvInput = {
    val albedo: Double = sample(params.albedo)
    val azimuth: ComparableQuantity[Angle] =
      Quantities.getQuantity(sample(params.azimuth), DEGREE_GEOM)
    val etaConv: ComparableQuantity[Dimensionless] =
      Quantities.getQuantity(sample(params.etaConv), PERCENT)
    val height: ComparableQuantity[Angle] =
      Quantities.getQuantity(sample(params.elevationAngle), DEGREE_GEOM)
    val kG: Double = sample(params.kG)
    val kT: Double = sample(params.kT)
    val sRated: ComparableQuantity[Power] =
      Quantities.getQuantity(input.sRated, MEGAVOLTAMPERE)
    val cosPhiRated = determineCosPhiRated(input)
    val qCharacteristics: ReactivePowerCharacteristic =
      convertQCharacteristic(params.qCharacteristic, cosPhiRated)

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
