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
import edu.ie3.powerFactory2psdm.converter.NodeConverter.getNode
import edu.ie3.powerFactory2psdm.converter.types.Transformer2WTypeConverter.getTransformer2WType
import edu.ie3.powerFactory2psdm.exception.pf.ConversionException
import edu.ie3.powerFactory2psdm.model.entity.Transformer2W
import java.util.UUID
import scala.util.{Failure, Success}

object Transformer2WConverter extends LazyLogging {

  def convertTransformers(
      input: List[Transformer2W],
      nodes: Map[String, NodeInput],
      types: Map[String, Transformer2WTypeInput]
  ): List[Transformer2WInput] = {
    input map { transformer =>
      (
        getNode(transformer.nodeLvId, nodes),
        getNode(transformer.nodeHvId, nodes),
        getTransformer2WType(transformer.typeId, types)
      ) match {
        case (Success(nodeLv), Success(nodeHv), Success(transformer2WType)) =>
          Transformer2WConverter.convert(
            transformer,
            nodeHv,
            nodeLv,
            transformer2WType
          )
        case (Failure(exc), _, _) =>
          throw ConversionException(
            s"Could not find the converted lv node of 2W transformer: ${transformer.id}",
            exc
          )
        case (_, Failure(exc), _) =>
          throw ConversionException(
            s"Could not find the converted hv node of 2W transformer: ${transformer.id}",
            exc
          )
        case (_, _, Failure(exc)) =>
          throw ConversionException(
            s"Could not find the converted type of 2W transformer: ${transformer.id}",
            exc
          )
      }
    }
  }

  def convert(
      input: Transformer2W,
      nodeHv: NodeInput,
      nodeLv: NodeInput,
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
      nodeHv,
      nodeLv,
      1,
      trafoType,
      input.tapPos,
      autotap
    )
  }

}
