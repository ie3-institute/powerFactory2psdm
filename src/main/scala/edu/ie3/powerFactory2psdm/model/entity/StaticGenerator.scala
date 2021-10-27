/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.model.entity

import com.typesafe.scalalogging.LazyLogging
import edu.ie3.powerFactory2psdm.config.ConversionConfigUtils.{
  BasicDataSource,
  LoadFlowSource,
  ParameterSource
}
import edu.ie3.powerFactory2psdm.exception.pf.MissingParameterException
import edu.ie3.powerFactory2psdm.model.RawPfGridModel.StatGen
import edu.ie3.powerFactory2psdm.model.entity.StaticGenerator.StatGenCategories
import edu.ie3.powerFactory2psdm.model.entity.StaticGenerator.StatGenCategories.getCategory

import scala.util.{Failure, Success, Try}

/** A static generator
  *
  * @param id
  *   identifier
  * @param busId
  *   id of the node it is connected to
  * @param sRated
  *   rated apparent power in MVA
  * @param cosPhi
  *   power factor
  * @param indCapFlag
  *   specifies leading or lagging power factor characteristic
  * @param category
  *   category of the static generator
  */
final case class StaticGenerator(
    id: String,
    busId: String,
    sRated: Double,
    cosPhi: Double,
    indCapFlag: Int,
    category: StatGenCategories.Value
) extends EntityModel

object StaticGenerator extends LazyLogging {

  object StatGenCategories extends Enumeration {
    val PV: Value = Value("Fotovoltaik")
    val WEC: Value = Value("Wind")
    val OTHER: Value = Value("Other")

    def getCategory(category: String): Value = {
      Try(withName(category)) match {
        case Failure(_) => {
          logger debug s"The category $category is not explicitly handled. Will assign $OTHER instead. NOTE: These generators " +
            s"will not be converted. "
          OTHER
        }
        case Success(value) => value
      }
    }
  }

  def build(
      input: StatGen,
      sRatedSource: ParameterSource,
      cosPhiSource: ParameterSource
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
      case BasicDataSource =>
        input.sgn.getOrElse(
          throw MissingParameterException(
            s"There is no rated power [basic data] defined for static generator: $id"
          )
        )
      case LoadFlowSource =>
        input.sgini.getOrElse(
          throw MissingParameterException(
            s"There is no rated power [load flow] defined for static generator: $id"
          )
        )
    }
    val cosPhi = cosPhiSource match {
      case BasicDataSource =>
        input.cosn.getOrElse(
          throw MissingParameterException(
            s"There is no cos phi [basic data] defined for static generator: $id"
          )
        )
      case LoadFlowSource =>
        input.cosgini.getOrElse(
          throw MissingParameterException(
            s"There is no cos phi [load flow] defined for static generator: $id"
          )
        )
    }
    val indCapFlag = input.pf_recap
      .getOrElse(
        throw MissingParameterException(
          s"There is no inductive capacitive specifier defined for static generator: $id"
        )
      )
      .toInt
    val category = getCategory(
      input.cCategory.getOrElse(
        throw MissingParameterException(
          s"There is no category specifier defined for static generator: $id"
        )
      )
    )
    StaticGenerator(id, busId, sRated, cosPhi, indCapFlag, category)
  }

}
