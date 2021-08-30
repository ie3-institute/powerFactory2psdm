/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.config.validate.conversion

trait ConversionModeValidator[T] {
  def apply(config: T): Unit
}

object ConversionModeValidator {

  def apply[T](
      conversionMode: T
  )(implicit validator: ConversionModeValidator[T]): Unit = {
    validator.apply(conversionMode)
  }
}
