/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.io

import com.opencsv.CSVWriter
import com.typesafe.scalalogging.LazyLogging
import edu.ie3.datamodel.exceptions.SourceException
import edu.ie3.datamodel.io.naming.FileNamingStrategy
import edu.ie3.datamodel.io.source.csv.{
  CsvGraphicSource,
  CsvRawGridSource,
  CsvTypeSource
}
import edu.ie3.powerFactory2psdm.model.RawPfGridModel
import io.circe.parser.decode
import io.circe.generic.auto._
import edu.ie3.datamodel.io.source.csv.CsvSystemParticipantSource
import edu.ie3.datamodel.io.source.csv.CsvThermalSource
import edu.ie3.datamodel.models.input.container.JointGridContainer
import scala.io.Source
import java.io.{BufferedWriter, File, FileWriter}
import scala.jdk.CollectionConverters.IterableHasAsJava
import scala.util.{Failure, Try}
import io.circe.parser._

object IoUtils extends LazyLogging {

  /** Reads a json export of a PowerFactory grid and builds a
    * [[RawPfGridModel]].
    *
    * @param gridFile
    *   path to json export
    * @return
    *   a [[RawPfGridModel]] instance
    */
  def parsePfGrid(gridFile: String): Option[RawPfGridModel] = {
    val source =
      Source.fromFile(gridFile)
    val jsonString =
      try source.mkString
      finally source.close

    decode[RawPfGridModel](jsonString) match {
      case Left(error) =>
        logger.error(error.getMessage())
        None
      case Right(decodingResult) =>
        Some(decodingResult)
    }
  }

  @deprecated("should be implemented within PSDM")
  def parsePsdmGrid(
      gridName: String,
      csvSep: String,
      folderPath: String,
      namingStrategy: FileNamingStrategy
  ): JointGridContainer = {

    /* Instantiating sources */
    val typeSource = new CsvTypeSource(csvSep, folderPath, namingStrategy)
    val rawGridSource =
      new CsvRawGridSource(csvSep, folderPath, namingStrategy, typeSource)

    val thermalSource =
      new CsvThermalSource(csvSep, folderPath, namingStrategy, typeSource)
    val systemParticipantSource = new CsvSystemParticipantSource(
      csvSep,
      folderPath,
      namingStrategy,
      typeSource,
      thermalSource,
      rawGridSource
    )
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
    val systemParticipants =
      systemParticipantSource.getSystemParticipants.orElseThrow(() =>
        new SourceException("Error during reading of system participant data.")
      )
    val graphicElements = graphicsSource.getGraphicElements.orElseThrow(() =>
      new SourceException("Error during reading of graphic elements.")
    )

    new JointGridContainer(
      gridName,
      rawGridElements,
      systemParticipants,
      graphicElements
    )
  }

  /** Credits to isomarcte:
    * https://stackoverflow.com/questions/52666231/how-to-write-to-a-csv-file-in-scala
    */
  def writeCsvFile(
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
