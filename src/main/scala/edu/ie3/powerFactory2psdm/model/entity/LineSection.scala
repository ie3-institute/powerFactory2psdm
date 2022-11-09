/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.model.entity

import edu.ie3.powerFactory2psdm.exception.pf.{
  ConversionException,
  MissingParameterException
}
import edu.ie3.powerFactory2psdm.model.RawPfGridModel.LineSections
import org.apache.commons.lang3.Conversion

/** A section of an electrical [[Line]]
  *
  * @param id
  *   its identifier
  * @param length
  *   its length
  * @param typeId
  *   the identifier of its type
  */
final case class LineSection(
    id: String,
    length: Double,
    typeId: String
) extends EntityModel

object LineSection {

  def build(rawLineSection: LineSections): LineSection = {
    val id = rawLineSection.id.getOrElse(
      throw MissingParameterException(
        s"There is no id for line section $rawLineSection"
      )
    )
    val length = rawLineSection.dline.getOrElse(
      throw MissingParameterException(
        s"There is no defined length for line section $id"
      )
    )
    val typeId = rawLineSection.typeId.getOrElse(
      throw MissingParameterException(
        s"There is no defined type for line section $id"
      )
    )
    LineSection(id, length, typeId)
  }

  def buildLineSectionMap(
      rawLineSections: List[LineSections]
  ): Map[String, List[LineSection]] = {
    val lineSections = rawLineSections.map(build)
    lineSections.groupBy(lineSection => getLineId(lineSection.id))
  }

  private def getLineId(lineSectionId: String): String = {
    val lineIdRegEx = raw".*(?=\\[^\\]*\.ElmLnesec)".r
    lineIdRegEx.findFirstIn(lineSectionId) match {
      case Some(id) => id
      case None =>
        throw ConversionException(
          s"Can't extract line id from line section: $lineSectionId"
        )
    }
  }
}
