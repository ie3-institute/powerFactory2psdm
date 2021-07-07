package edu.ie3.powerFactory2psdm.model.powerfactory

import edu.ie3.powerFactory2psdm.exception.pf.MissingParameterException
import edu.ie3.powerFactory2psdm.model.powerfactory.RawGridModel.PowerPlants

final case class PowerPlant(id: String, s: Double, cosPhi: Double, indCap: Double) extends EntityModel

object PowerPlant {
  def build(rawPowerPlant: PowerPlants): PowerPlant = {
    val id = rawPowerPlant.id.getOrElse(
      throw MissingParameterException(s"There is no id for PowerPlant: $rawPowerPlant")
    )
    val s = rawPowerPlant.sgini.getOrElse(
      throw MissingParameterException(s"PowerPlant: $id has no defined apparent power")
    )
    val cosPhi = rawPowerPlant.cosgini.getOrElse(
      throw MissingParameterException(s"PowerPlant: $id has no defined power factor")
    )
    val indCap = rawPowerPlant.pf_recap.getOrElse(
      throw MissingParameterException(s"PowerPlant: $id has no defined leading/lagging power factor specifier")
    )
    PowerPlant(id, s, cosPhi, indCap)
  }
}
