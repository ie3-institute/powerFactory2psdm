/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.reduce

import edu.ie3.datamodel.io.naming.FileNamingStrategy
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.container.JointGridContainer
import edu.ie3.datamodel.models.input.container.RawGridElements
import edu.ie3.datamodel.models.input.container.SystemParticipants
import edu.ie3.datamodel.models.input.system._
import edu.ie3.datamodel.models.input.system.characteristic.ReactivePowerCharacteristic
import edu.ie3.powerFactory2psdm.util.QuantityUtils.RichQuantityDouble
import edu.ie3.datamodel.io.sink.CsvFileSink
import edu.ie3.powerFactory2psdm.io.IoUtils

import java.io.File
import scala.jdk.CollectionConverters._
import java.util.UUID

object GridModelReducer {

  def main(args: Array[String]): Unit = {

    // input parameters
    val gridName = "exampleGrid"
    val csvSep = ","
    val inputFolderPath = new File(
      "."
    ).getCanonicalPath + "/src/test/resources/psdmGrid/vn_146_lv_small"
    val namingStrategy = new FileNamingStrategy()

    // output parameters
    val reducedGridName = "reduced_" + gridName
    val outputDir = new File(new File(".") + "/out/reducedGrid")

    reduceGrid(
      csvSep,
      inputFolderPath,
      namingStrategy,
      reducedGridName,
      outputDir
    )
  }

  /** Reduces a grid by eliminating all system participants of the grid and
    * connecting a new one per node. Furthermore creates and writes mapping from
    * node to the connected system participant. This is done to have a grid
    * which we can map primary data to each node without any additional models
    * in the grid that draw or generate power.
    *
    * @param csvSep
    *   csv separator of the grid to reduce
    * @param inputFolderPath
    *   folder path of the input grid
    * @param namingStrategy
    *   naming strategy used in input grid
    * @param reducedGridName
    *   name for the reduced grid
    * @param outputDir
    *   directory for storing grid and system participant mapping
    */
  def reduceGrid(
      csvSep: String,
      inputFolderPath: String,
      namingStrategy: FileNamingStrategy,
      reducedGridName: String,
      outputDir: File
  ): Unit = {

    val reducedGrid = reduceGrid(
      csvSep,
      inputFolderPath,
      namingStrategy,
      reducedGridName
    )
    if (!outputDir.exists()) {
      outputDir.mkdir()
    }
    val initEmptyFiles = false
    val sink =
      new CsvFileSink(
        outputDir.getCanonicalPath,
        namingStrategy,
        initEmptyFiles,
        csvSep
      )
    sink.persistJointGrid(reducedGrid)

    // write out mapping from node to system participant in csv file
    val mappingFileName = outputDir.getPath + "/node_participant_mapping.csv"
    writeMapping(
      mappingFileName,
      reducedGrid.getSystemParticipants.getFixedFeedIns.asScala.toSet
    )
  }

  /** Reduces a grid by eliminating all system participants of the grid and
    * connecting a new one per node. Furthermore creates and writes mapping from
    * node to the connected system participant. This is done to have a grid
    * which we can map primary data to each node without any additional models
    * in the grid that draw or generate power.
    *
    * @param csvSep
    *   csv separator of the grid to reduce
    * @param inputFolderPath
    *   folder path of the input grid
    * @param namingStrategy
    *   naming strategy used in input grid
    * @param reducedGridName
    *   name for the reduced grid
    * @param outputDir
    *   directory for storing grid and system participant mapping
    */
  private[reduce] def reduceGrid(
      csvSep: String,
      inputFolderPath: String,
      namingStrategy: FileNamingStrategy,
      reducedGridName: String
  ): JointGridContainer = {

    val inputGrid =
      IoUtils.parsePsdmGrid(
        reducedGridName,
        csvSep,
        inputFolderPath,
        namingStrategy
      )
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

  private def createFixedFeedIns(
      gridElements: RawGridElements
  ): Set[FixedFeedInInput] = {
    val nodes = gridElements.getNodes
    nodes.asScala.map(createFixedFeedIn).toSet
  }

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
