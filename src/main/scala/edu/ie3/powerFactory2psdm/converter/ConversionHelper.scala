/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter

import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.input.system.characteristic.ReactivePowerCharacteristic
import edu.ie3.powerFactory2psdm.config.ConversionConfigUtils.{
  DependentQCharacteristic,
  FixedQCharacteristic,
  QCharacteristic
}
import edu.ie3.powerFactory2psdm.exception.pf.ElementConfigurationException
import edu.ie3.powerFactory2psdm.model.entity.StaticGenerator
import edu.ie3.util.quantities.PowerSystemUnits
import tech.units.indriya.ComparableQuantity

import javax.measure.quantity.Power
import scala.util.{Failure, Success, Try}

/** Utility object to hold utility functions for model converison
  */
object ConversionHelper {

  /** Converts a configured q characteristic to a PSDM conform reactive power
    * characteristic
    *
    * @param characteristic
    *   the q characteristic to be followed
    * @param cosPhiRated
    *   the cosinus phi value of the model
    * @return
    *   the PSDM reactive power characteristic
    */
  def convertQCharacteristic(
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

  /** Specified cosinus phi with respect to the grid regulations (VDE-AR-N 4105)
    *
    * @param sRated
    *   rated power of the infeed system
    * @return
    *   cosinus phi for the infeed system
    */
  def lvGenerationCosPhi(sRated: ComparableQuantity[Power]): Double = {
    val power =
      sRated.to(PowerSystemUnits.KILOVOLTAMPERE).getValue.doubleValue()
    if (power <= 3.86)
      1d
    else if ((3.86 < power) && (power <= 13.8))
      0.95
    else
      0.9
  }

  /** Determines the cos phi rated of a static generator
    *
    * @param input
    *   the static generator
    * @return
    *   the cosinus phi rated value
    */
  def determineCosPhiRated(input: StaticGenerator): Double =
    determineCosPhiRated(input.indCapFlag, input.cosPhi) match {
      case Success(value) => value
      case Failure(exc) =>
        throw ElementConfigurationException(
          s"Can't determine cos phi rated for static generator: ${input.id}. Exception: ${exc.getMessage}"
        )
    }

  /** Determines the cos phi depending on an inductive or capacitive specifier
    *
    * @param indCapFlag
    *   specifies inductive (0) or capacitive (1) behaviour
    * @param cosPhi
    *   cos phi value
    * @return
    *   either cos phi or an exception
    */
  def determineCosPhiRated(indCapFlag: Int, cosPhi: Double): Try[Double] =
    indCapFlag match {
      case 0 => Success(cosPhi)
      case 1 => Success(-cosPhi)
      case _ =>
        Failure(
          ElementConfigurationException(
            "The inductive capacitive specifier should be either 0 (inductive) or 1 (capacitive)"
          )
        )
    }

  /** Get all duplicates of the sequence.
    *
    * @param items
    *   items to check for duplicates
    * @tparam T
    *   type of the items
    * @return
    *   sequence of duplicates
    */
  def getDuplicates[T](items: Seq[T]): Seq[T] = {
    val uniqueItems = items.distinct
    if (uniqueItems.size == items.size) {
      return Seq.empty[T]
    }
    items.diff(uniqueItems)
  }

}
