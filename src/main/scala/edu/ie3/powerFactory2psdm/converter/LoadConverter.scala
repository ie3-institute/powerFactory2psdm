/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter

import edu.ie3.datamodel.models.{BdewLoadProfile, OperationTime}
import edu.ie3.datamodel.models.input.{NodeInput, OperatorInput}
import edu.ie3.datamodel.models.input.system.LoadInput
import edu.ie3.datamodel.models.input.system.characteristic.CosPhiFixed
import edu.ie3.powerFactory2psdm.converter.NodeConverter.getNode
import edu.ie3.powerFactory2psdm.exception.pf.ConversionException
import edu.ie3.powerFactory2psdm.model.entity.Load
import edu.ie3.util.quantities.PowerSystemUnits.{KILOWATTHOUR, MEGAVOLTAMPERE}
import tech.units.indriya.quantity.Quantities

import java.util.{Locale, UUID}
import scala.util.{Failure, Success}

object LoadConverter {

  def convertLoads(
      input: List[Load],
      nodes: Map[String, NodeInput]
  ): List[LoadInput] = {
    input map { load =>
      getNode(load.nodeId, nodes) match {
        case Success(node) => convert(load, node)
        case Failure(exc) =>
          throw ConversionException(
            s"Could not convert load due to inability of finding its converted node with id: ${load.nodeId}",
            exc
          )
      }
    }
  }

  def convert(input: Load, node: NodeInput): LoadInput = {
    val id = input.id
    val cosPhi = ConversionHelper.determineCosPhiRated(
      input.indCapFlag,
      input.cosphi
    ) match {
      case Success(value) => value
      case Failure(exc) =>
        throw ConversionException(
          s"Couldn't determine cos phi rated for load: $id. Exception: ",
          exc
        )
    }
    val sRated = Quantities.getQuantity(input.s, MEGAVOLTAMPERE)
    val eCons = Quantities.getQuantity(0d, KILOWATTHOUR)
    val varCharacteristicString =
      "cosPhiFixed:{(0.0,%#.2f)}".formatLocal(Locale.ENGLISH, cosPhi)

    new LoadInput(
      UUID.randomUUID(),
      id,
      OperatorInput.NO_OPERATOR_ASSIGNED,
      OperationTime.notLimited(),
      node,
      new CosPhiFixed(varCharacteristicString),
      BdewLoadProfile.H0,
      false,
      eCons,
      sRated,
      cosPhi
    )
  }
}
