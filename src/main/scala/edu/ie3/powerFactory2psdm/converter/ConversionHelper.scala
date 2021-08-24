/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter

import edu.ie3.datamodel.models.input.system.characteristic.ReactivePowerCharacteristic
import edu.ie3.powerFactory2psdm.config.ConversionConfig.{
  DependentQCharacteristic,
  FixedQCharacteristic,
  QCharacteristic
}
import edu.ie3.powerFactory2psdm.exception.pf.ElementConfigurationException
import edu.ie3.powerFactory2psdm.model.entity.StaticGenerator

import scala.util.{Failure, Success, Try}

object ConversionHelper {

  def determineReactivePowerCharacteristic(
      characteristic: QCharacteristic,
      cosPhiRated: Double
  ): ReactivePowerCharacteristic = characteristic match {
    case FixedQCharacteristic =>
      ReactivePowerCharacteristic.parse(
        s"cosPhiFixed:{(0.0, $cosPhiRated)}"
      )
    case DependentQCharacteristic(characteristic) =>
      ReactivePowerCharacteristic.parse(characteristic)
  }

  def determineCosPhiRated(input: StaticGenerator): Double =
    determineCosPhiRated(input.indCapFlag, input.cosPhi) match {
      case Success(value) => value
      case Failure(exc) =>
        throw ElementConfigurationException(
          s"Can't determine cos phi rated for static generator: ${input.id}. Exception: ${exc.getMessage}"
        )
    }

  def determineCosPhiRated(indCapFlag: Int, cosPhi: Double): Try[Double] =
    indCapFlag match {
      case 0 => Success(cosPhi)
      case 1 => Success(-cosPhi)
      case _ =>
        Failure(
          ElementConfigurationException(
            s"The inductive capacitive specifier should be either 0 (inductive) or 1 (capacitive)"
          )
        )
    }

}
