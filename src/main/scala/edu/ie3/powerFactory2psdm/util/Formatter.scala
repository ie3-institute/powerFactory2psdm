/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.util

import java.nio.file.{Files, Paths}
import org.scalafmt.interfaces.Scalafmt

object Formatter {

  def format(str: String, fmtPath: Option[String]): String = {
    val scalafmt = Scalafmt.create(this.getClass.getClassLoader)
    val defaultConfigPath = Paths.get(".scalafmt.conf")
    val defaultConfig =
      if (Files.exists(defaultConfigPath)) defaultConfigPath else Paths.get("")
    val config = fmtPath.fold(defaultConfig)(Paths.get(_))
    val result = scalafmt
      .withRespectVersion(false)
      .format(config, Paths.get("Nil.scala"), str)
    scalafmt.clear()
    result
  }

}
