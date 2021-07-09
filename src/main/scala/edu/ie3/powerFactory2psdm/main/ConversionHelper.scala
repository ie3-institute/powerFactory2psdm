/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.main

import edu.ie3.powerFactory2psdm.config.ArgsParser
import edu.ie3.powerFactory2psdm.config.ArgsParser.Arguments
import com.typesafe.config.{Config => TypesafeConfig}
import edu.ie3.powerFactory2psdm.exception.io.ConversionConfigException

trait ConversionHelper {

  def prepareConfig(args: Array[String]): (Arguments, TypesafeConfig) = {

    val parsedArgs = ArgsParser.parse(args) match {
      case Some(pArgs) => pArgs
      case None =>
        System.exit(-1)
        throw new IllegalArgumentException(
          "Unable to parse provided Arguments."
        )
    }

    val parsedArgsConfig = parsedArgs.config match {
      case None =>
        throw ConversionConfigException(
          "Please provide a valid config file via --config <path-to-config-file>."
        )
      case Some(parsedArgsConfig) => parsedArgsConfig
    }
    (parsedArgs, parsedArgsConfig)
  }

}
