package edu.ie3.powerFactory2psdm.io

import com.typesafe.scalalogging.LazyLogging
import edu.ie3.powerFactory2psdm.model.powerfactory.PowerFactoryGrid
import io.circe.parser.decode
import io.circe.generic.auto._
import io.circe.parser._
import scala.io.Source
import java.io.File


object PfGridParser extends LazyLogging {
  val exportedGridFile = s"${new File(".").getCanonicalPath}/src/main/python/pfGridExport/pfGrid.json"

  def parse(gridFile: String = exportedGridFile): Option[PowerFactoryGrid] = {

    val source =
      Source.fromFile(gridFile)
    val jsonString = try source.mkString
    finally source.close

    decode[PowerFactoryGrid](jsonString) match {
      case Left(error) =>
        logger.error(error.getMessage())
        None
      case Right(decodingResult) =>
        Some(decodingResult)
    }
  }
}
