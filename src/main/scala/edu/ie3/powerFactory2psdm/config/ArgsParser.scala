/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.config

import com.typesafe.config.{ConfigFactory, Config => TypesafeConfig}
import scopt.{OptionParser => scoptOptionParser}

import java.io.File
import java.nio.file.Paths

object ArgsParser {
  // case class for allowed arguments
  final case class Arguments(
      configLocation: Option[String] = None,
      config: Option[TypesafeConfig] = None
  )

  def parse(args: Array[String]): Option[Arguments] = parse(buildParser, args)

  private def parse(
      parser: scoptOptionParser[Arguments],
      args: Array[String]
  ): Option[Arguments] = {
    parser.parse(args, init = Arguments())
  }

  private def parseTypesafeConfig(fileName: String): TypesafeConfig = {
    val file = Paths.get(fileName).toFile
    if (!file.exists())
      throw new Exception(s"Missing config file on path $fileName")
    parseTypesafeConfig(file)
  }

  private def parseTypesafeConfig(file: File): TypesafeConfig = {
    ConfigFactory.parseFile(file)
  }

  // build the config parser using scopt library
  private def buildParser: scoptOptionParser[Arguments] = {
    new scoptOptionParser[Arguments]("pf2psdm") {
      opt[String]("config")
        .action((value, args) => {
          args.copy(
            config = Some(parseTypesafeConfig(value)),
            configLocation = Option(value)
          )
        })
        .validate(
          value =>
            if (value.trim.isEmpty) failure("config location cannot be empty")
            else success
        )
        .validate(
          value =>
            if (value.contains("\\"))
              failure("wrong config path, expected: /, found: \\")
            else success
        )
        .text("Location of the pf2psdm config file")
        .minOccurs(1)
    }
  }
}