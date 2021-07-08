/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.config

final case class ConversionConfig(
    )
object ConversionConfig {
  final case class ConversionMode(
      fixedFeedIns: scala.Boolean
  )
  object ConversionMode {
    def apply(
        c: com.typesafe.config.Config,
        parentPath: java.lang.String,
        $tsCfgValidator: $TsCfgValidator
    ): ConversionConfig.ConversionMode = {
      ConversionConfig.ConversionMode(
        fixedFeedIns = !c.hasPathOrNull("fixedFeedIns") || c.getBoolean(
          "fixedFeedIns"
        )
      )
    }
  }

  def apply(c: com.typesafe.config.Config): ConversionConfig = {
    val $tsCfgValidator: $TsCfgValidator = new $TsCfgValidator()
    val parentPath: java.lang.String = ""
    val $result = ConversionConfig(
      )
    $tsCfgValidator.validate()
    $result
  }
  private final class $TsCfgValidator {
    private val badPaths =
      scala.collection.mutable.ArrayBuffer[java.lang.String]()

    def addBadPath(
        path: java.lang.String,
        e: com.typesafe.config.ConfigException
    ): Unit = {
      badPaths += s"'$path': ${e.getClass.getName}(${e.getMessage})"
    }

    def validate(): Unit = {
      if (badPaths.nonEmpty) {
        throw new com.typesafe.config.ConfigException(
          badPaths.mkString("Invalid configuration:\n    ", "\n    ", "")
        ) {}
      }
    }
  }
}
