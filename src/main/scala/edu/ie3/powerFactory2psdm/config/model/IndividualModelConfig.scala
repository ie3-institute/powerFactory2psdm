/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.config.model

import edu.ie3.powerFactory2psdm.config.model.IndividualModelConfig.ModelConversionMode

trait IndividualModelConfig[M <: ModelConversionMode] {
  val ids: Set[String]
  val conversionMode: M
}

object IndividualModelConfig {

  /** Trait that groups the options for conversion of a model
    */
  trait ModelConversionMode

}
