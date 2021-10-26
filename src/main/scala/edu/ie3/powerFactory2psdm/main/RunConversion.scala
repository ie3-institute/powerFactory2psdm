/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.main

import com.typesafe.scalalogging.LazyLogging
import edu.ie3.datamodel.io.csv.DefaultDirectoryHierarchy
import edu.ie3.datamodel.io.naming.{
  DefaultDirectoryHierarchy,
  EntityPersistenceNamingStrategy,
  FileNamingStrategy
}
import edu.ie3.datamodel.io.sink.CsvFileSink
import edu.ie3.powerFactory2psdm.config.ConversionConfig
import edu.ie3.powerFactory2psdm.config.validate.ConfigValidator
import edu.ie3.powerFactory2psdm.converter.GridConverter
import edu.ie3.powerFactory2psdm.io.PfGridParser

import java.io.File
import edu.ie3.powerFactory2psdm.exception.io.GridParsingException
import edu.ie3.powerFactory2psdm.model.RawPfGridModel
import pureconfig.ConfigSource
import pureconfig.generic.auto._

object RunConversion extends LazyLogging {

  def main(args: Array[String]): Unit = {

    logger.info("Parsing the config")
    val config =
      ConfigSource
        .file("src/test/resources/application.conf")
        .at("conversion-config")
        .loadOrThrow[ConversionConfig]
    ConfigValidator.validateConversionConfig(config)
    val exportedGridFile =
      s"${new File(".").getCanonicalPath}/src/main/python/pfGridExport/pfGrid.json"
    logger.info("Parsing the json grid file.")
    val pfGrid: RawPfGridModel = PfGridParser
      .parse(exportedGridFile)
      .getOrElse(
        throw GridParsingException("Parsing the Json grid file failed")
      )
    logger.info("Converting grid to PSDM")
    val jointGridContainer = GridConverter.convert(pfGrid, config)

    val baseTargetDirectory = config.output.targetFolder

    val csvSink = if (config.output.csvConfig.directoryHierarchy) {
      new CsvFileSink(
        baseTargetDirectory,
        new FileNamingStrategy(
          new EntityPersistenceNamingStrategy(),
          new DefaultDirectoryHierarchy(baseTargetDirectory, config.gridName)
        ),
        false,
        config.output.csvConfig.separator
      )
    } else {
      new CsvFileSink(
        baseTargetDirectory,
        new FileNamingStrategy(),
        false,
        config.output.csvConfig.separator
      )
    }
    csvSink.persistJointGrid(jointGridContainer)
  }
}
