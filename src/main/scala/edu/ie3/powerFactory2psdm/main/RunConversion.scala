/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.main

import com.typesafe.scalalogging.LazyLogging
import edu.ie3.powerFactory2psdm.converter.GridConverter
import edu.ie3.powerFactory2psdm.io.PfGridParser

import java.io.File
import edu.ie3.powerFactory2psdm.exception.io.GridParsingException
import edu.ie3.powerFactory2psdm.model.powerfactory.PowerFactoryGrid
import org.jgrapht.alg.connectivity.BiconnectivityInspector

object RunConversion extends LazyLogging {

  def main(args: Array[String]): Unit = {
    val exportedGridFile =
      s"${new File(".").getCanonicalPath}/src/main/python/pfGridExport/pfGrid.json"
    logger.info("Parsing the json grid file.")
    val pfGrid: PowerFactoryGrid = PfGridParser
      .parse(exportedGridFile)
      .getOrElse(
        throw GridParsingException("Parsing the Json grid file failed")
      )
    val psdmGrid = GridConverter.convert(pfGrid)
  }
}
