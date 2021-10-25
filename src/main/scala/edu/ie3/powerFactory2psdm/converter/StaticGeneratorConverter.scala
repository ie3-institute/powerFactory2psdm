package edu.ie3.powerFactory2psdm.converter

import edu.ie3.datamodel.models.input.system.{FixedFeedInInput, PvInput, WecInput}
import edu.ie3.powerFactory2psdm.config.ConversionConfig.StatGenModelConfigs
import edu.ie3.powerFactory2psdm.model.entity.StaticGenerator

object StaticGeneratorConverter {

  /***
   * Container class to house the different PSDM models resulting from the conversion of the [[StaticGenerator]]s
   *
   * @param fixedFeedIns fixed feed ins
   * @param pvInputs pv models
   * @param wecInputs wec models
   */
  final case class StatGenModelContainer(
     fixedFeedIns: List[FixedFeedInInput],
     pvInputs: List[PvInput],
     wecInputs: List[WecInput]
   )

  def convert(input: List[StaticGenerator], conversionConfig: StatGenModelConfigs): StatGenModelContainer = {
    input.map(statGen => statGen.category match {
      case ""
    })
  }

}
