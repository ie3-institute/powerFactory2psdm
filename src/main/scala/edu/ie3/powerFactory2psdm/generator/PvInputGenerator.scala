/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.generator

import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.system.PvInput
import edu.ie3.datamodel.models.input.system.characteristic.ReactivePowerCharacteristic
import edu.ie3.powerFactory2psdm.config.model.PvConversionConfig.PvModelGeneration
import edu.ie3.powerFactory2psdm.converter.ConversionHelper.{
  convertQCharacteristic,
  determineCosPhiRated
}
import edu.ie3.powerFactory2psdm.model.entity.StaticGenerator
import edu.ie3.powerFactory2psdm.util.QuantityUtils.RichQuantityDouble
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
    * generated via the defined parameter sampling methods for every parameter.
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
    val albedo = sample(params.albedo)
    val azimuth = sample(params.azimuth).toDegreeGeom
    val etaConv = sample(params.etaConv).toPercent
    val height = sample(params.elevationAngle).toDegreeGeom
    val kG = sample(params.kG)
    val kT = sample(params.kT)
    val sRated = input.sRated.toMegaVoltAmpere
    val cosPhiRated = determineCosPhiRated(input)
    val qCharacteristics =
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

}
