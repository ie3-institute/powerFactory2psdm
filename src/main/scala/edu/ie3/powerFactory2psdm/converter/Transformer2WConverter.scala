/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter

import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.connector.Transformer2WInput
import edu.ie3.datamodel.models.input.connector.`type`.Transformer2WTypeInput
import edu.ie3.powerFactory2psdm.model.powerfactory.Transformer2W

import java.util.UUID

object Transformer2WConverter {

  def convert(
      rawTrafo: Transformer2W,
      nodeA: NodeInput,
      nodeB: NodeInput,
      trafoType: Transformer2WTypeInput
  ): Transformer2WInput = {

    val autotap = (rawTrafo.autoTap, rawTrafo.extTapCont) match {
      case (1, _)            => true
      case (_, Some(_)) => true
      case _                 => false
    }

    new Transformer2WInput(
      UUID.randomUUID(),
      rawTrafo.id,
      nodeA,
      nodeB,
      1,
      trafoType,
      rawTrafo.tapPos.toInt,
      autotap
    )
  }

}
