/**
 * © 2021. Johannes Hiry,
 **/

package edu.ie3.powerFactory2psdm.config.validate.conversion

import edu.ie3.powerFactory2psdm.config.model.IndividualModelConfig.ModelConversionMode

trait ConversionModeValidator[T <: ModelConversionMode] {
  def validate(conversionMode: T): Unit
}
