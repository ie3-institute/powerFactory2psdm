/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.model.entity

import edu.ie3.powerFactory2psdm.exception.pf.{
  ElementConfigurationException,
  MissingParameterException
}
import edu.ie3.powerFactory2psdm.model.RawPfGridModel.{Loads, LoadsLV, LoadsMV}
import edu.ie3.powerFactory2psdm.model.setting.ConversionPrefixes.ConversionPrefix
import org.apache.logging.log4j.core.config.ConfigurationException

import scala.util.{Failure, Success, Try}

/** Electrical load
  *
  * @param id
  *   identifier
  * @param s
  *   apparent power in VA
  * @param cosphi
  *   cosinus phi value
  */
final case class Load(
    id: String,
    nodeId: String,
    s: Double,
    cosphi: Double,
    indCapFlag: Int,
    isScaled: Boolean,
    scalingFactor: Option[Double]
) extends EntityModel

object Load {

  def build(input: Loads, conversionPrefix: ConversionPrefix): Load = {
    val id = input.id.getOrElse(
      throw MissingParameterException(s"Load $input has no defined id.")
    )
    val nodeId = input.busId.getOrElse(
      throw MissingParameterException(s"Load $id has no defined bus")
    )
    val s = input.slini.getOrElse(
      throw MissingParameterException(s"Load $id has no defined apparent power")
    ) * conversionPrefix.value
    val cosphi = input.coslini.getOrElse(
      throw MissingParameterException(s"Load $id has no defined cosinus phi")
    )
    val indCap = input.pf_recap
      .getOrElse(
        throw MissingParameterException(
          s"Load $id has no defined inductive/capacitive specifier"
        )
      )
      .toInt
    getIsScaled(input.i_scale) match {
      case Success(isScaled) =>
        Load(id, nodeId, s, cosphi, indCap, isScaled, input.scale0)
      case Failure(exc) =>
        throw new ConfigurationException(
          s"Could not determine whether load $id is scaled.",
          exc
        )
    }
  }

  def build(input: LoadsLV): Load = {
    val id = input.id.getOrElse(
      throw MissingParameterException(s"LV Load $input has no defined id.")
    )
    val nodeId = input.busId.getOrElse(
      throw MissingParameterException(s"LV Load $id has no defined bus")
    )
    // for some weird reason the unit prefix adjustable via the project setting within PowerFactory only applies to
    // general loads and mv loads for lv loads the unit is always kVA hence we scale it to VA via 1e3
    val s = input.slini.getOrElse(
      throw MissingParameterException(
        s"LV Load $id has no defined apparent power"
      )
    ) * 1e3
    val cosphi = input.coslini.getOrElse(
      throw MissingParameterException(s"LV Load $id has no defined cosinus phi")
    )
    val indCap = input.pf_recap
      .getOrElse(
        throw MissingParameterException(
          s"LV Load $id has no defined inductive/capacitive specifier"
        )
      )
      .toInt
    getIsScaled(input.i_scale) match {
      case Success(isScaled) =>
        Load(id, nodeId, s, cosphi, indCap, isScaled, input.scale0)
      case Failure(exc) =>
        throw new ConfigurationException(
          s"Could not determine whether lv load $id is scaled.",
          exc
        )

    }
  }

  def build(input: LoadsMV, conversionPrefix: ConversionPrefix): Load = {
    val id = input.id.getOrElse(
      throw MissingParameterException(s"MV Load $input has no defined id.")
    )
    val nodeId = input.busId.getOrElse(
      throw MissingParameterException(s"MV Load $id has no defined bus")
    )
    val s = input.slini.getOrElse(
      throw MissingParameterException(
        s"MV Load $id has no defined apparent power"
      )
    ) * conversionPrefix.value
    val cosphi = input.coslini.getOrElse(
      throw MissingParameterException(s"MV Load $id has no defined cosinus phi")
    )
    val indCap = input.pf_recap
      .getOrElse(
        throw MissingParameterException(
          s"MV Load $id has no defined inductive/capacitive specifier"
        )
      )
      .toInt
    getIsScaled(input.i_scale) match {
      case Success(isScaled) =>
        Load(id, nodeId, s, cosphi, indCap, isScaled, input.scale0)
      case Failure(exc) =>
        throw new ConfigurationException(
          s"Could not determine whether mv load $id is scaled.",
          exc
        )
    }
  }

  def getIsScaled(maybeIsScaled: Option[Double]): Try[Boolean] =
    maybeIsScaled.map(_.toInt) match {
      case Some(x) if x == 0 => Success(false)
      case Some(x) if x == 1 => Success(true)
      case Some(x) =>
        Failure(
          ElementConfigurationException(
            s"The isScaled specifier: $x should be either 0 or 1."
          )
        )
      case None =>
        Failure(MissingParameterException("The isScaled specifier is missing."))
    }
}
