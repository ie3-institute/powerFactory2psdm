/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.model.powerfactory

import edu.ie3.powerFactory2psdm.exception.io.ConversionConfigException
import edu.ie3.powerFactory2psdm.exception.pf.MissingParameterException
import edu.ie3.powerFactory2psdm.model.powerfactory.RawGridModel.StatGen
import org.apache.logging.log4j.core.config.ConfigurationException

/**
  * A static generator
  *
  * @param id identifier
  * @param busId id of the node it is connected to
  * @param sRated rated apparent power in MVA
  * @param cosPhi power factor
  * @param indCapFlag specifies leading or lagging power factor characteristic
  * @param category category of the static generator
  */
final case class StaticGenerator(
    id: String,
    busId: String,
    sRated: Double,
    cosPhi: Double,
    indCapFlag: Int,
    category: String
) extends EntityModel

object StaticGenerator {

  def build(
      input: StatGen,
      sRatedSource: String,
      cosPhiSource: String
  ): StaticGenerator = {
    val id = input.id.getOrElse(
      throw MissingParameterException(
        s"There is no id for static generator: $input"
      )
    )
    val busId = input.busId.getOrElse(
      throw MissingParameterException(
        s"There is no id of a connected bus for static generator: $id"
      )
    )
    val sRated = sRatedSource match {
      case "basic data" =>
        input.sgn.getOrElse(
          throw MissingParameterException(
            s"There is no rated power [basic data] defined for static generator: $id"
          )
        )
      case "load flow" =>
        input.sgini.getOrElse(
          throw MissingParameterException(
            s"There is no rated power [load flow] defined for static generator: $id"
          )
        )
      case _ => throw paramSourceException
    }
    val cosPhi = cosPhiSource match {
      case "basic data" =>
        input.cosn.getOrElse(
          throw MissingParameterException(
            s"There is no cos phi [basic data] defined for static generator: $id"
          )
        )
      case "load flow" =>
        input.cosgini.getOrElse(
          throw MissingParameterException(
            s"There is no cos phi [load flow] defined for static generator: $id"
          )
        )
      case _ => throw paramSourceException
    }
    val indCapFlag = input.pf_recap
      .getOrElse(
        throw MissingParameterException(
          s"There is no inductive capacitive specifier defined for static generator: $id"
        )
      )
      .toInt
    val category = input.cCategory.getOrElse(
      throw MissingParameterException(
        s"There is no category specifier defined for static generator: $id"
      )
    )

    StaticGenerator(id, busId, sRated, cosPhi, indCapFlag, category)
  }

  val paramSourceException: ConversionConfigException =
    ConversionConfigException(
      "We only differentiate between options \"basic data\" and \"load flow\". Please adjust the StatGenModelConfigs accordingly"
    )

}
