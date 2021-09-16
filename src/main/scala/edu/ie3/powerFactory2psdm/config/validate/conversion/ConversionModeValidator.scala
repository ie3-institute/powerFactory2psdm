/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.config.validate.conversion

import edu.ie3.powerFactory2psdm.config.model.IndividualModelConfig.ModelConversionMode

trait ConversionModeValidator[T <: ModelConversionMode] {
  def validate(conversionMode: T): Unit
}
