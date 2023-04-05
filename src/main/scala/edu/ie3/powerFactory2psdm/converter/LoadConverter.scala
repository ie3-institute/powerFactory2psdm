/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter

import edu.ie3.datamodel.models.OperationTime
import edu.ie3.datamodel.models.input.{NodeInput, OperatorInput}
import edu.ie3.datamodel.models.input.system.LoadInput
import edu.ie3.datamodel.models.input.system.characteristic.CosPhiFixed
import edu.ie3.datamodel.models.profile.BdewStandardLoadProfile
import edu.ie3.powerFactory2psdm.exception.pf.ElementConfigurationException
import edu.ie3.powerFactory2psdm.converter.NodeConverter.getNode
import edu.ie3.powerFactory2psdm.exception.pf.ConversionException
import edu.ie3.powerFactory2psdm.model.entity.Load
import edu.ie3.powerFactory2psdm.util.QuantityUtils.RichQuantityDouble

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
    val sRated = if (input.isScaled) {
      (input.s * input.scalingFactor.getOrElse(
        throw ElementConfigurationException(
          s"Load $id is specified as scaled but does not hold a scaling factor."
        )
      )).asVoltAmpere
    } else {
      input.s.asVoltAmpere
    }
    val eCons = 0d.asKiloWattHour
    val varCharacteristicString =
      "cosPhiFixed:{(0.0,%#.2f)}".formatLocal(Locale.ENGLISH, cosPhi)

    new LoadInput(
      UUID.randomUUID(),
      id,
      OperatorInput.NO_OPERATOR_ASSIGNED,
      OperationTime.notLimited(),
      node,
      new CosPhiFixed(varCharacteristicString),
      BdewStandardLoadProfile.H0,
      false,
      eCons,
      sRated,
      cosPhi
    )
  }
}
