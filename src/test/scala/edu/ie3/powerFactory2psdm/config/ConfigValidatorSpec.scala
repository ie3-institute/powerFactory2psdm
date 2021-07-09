package edu.ie3.powerFactory2psdm.config

import edu.ie3.powerFactory2psdm.common.ConfigTestData
import edu.ie3.powerFactory2psdm.exception.io.ConversionConfigException
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class ConfigValidatorSpec extends Matchers with ConfigTestData with AnyWordSpecLike{

  "A config validator" should {
    "recognise a correct config" in {
      ConfigValidator.checkValidity(validConfig)
    }

    "thrown an exception if the albedo factor is invalid" in {
      val exc = intercept[ConversionConfigException](ConfigValidator.checkPvValidity(pvParamsConfig.copy(albedo = 3)))
      exc.getMessage shouldBe "Faulty config: The albedo factor should be between 0 and 1"
    }
  }

}
