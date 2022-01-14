/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.reduce

import com.opencsv.CSVWriter
import edu.ie3.datamodel.exceptions.SourceException
import edu.ie3.datamodel.io.naming.FileNamingStrategy
import edu.ie3.datamodel.io.source.csv.CsvGraphicSource
import edu.ie3.datamodel.io.source.csv.CsvRawGridSource
import edu.ie3.datamodel.io.source.csv.CsvTypeSource
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.container.GraphicElements
import edu.ie3.datamodel.models.input.container.JointGridContainer
import edu.ie3.datamodel.models.input.container.RawGridElements
import edu.ie3.datamodel.models.input.container.SystemParticipants
import edu.ie3.datamodel.models.input.system._
import edu.ie3.datamodel.models.input.system.characteristic.ReactivePowerCharacteristic
import edu.ie3.powerFactory2psdm.util.QuantityUtils.RichQuantityDouble
import edu.ie3.datamodel.io.sink.CsvFileSink
import java.io.{BufferedWriter, File, FileWriter}
import scala.jdk.CollectionConverters._
import java.util.UUID
import scala.util.{Failure, Try}

object GridModelReducer {

  def main(args: Array[String]): Unit = {

    // input parameters
    val gridName = "exampleGrid"
    val csvSep = ","
    val inputFolderPath =
      "/Users/thomas/IdeaProjects/powerFactory2psdm/out/vn_146_lv_small/fullGrid"
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

    val (rawGridElements, graphicElements) =
      readGridModel(csvSep, inputFolderPath, namingStrategy)

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
    val reducedGrid = new JointGridContainer(
      reducedGridName,
      rawGridElements,
      systemParticipants,
      graphicElements
    )

    // write out reduced grid
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
    writeMapping(mappingFileName, fixedFeedIns)
  }

  private def readGridModel(
      csvSep: String,
      folderPath: String,
      namingStrategy: FileNamingStrategy
  ): (RawGridElements, GraphicElements) = {

    /* Instantiating sources */
    val typeSource = new CsvTypeSource(csvSep, folderPath, namingStrategy)
    val rawGridSource =
      new CsvRawGridSource(csvSep, folderPath, namingStrategy, typeSource)
    val graphicsSource = new CsvGraphicSource(
      csvSep,
      folderPath,
      namingStrategy,
      typeSource,
      rawGridSource
    )

    /* Loading models */
    val rawGridElements = rawGridSource.getGridData.orElseThrow(() =>
      new SourceException("Error during reading of raw grid data.")
    )
    val graphicElements = graphicsSource.getGraphicElements.orElseThrow(() =>
      new SourceException("Error during reading of graphic elements.")
    )
    (rawGridElements, graphicElements)
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
    writeCsvFile(fileName, header, rows)
  }

  /** Credits to isomarcte: https://stackoverflow.com/questions/52666231/how-to-write-to-a-csv-file-in-scala */
  private def writeCsvFile(
      fileName: String,
      header: List[String],
      rows: List[List[String]]
  ): Try[Unit] =
    Try(new CSVWriter(new BufferedWriter(new FileWriter(fileName)))).flatMap(
      (csvWriter: CSVWriter) =>
        Try {
          csvWriter.writeAll(
            (header +: rows).map(_.toArray).asJava
          )
          csvWriter.close()
        } match {
          case f @ Failure(_) =>
            Try(csvWriter.close()).recoverWith { case _ =>
              f
            }
          case success =>
            success
        }
    )
}
