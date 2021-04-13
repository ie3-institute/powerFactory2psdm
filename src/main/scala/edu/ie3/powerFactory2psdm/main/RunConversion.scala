package edu.ie3.powerFactory2psdm.main

import com.typesafe.scalalogging.LazyLogging
import edu.ie3.powerFactory2psdm.io.PfGridParser
import java.io.File
import edu.ie3.powerFactory2psdm.model.powerfactory.PowerFactoryGrid


object RunConversion extends LazyLogging {

  def main(args: Array[String]): Unit ={
    val exportedGridFile = s"${new File(".").getCanonicalPath}/src/main/python/pfGridExport/pfGrid.json"
    logger.info("Parsing the json grid file.")
    val pfGrid: PowerFactoryGrid = PfGridParser
      .parse(exportedGridFile)
      .getOrElse(throw new RuntimeException("Parsing the Json grid file failed"))
  }
}
