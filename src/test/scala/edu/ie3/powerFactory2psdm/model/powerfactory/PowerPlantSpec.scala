package edu.ie3.powerFactory2psdm.model.powerfactory

import edu.ie3.powerFactory2psdm.exception.pf.MissingParameterException
import edu.ie3.powerFactory2psdm.model.powerfactory.RawGridModel.PowerPlants
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class PowerPlantSpec extends Matchers with AnyWordSpecLike{

  "A power plant" should {

    val id = "somePowerPlant"
    val input = PowerPlants(id = Some(id), sgini = Some(10.123), cosgini = Some(0.9114), pf_recap = Some(0), bus1Id = Some("someNode") )

    "throw an exception if the id is missing" in {
      val faulty = input.copy(id = None)
      val exc = intercept[MissingParameterException](PowerPlant.build(faulty))
      exc.getMessage shouldBe s"There is no id for PowerPlant: $faulty"
    }

    "throw an exception if the node id is missing" in {
      val faulty = input.copy(bus1Id = None)
      val exc = intercept[MissingParameterException](PowerPlant.build(faulty))
      exc.getMessage shouldBe s"PowerPlant: $id has no defined node it's connected to"
    }

    "throw an exception if the apparent power is missing" in {
      val faulty = input.copy(sgini = None)
      val exc = intercept[MissingParameterException](PowerPlant.build(faulty))
      exc.getMessage shouldBe s"PowerPlant: $id has no defined apparent power"
    }

    "throw an exception if the cos phi is missing" in {
      val faulty = input.copy(cosgini = None)
      val exc = intercept[MissingParameterException](PowerPlant.build(faulty))
      exc.getMessage shouldBe s"PowerPlant: $id has no defined power factor"
    }

    "throw an exception if the power factor specifier is missing" in {
      val faulty = input.copy(pf_recap = None)
      val exc = intercept[MissingParameterException](PowerPlant.build(faulty))
      exc.getMessage shouldBe s"PowerPlant: $id has no defined leading/lagging power factor specifier"
    }

  }

}
