/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter

import com.typesafe.scalalogging.LazyLogging
import edu.ie3.datamodel.models.OperationTime
import edu.ie3.datamodel.models.input.system.LoadInput
import edu.ie3.datamodel.models.input.system.characteristic.CosPhiFixed
import edu.ie3.datamodel.models.input.{NodeInput, OperatorInput}
import edu.ie3.datamodel.models.profile.{BdewStandardLoadProfile, LoadProfile, NbwTemperatureDependantLoadProfile}
import edu.ie3.powerFactory2psdm.converter.NodeConverter.getNode
import edu.ie3.powerFactory2psdm.exception.pf.{ConversionException, ElementConfigurationException}
import edu.ie3.powerFactory2psdm.model.entity.Load
import edu.ie3.powerFactory2psdm.util.QuantityUtils.RichQuantityDouble

import java.util.{Locale, UUID}
import scala.util.{Failure, Success}

object LoadConverter extends LazyLogging {

  def convertLoads(
      input: List[Load],
      nodes: Map[String, NodeInput]
  ): List[LoadInput] = {
    input flatMap { load =>
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

  def convert(input: Load, node: NodeInput): Option[LoadInput] = {
    input.id match {
      case loadId
          if loadId.endsWith("HH.ElmLod") | loadId.endsWith("NSPH.ElmLod") =>
        val cosPhi = ConversionHelper.determineCosPhiRated(
          input.indCapFlag,
          input.cosphi
        ) match {
          case Success(value) => value
          case Failure(exc) =>
            throw ConversionException(
              s"Couldn't determine cos phi rated for load: $loadId. Exception: ",
              exc
            )
        }
        val sRated = if (input.isScaled) {
          (input.s * input.scalingFactor.getOrElse(
            throw ElementConfigurationException(
              s"Load $loadId is specified as scaled but does not hold a scaling factor."
            )
          )).asVoltAmpere
        } else {
          input.s.asVoltAmpere
        }
        val eCons = 0d.asKiloWattHour
        val varCharacteristicString =
          "cosPhiFixed:{(0.0,%#.2f)}".formatLocal(Locale.ENGLISH, cosPhi)

        Some(
          new LoadInput(
            UUID.randomUUID(),
            loadId,
            OperatorInput.NO_OPERATOR_ASSIGNED,
            OperationTime.notLimited(),
            node,
            new CosPhiFixed(varCharacteristicString),
            getLoadProfile(loadId),
            false,
            eCons,
            sRated,
            cosPhi
          )
        )
      case wpId if wpId.endsWith("WP.ElmLod") =>
        logger.warn(
          s"Not converting load with id: $wpId as heat pump load conversion is not implemented."
        )
        None
      case emobId if emobId.endsWith("Emob.ElmLod") =>
        logger.warn(
          s"Not converting load with id: $emobId as e mobility load conversion is not implemented."
        )
        None
      case unknownId =>
        logger.warn(
          s"Not converting load with id: $unknownId as load type can't be determined."
        )
        None
    }
  }

  def getLoadProfile(loadId: String): LoadProfile = {
    if (loadId.endsWith("HH.ElmLod"))
      BdewStandardLoadProfile.H0
    else if (loadId.endsWith("NSPH.ElmLod"))
      NbwTemperatureDependantLoadProfile.EZ2
    else
      throw new IllegalArgumentException(
        s"No load profile for load id: $loadId"
      )
  }

}
