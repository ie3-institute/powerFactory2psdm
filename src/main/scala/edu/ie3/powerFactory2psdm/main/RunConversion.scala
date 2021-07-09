/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.main

import com.typesafe.scalalogging.LazyLogging
import edu.ie3.powerFactory2psdm.config.{ConfigValidator, ConversionConfig}
import edu.ie3.powerFactory2psdm.converter.GridConverter
import edu.ie3.powerFactory2psdm.io.PfGridParser

import java.io.File
import edu.ie3.powerFactory2psdm.exception.io.GridParsingException
import edu.ie3.powerFactory2psdm.model.powerfactory.RawGridModel

object RunConversion extends ConversionHelper with LazyLogging {

  def main(args: Array[String]): Unit = {

    logger.info("Parsing the config")
    val (_, config) = prepareConfig(args)
    val conversionConfig = ConversionConfig(config)
    ConfigValidator.checkValidity(conversionConfig)
    val exportedGridFile =
      s"${new File(".").getCanonicalPath}/src/main/python/pfGridExport/pfGrid.json"
    logger.info("Parsing the json grid file.")
    val pfGrid: RawGridModel = PfGridParser
      .parse(exportedGridFile)
      .getOrElse(
        throw GridParsingException("Parsing the Json grid file failed")
      )
    val psdmGrid = GridConverter.convert(pfGrid)
  }
}
