package edu.ie3.powerFactory2psdm.main

import com.typesafe.scalalogging.LazyLogging
import edu.ie3.powerFactory2psdm.io.PfGridParser
import edu.ie3.powerFactory2psdm.model.powerfactory.PowerFactoryGrid

object RunConversion extends LazyLogging {

  def main(args: Array[String]): Unit ={
    logger.info("Parsing the json grid file.")
    val pfGrid = PfGridParser.parse().getOrElse(throw new RuntimeException("Parsing the Json grid file failed"))
  }
}
