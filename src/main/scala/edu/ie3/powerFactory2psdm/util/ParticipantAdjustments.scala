/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.util

import com.typesafe.scalalogging.LazyLogging
import edu.ie3.datamodel.models.input.container.{
  JointGridContainer,
  SystemParticipants
}
import edu.ie3.datamodel.models.input.system.LoadInput
import edu.ie3.datamodel.models.input.system.characteristic.CosPhiFixed
import edu.ie3.datamodel.models.profile.BdewStandardLoadProfile
import edu.ie3.powerFactory2psdm.config.model.PvConversionConfig.PvModelGeneration
import edu.ie3.powerFactory2psdm.converter.ConversionHelper.lvGenerationCosPhi
import edu.ie3.powerFactory2psdm.generator.PvInputGenerator
import edu.ie3.powerFactory2psdm.util.ParticipantInformation.Participants
import edu.ie3.util.quantities.QuantityUtils.RichQuantityDouble

import java.util.{Locale, UUID}
import scala.jdk.CollectionConverters.{IterableHasAsScala, SetHasAsJava}

object ParticipantAdjustments extends LazyLogging {

  def adjust(
      jgc: JointGridContainer,
      nodalParticipantInfo: Map[
        String,
        Map[Participants.Value, ParticipantInformation]
      ],
      pvModelGeneration: PvModelGeneration
  ): JointGridContainer = {
    val nodes = jgc.getRawGrid.getNodes.asScala
    val nodesMap = nodes.map(node => node.getId -> node).toMap
    val oldSystemParticipants = jgc.getSystemParticipants
    val updatedParticipants = nodalParticipantInfo.foldLeft(
      oldSystemParticipants
    )((systemParticipants, mapEntry) => {
      val (id, participantInfos) = mapEntry
      val node = nodesMap.getOrElse(
        id,
        throw new NoSuchElementException(s"Node with id $id not found")
      )
      participantInfos.foldLeft(systemParticipants)((sp, info) => {
        val (participant, participantInfo) = info
        participant match {
          case Participants.LOAD =>
            val oldLoads = sp.getLoads.asScala.filter(_.getNode == node)
            val cosPhi = oldLoads.map(_.getCosPhiRated).sum / oldLoads.size
            val oldSRated =
              oldLoads.map(_.getsRated()).reduce((a, b) => a.add(b))
            val newLoads = (0 until participantInfo.count).map(count => {
              val energy = 3500d.asKiloWattHour
              val varCharacteristicString =
                "cosPhiFixed:{(0.0,%#.2f)}".formatLocal(Locale.ENGLISH, cosPhi)
              val sRated = oldSRated.divide(participantInfo.count)
              new LoadInput(
                UUID.randomUUID(),
                id + f"-Load-$count",
                node,
                new CosPhiFixed(varCharacteristicString),
                BdewStandardLoadProfile.H0,
                false,
                energy,
                sRated,
                cosPhi
              )
            })
            val updatedLoads =
              sp.getLoads.asScala.filter(_.getNode != node) ++ newLoads
            new SystemParticipants(
              sp.getBmPlants,
              sp.getChpPlants,
              sp.getEvCS,
              sp.getEvs,
              sp.getFixedFeedIns,
              sp.getHeatPumps,
              updatedLoads.toSet.asJava,
              sp.getPvPlants,
              sp.getStorages,
              sp.getWecPlants,
              sp.getEmSystems
            )

          case Participants.PV =>
            val newPvs = (0 until participantInfo.count).map(count => {
              val power = participantInfo.power.divide(participantInfo.count)
              PvInputGenerator.generate(
                node,
                id + s"-PV-$count",
                power,
                lvGenerationCosPhi(power),
                pvModelGeneration
              )
            })
            val updatedPvs = sp.getPvPlants.asScala
              .filter(_.getNode != node) ++ newPvs
            new SystemParticipants(
              sp.getBmPlants,
              sp.getChpPlants,
              sp.getEvCS,
              sp.getEvs,
              sp.getFixedFeedIns,
              sp.getHeatPumps,
              sp.getLoads,
              updatedPvs.toSet.asJava,
              sp.getStorages,
              sp.getWecPlants,
              sp.getEmSystems
            )
          case participant: Participants.Value =>
            logger.warn(
              s"Handling $participant adjustment information not supported yet. Skipping ..."
            )
            sp
        }
      })
    })
    new JointGridContainer(
      jgc.getGridName,
      jgc.getRawGrid,
      updatedParticipants,
      jgc.getGraphics,
      jgc.getSubGridTopologyGraph
    )
  }
}
