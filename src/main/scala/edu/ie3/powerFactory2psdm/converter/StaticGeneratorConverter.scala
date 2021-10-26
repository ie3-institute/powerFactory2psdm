/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter

import com.typesafe.scalalogging.LazyLogging
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.system.{
  FixedFeedInInput,
  PvInput,
  WecInput
}
import edu.ie3.powerFactory2psdm.config.ConversionConfig.StatGenModelConfigs
import edu.ie3.powerFactory2psdm.config.model.PvConversionConfig.{
  PvFixedFeedIn,
  PvModelGeneration
}
import edu.ie3.powerFactory2psdm.config.model.WecConversionConfig.{
  WecFixedFeedIn,
  WecModelGeneration
}
import edu.ie3.powerFactory2psdm.exception.pf.ConversionException
import edu.ie3.powerFactory2psdm.generator.{PvInputGenerator, WecInputGenerator}
import edu.ie3.powerFactory2psdm.model.entity.StaticGenerator
import edu.ie3.powerFactory2psdm.model.entity.StaticGenerator.StatGenCategories.{
  BATTERY,
  BIOGAS,
  OTHER,
  PV,
  WEC
}

import scala.util.{Failure, Success}

object StaticGeneratorConverter extends LazyLogging {

  /** * Container class to house the different PSDM models resulting from the
    * conversion of the [[StaticGenerator]] s
    *
    * @param fixedFeedIns
    *   fixed feed ins
    * @param pvInputs
    *   pv models
    * @param wecInputs
    *   wec models
    */
  final case class StatGenModelContainer(
      fixedFeedIns: List[FixedFeedInInput],
      pvInputs: List[PvInput],
      wecInputs: List[WecInput]
  )

  def convert(
      input: List[StaticGenerator],
      conversionConfig: StatGenModelConfigs,
      nodes: Map[String, NodeInput]
  ): StatGenModelContainer = {
    val emptyModelContainer
        : (List[FixedFeedInInput], List[PvInput], List[WecInput]) =
      (Nil, Nil, Nil)
    val convertedModels = input.foldLeft(emptyModelContainer) {
      case ((fixed, pv, wec), statGen) =>
        convert(statGen, conversionConfig, nodes) match {
          case x: FixedFeedInInput => (x :: fixed, pv, wec)
          case x: PvInput          => (fixed, x :: pv, wec)
          case x: WecInput         => (fixed, pv, x :: wec)
          case x => {
            logger error s"Got an unexpected model: $x. This will be ignored and not converted!"
            (fixed, pv, wec)
          }
        }
    }
    StatGenModelContainer(
      convertedModels._1,
      convertedModels._2,
      convertedModels._3
    )
  }

  def convert(
      statGen: StaticGenerator,
      conversionConfig: StatGenModelConfigs,
      nodes: Map[String, NodeInput]
  ): Any = {
    val node = NodeConverter.getNode(statGen.busId, nodes) match {
      case Failure(exc) =>
        throw ConversionException(
          s"Can't find converted node of static generator: ${statGen.busId}",
          exc
        )
      case Success(node) => node
    }
    statGen.category match {
      case PV =>
        val maybeIndividualConfig =
          conversionConfig.pvConfig.getIndividualModelConfig(statGen.id)
        val conversionMode = maybeIndividualConfig
          .map(config => config.conversionMode)
          .getOrElse(conversionConfig.pvConfig.conversionMode)
        conversionMode match {
          case fixedFeedIn: PvFixedFeedIn =>
            FixedFeedInConverter.convert(
              statGen,
              node,
              fixedFeedIn.qCharacteristic
            )
          case modelGeneration: PvModelGeneration =>
            PvInputGenerator.generate(statGen, node, modelGeneration)
        }
      case WEC =>
        val maybeIndividualConfig =
          conversionConfig.wecConfig.getIndividualModelConfig(statGen.id)
        val conversionMode = maybeIndividualConfig
          .map(config => config.conversionMode)
          .getOrElse(conversionConfig.wecConfig.conversionMode)
        conversionMode match {
          case fixedFeedIn: WecFixedFeedIn =>
            FixedFeedInConverter.convert(
              statGen,
              node,
              fixedFeedIn.qCharacteristic
            )
          case modelGeneration: WecModelGeneration =>
            WecInputGenerator.generate(statGen, node, modelGeneration)
        }
      case BIOGAS =>
        logger error s"Specific model generation for category: $BIOGAS is currently not supported. Generator ${statGen.id} will not be converted."

      case BATTERY =>
        logger error s"Specific model generation for category: $BATTERY is currently not supported. Generator ${statGen.id} will not be converted."

      case OTHER =>
        logger error s"Specific model generation for category: $OTHER is currently not supported. Generator ${statGen.id} will not be converted."
    }
  }

}
