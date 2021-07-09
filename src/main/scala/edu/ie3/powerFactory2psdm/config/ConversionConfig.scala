/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.config

final case class ConversionConfig(
    conversionMode: ConversionConfig.ConversionMode,
    model: ConversionConfig.Model
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
        fixedFeedIns = $_reqBln(parentPath, c, "fixedFeedIns", $tsCfgValidator)
      )
    }
    private def $_reqBln(
        parentPath: java.lang.String,
        c: com.typesafe.config.Config,
        path: java.lang.String,
        $tsCfgValidator: $TsCfgValidator
    ): scala.Boolean = {
      if (c == null) false
      else
        try c.getBoolean(path)
        catch {
          case e: com.typesafe.config.ConfigException =>
            $tsCfgValidator.addBadPath(parentPath + path, e)
            false
        }
    }

  }

  final case class Model(
      defaultParams: ConversionConfig.Model.DefaultParams
  )
  object Model {
    final case class DefaultParams(
        pv: ConversionConfig.Model.DefaultParams.Pv
    )
    object DefaultParams {
      final case class Pv(
          albedo: scala.Double
      )
      object Pv {
        def apply(
            c: com.typesafe.config.Config,
            parentPath: java.lang.String,
            $tsCfgValidator: $TsCfgValidator
        ): ConversionConfig.Model.DefaultParams.Pv = {
          ConversionConfig.Model.DefaultParams.Pv(
            albedo = $_reqDbl(parentPath, c, "albedo", $tsCfgValidator)
          )
        }
        private def $_reqDbl(
            parentPath: java.lang.String,
            c: com.typesafe.config.Config,
            path: java.lang.String,
            $tsCfgValidator: $TsCfgValidator
        ): scala.Double = {
          if (c == null) 0
          else
            try c.getDouble(path)
            catch {
              case e: com.typesafe.config.ConfigException =>
                $tsCfgValidator.addBadPath(parentPath + path, e)
                0
            }
        }

      }

      def apply(
          c: com.typesafe.config.Config,
          parentPath: java.lang.String,
          $tsCfgValidator: $TsCfgValidator
      ): ConversionConfig.Model.DefaultParams = {
        ConversionConfig.Model.DefaultParams(
          pv = ConversionConfig.Model.DefaultParams.Pv(
            if (c.hasPathOrNull("pv")) c.getConfig("pv")
            else com.typesafe.config.ConfigFactory.parseString("pv{}"),
            parentPath + "pv.",
            $tsCfgValidator
          )
        )
      }
    }

    def apply(
        c: com.typesafe.config.Config,
        parentPath: java.lang.String,
        $tsCfgValidator: $TsCfgValidator
    ): ConversionConfig.Model = {
      ConversionConfig.Model(
        defaultParams = ConversionConfig.Model.DefaultParams(
          if (c.hasPathOrNull("defaultParams")) c.getConfig("defaultParams")
          else com.typesafe.config.ConfigFactory.parseString("defaultParams{}"),
          parentPath + "defaultParams.",
          $tsCfgValidator
        )
      )
    }
  }

  def apply(c: com.typesafe.config.Config): ConversionConfig = {
    val $tsCfgValidator: $TsCfgValidator = new $TsCfgValidator()
    val parentPath: java.lang.String = ""
    val $result = ConversionConfig(
      conversionMode = ConversionConfig.ConversionMode(
        if (c.hasPathOrNull("conversionMode")) c.getConfig("conversionMode")
        else com.typesafe.config.ConfigFactory.parseString("conversionMode{}"),
        parentPath + "conversionMode.",
        $tsCfgValidator
      ),
      model = ConversionConfig.Model(
        if (c.hasPathOrNull("model")) c.getConfig("model")
        else com.typesafe.config.ConfigFactory.parseString("model{}"),
        parentPath + "model.",
        $tsCfgValidator
      )
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
