/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter

import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.system.FixedFeedInInput
import edu.ie3.powerFactory2psdm.config.ConversionConfigUtils.QCharacteristic
import edu.ie3.powerFactory2psdm.converter.ConversionHelper.{
  convertQCharacteristic,
  determineCosPhiRated
}
import edu.ie3.powerFactory2psdm.model.entity.StaticGenerator
import edu.ie3.powerFactory2psdm.util.QuantityUtils.RichQuantityDouble
import edu.ie3.util.quantities.PowerSystemUnits.MEGAVOLTAMPERE
import tech.units.indriya.quantity.Quantities

import java.util.UUID

object FixedFeedInConverter {

  /** Converts a static generator to a [[FixedFeedInInput]]
    *
    * @param input
    *   generator to convert
    * @param node
    *   node the static generator is connected to
    * @return
    *   a fixed feed-in
    */
  def convert(
      input: StaticGenerator,
      node: NodeInput,
      qCharacteristic: QCharacteristic
  ): FixedFeedInInput = {

    val cosPhiRated = determineCosPhiRated(input)
    val reactivePowerCharacteristic =
      convertQCharacteristic(qCharacteristic, cosPhiRated)
    val s = input.sRated.toMegaVoltAmpere

    new FixedFeedInInput(
      UUID.randomUUID(),
      input.id,
      node,
      reactivePowerCharacteristic,
      s,
      cosPhiRated
    )
  }

}
