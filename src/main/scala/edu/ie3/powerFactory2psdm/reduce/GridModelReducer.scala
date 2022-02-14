/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.reduce

import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.container.JointGridContainer
import edu.ie3.datamodel.models.input.container.RawGridElements
import edu.ie3.datamodel.models.input.container.SystemParticipants
import edu.ie3.datamodel.models.input.system._
import edu.ie3.datamodel.models.input.system.characteristic.ReactivePowerCharacteristic
import edu.ie3.powerFactory2psdm.util.QuantityUtils.RichQuantityDouble
import edu.ie3.powerFactory2psdm.io.IoUtils
import edu.ie3.powerFactory2psdm.io.IoUtils.{
  getFileNamingStrategy,
  persistJointGridContainer
}

import java.io.File
import scala.jdk.CollectionConverters._
import java.util.UUID

object GridModelReducer {

  /** Reduces a grid by eliminating all system participants of the grid and
    * connecting a new one per node. Furthermore creates and writes mapping from
    * node to the connected system participant. This is done to have a grid
    * which we can map primary data to each node without any additional models
    * in the grid that draw or generate power. *
    */
  def main(args: Array[String]): Unit = {

    // input parameters
    val gridName = "exampleGrid"
    val csvSep = ","
    val inputDir = new File(
      "."
    ).getCanonicalPath + "/src/test/resources/psdmGrid/vn_146_lv_small"
    val inputUsesHierarchicNaming = false

    // output parameters
    val reducedGridName = "reduced_" + gridName
    val outputDir = new File(".").getCanonicalPath + "/out/reducedGrid"
    val outputUseHierarchicNaming = false

    // reduce grid
    val inputNamingStrategy =
      getFileNamingStrategy(inputUsesHierarchicNaming, inputDir, gridName)
    val inputGrid =
      IoUtils.parsePsdmGrid(
        reducedGridName,
        csvSep,
        inputDir,
        inputNamingStrategy
      )
    val reducedGrid = reduceGrid(
      reducedGridName,
      inputGrid
    )

    // persist reduced grid
    persistJointGridContainer(
      reducedGrid,
      reducedGridName,
      outputUseHierarchicNaming,
      outputDir,
      csvSep
    )

    // write out mapping from node to system participant in csv file
    val mappingFileName = outputDir + "/node_participant_mapping.csv"
    writeMapping(
      mappingFileName,
      reducedGrid.getSystemParticipants.getFixedFeedIns.asScala.toSet
    )
  }

  /** Reduces a grid by eliminating all system participants of the grid and
    * connecting a new one per node.
    *
    * @param inputGrid
    *   the grid which to reduce
    * @param reducedGridName
    *   name for the reduced grid
    */
  def reduceGrid(
      reducedGridName: String,
      inputGrid: JointGridContainer
  ): JointGridContainer = {

    val rawGridElements = inputGrid.getRawGrid

    // create a system participant for each node
    val fixedFeedIns = createFixedFeedIns(rawGridElements)
    val systemParticipants = new SystemParticipants(
      Set.empty[BmInput].asJava,
      Set.empty[ChpInput].asJava,
      Set.empty[EvcsInput].asJava,
      Set.empty[EvInput].asJava,
      fixedFeedIns.asJava,
      Set.empty[HpInput].asJava,
      Set.empty[LoadInput].asJava,
      Set.empty[PvInput].asJava,
      Set.empty[StorageInput].asJava,
      Set.empty[WecInput].asJava
    )

    new JointGridContainer(
      reducedGridName,
      rawGridElements,
      systemParticipants,
      inputGrid.getGraphics
    )

  }

  /** Create exactly one fixed feed in at every node within the grid.
    *
    * @param gridElements
    *   the grid's grid elements
    * @return
    *   the set of created [[FixedFeedInInput]]s
    */
  private def createFixedFeedIns(
      gridElements: RawGridElements
  ): Set[FixedFeedInInput] = {
    gridElements.getNodes.asScala.map(createFixedFeedIn).toSet
  }

  /** Create a fixed feed in at the supplied node.
    *
    * @param node
    *   the node for which to create a [[FixedFeedInInput]]
    * @return
    */
  private def createFixedFeedIn(node: NodeInput): FixedFeedInInput = {
    new FixedFeedInInput(
      UUID.randomUUID(),
      s"Participant-Node-${node.getUuid}",
      node,
      ReactivePowerCharacteristic.parse(
        s"cosPhiFixed:{(0.0, 0.95)}"
      ),
      1.0.asKiloWatt,
      0.95
    )
  }

  /** Write a mapping from system participant to the uuid it is connected to, to
    * a csv file. The resulting csv consists of two columns, the system
    * participant uuid and the node uuid at which the system participant is
    * connected.
    * @param fileName
    *   name of the resulting csv file
    * @param participants
    *   the participants for which to write the mapping
    * @tparam T
    *   the type of the system participant
    */
  private def writeMapping[T <: SystemParticipantInput](
      fileName: String,
      participants: Set[T]
  ): Unit = {
    val header = List("node", "participant")
    val rows = participants
      .map(participant =>
        List(participant.getNode.getUuid.toString, participant.getUuid.toString)
      )
      .toList
    IoUtils.writeCsvFile(fileName, header, rows)
  }

}
