/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.config.model

import edu.ie3.powerFactory2psdm.config.ConversionConfigUtils.QCharacteristic
import edu.ie3.powerFactory2psdm.config.model.BsConversionConfig.{
  BsModelConversionMode,
  IndividualBsConfig
}
import edu.ie3.powerFactory2psdm.config.model.IndividualModelConfig.ModelConversionMode
import edu.ie3.powerFactory2psdm.generator.ParameterSamplingMethod

final case class BsConversionConfig(
    conversionMode: BsModelConversionMode,
    individualConfigs: Option[List[IndividualBsConfig]]
) extends DefaultModelConfig

object BsConversionConfig {
  final case class IndividualBsConfig(
      ids: Set[String],
      conversionMode: BsModelConversionMode
  ) extends IndividualModelConfig

  sealed trait BsModelConversionMode extends ModelConversionMode

  final case class BsFixedFeedIn(qCharacteristic: QCharacteristic)
      extends BsModelConversionMode

  /** Battery storage model generation parameter sampling configuration
    *
    * @param capex
    *   – capital expense for this type of Storage (typically in €)
    * @param opex
    *   – operating expense for this type of Storage (typically in €/MWh)
    * @param eStorage
    *   – stored energy capacity in kWh
    * @param pMax
    *   – maximum permissible active power of the integrated inverter in kW
    * @param activePowerGradient
    *   – maximum permissible gradient of active power change in %/h
    * @param eta
    *   – efficiency of the charging and discharging process in percent
    * @param dod
    *   – maximum permissible depth of discharge in percent
    * @param lifeTime
    *   – maximum life time of the storage in h
    * @param lifeCycle
    *   – maximum amount of full charging/discharging cycles
    * @param qCharacteristic
    *   – Description of a reactive power characteristic for integrated inverter
    *   type
    */
  final case class BsModelGeneration(
      capex: ParameterSamplingMethod,
      opex: ParameterSamplingMethod,
      eStorage: ParameterSamplingMethod,
      pMax: ParameterSamplingMethod,
      activePowerGradient: ParameterSamplingMethod,
      eta: ParameterSamplingMethod,
      dod: ParameterSamplingMethod,
      lifeTime: ParameterSamplingMethod,
      lifeCycle: ParameterSamplingMethod,
      qCharacteristic: QCharacteristic
  ) extends BsModelConversionMode

}
