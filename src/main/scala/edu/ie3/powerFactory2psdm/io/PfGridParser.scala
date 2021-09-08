/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.io

import com.typesafe.scalalogging.LazyLogging
import edu.ie3.powerFactory2psdm.model.RawPfGridModel
import io.circe.parser.decode
import io.circe.generic.auto._
import io.circe.parser._

import scala.io.Source
import java.io.File

object PfGridParser extends LazyLogging {

  def parse(gridFile: String): Option[RawPfGridModel] = {
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
}
