/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter

import com.typesafe.scalalogging.LazyLogging
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.connector.Transformer2WInput
import edu.ie3.datamodel.models.input.connector.`type`.Transformer2WTypeInput
import edu.ie3.powerFactory2psdm.model.entity.Transformer2W

import java.util.UUID

object Transformer2WConverter extends LazyLogging {

  def convert(
      input: Transformer2W,
      nodeA: NodeInput,
      nodeB: NodeInput,
      trafoType: Transformer2WTypeInput
  ): Transformer2WInput = {

    /* We consider a transformer to automatically adjust its tap position if either
     * autotap is set to true or an external tap control mechanism is set. Keep in mind
     * that we don't export the specifics of the external tap control. */
    val autotap = (input.autoTap, input.extTapControl) match {
      case (_, Some(extTapCont)) =>
        logger.debug(
          s"The transformers: ${input.id} external tap control $extTapCont is converted to SIMONAs auto tap mechanic"
        )
        true
      case (1, _) => true
      case _      => false
    }

    new Transformer2WInput(
      UUID.randomUUID(),
      input.id,
      nodeA,
      nodeB,
      1,
      trafoType,
      input.tapPos,
      autotap
    )
  }

}
