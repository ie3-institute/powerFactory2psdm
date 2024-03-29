/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.model

import edu.ie3.powerFactory2psdm.exception.pf.{
  ConversionException,
  MissingParameterException
}
import edu.ie3.powerFactory2psdm.model.RawPfGridModel.ProjectSettings
import edu.ie3.powerFactory2psdm.model.setting.ConversionPrefixes
import edu.ie3.powerFactory2psdm.model.setting.ConversionPrefixes.ConversionPrefix
import org.scalatest.PrivateMethodTester
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class PreprocessedPfGridModelSpec
    extends Matchers
    with AnyWordSpecLike
    with PrivateMethodTester {

  "A GridModel" should {
    val settings = ProjectSettings(Some(0), Some("k"), Some("k"))

    "extract project settings" in {
      val extractProjectSettings =
        PrivateMethod[ProjectSettings](Symbol("extractProjectSettings"))
      val extractedSettings =
        PreprocessedPfGridModel invokePrivate extractProjectSettings(
          Some(List(settings))
        )
      extractedSettings shouldBe settings
    }

    "throw an exception if there are multiple project settings" in {
      val extractProjectSettings =
        PrivateMethod[ProjectSettings](Symbol("extractProjectSettings"))
      val exc = intercept[ConversionException](
        PreprocessedPfGridModel invokePrivate extractProjectSettings(
          Some(List(settings, settings))
        )
      )
      exc.getMessage shouldBe "There are multiple project settings defined."
    }

    "throw an exception if there are no project settings" in {
      val extractProjectSettings =
        PrivateMethod[ProjectSettings](Symbol("extractProjectSettings"))
      val exc = intercept[ConversionException](
        PreprocessedPfGridModel invokePrivate extractProjectSettings(
          None
        )
      )
      exc.getMessage shouldBe "There are no project settings defined."
    }

    "build conversion prefixes" in {
      val buildConversionPrefixes =
        PrivateMethod[ConversionPrefixes](Symbol("buildConversionPrefixes"))
      val conversionPrefixes =
        PreprocessedPfGridModel invokePrivate buildConversionPrefixes(
          settings
        )
      conversionPrefixes.loadPQSPrefix shouldBe ConversionPrefix(1e3)
      conversionPrefixes.lineLengthPrefix shouldBe ConversionPrefix(1e3)
    }

    "throw an exception when the pqsPrefix is missing" in {
      val buildConversionPrefixes =
        PrivateMethod[ConversionPrefixes](Symbol("buildConversionPrefixes"))
      val exc = intercept[MissingParameterException](
        PreprocessedPfGridModel invokePrivate buildConversionPrefixes(
          settings.copy(prefixPQS = None)
        )
      )
      exc.getMessage shouldBe "The projects settings miss the prefix specification for active/reactive/apparent power values"
    }

    "throw an exception when the prefixLength is missing" in {
      val buildConversionPrefixes =
        PrivateMethod[ConversionPrefixes](Symbol("buildConversionPrefixes"))
      val exc = intercept[MissingParameterException](
        PreprocessedPfGridModel invokePrivate buildConversionPrefixes(
          settings.copy(prefixLength = None)
        )
      )
      exc.getMessage shouldBe "The project settings miss the prefix specification for line length."
    }

  }

}
