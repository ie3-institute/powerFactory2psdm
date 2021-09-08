/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.model.setting

/** Enumeration of the different unit system options within the project
  * settings.
  */
object UnitSystem extends Enumeration {
  val metric = 0
  val englishTransportation = 1d
  val englishIndustry = 2d
}
