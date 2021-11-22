/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter

import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.connector.LineInput
import edu.ie3.datamodel.models.input.connector.`type`.LineTypeInput
import edu.ie3.datamodel.models.input.system.characteristic.OlmCharacteristicInput
import edu.ie3.datamodel.utils.GridAndGeoUtils
import edu.ie3.powerFactory2psdm.converter.NodeConverter.getNode
import edu.ie3.powerFactory2psdm.converter.types.LineTypeConverter
import edu.ie3.powerFactory2psdm.converter.types.LineTypeConverter.getLineType
import edu.ie3.powerFactory2psdm.exception.pf.ConversionException
import edu.ie3.powerFactory2psdm.model.entity.Line
import edu.ie3.powerFactory2psdm.model.setting.ConversionPrefixes.ConversionPrefix
import edu.ie3.powerFactory2psdm.util.QuantityUtils.RichQuantityDouble

import java.util.UUID
import scala.util.{Failure, Success}

object LineConverter {

  /** Converts lines to PSDM [[LineInput]]
    *
    * @param lines
    *   the lines to be converted
    * @param lineLengthPrefix
    *   the set prefix for the length of the line
    * @param nodes
    *   a map of node ids to nodes
    * @param lineTypes
    *   a map of line type ids to line types
    * @return
    *   a list of all converted Lines
    */
  def convertLines(
      lines: List[Line],
      lineLengthPrefix: ConversionPrefix,
      nodes: Map[String, NodeInput],
      lineTypes: Map[String, LineTypeInput]
  ): List[LineInput] = {
    lines.map(line => {
      val lineType = (line.typeId, line.lineSections) match {
        case (Some(lineTypeId), None) =>
          getLineType(lineTypeId, lineTypes).getOrElse(
            throw ConversionException(
              s"Could not convert line: $line due to failed line type retrieval."
            )
          )
        case (None, Some(lineSections)) =>
          LineTypeConverter.convert(
            line.id,
            line.length,
            lineSections,
            lineTypes
          )
        case (Some(_), Some(_)) => {
          throw ConversionException(
            s"Line: ${line.id} has line types and line sections which both define line types. " +
              s"This error should not happen since PowerFactory only lets you define one of the two."
          )
        }
        case (None, None) =>
          throw ConversionException(
            s"Could not convert line: ${line.id} since there is no defined type in the model and there are no line section that specify the type"
          )
      }
      (
        getNode(line.nodeAId, nodes),
        getNode(line.nodeBId, nodes)
      ) match {
        case (Success(nodeA), Success(nodeB)) =>
          LineConverter.convert(
            line,
            lineLengthPrefix,
            lineType,
            nodeA,
            nodeB
          )
        case (Failure(exc), _) =>
          throw ConversionException(
            s"Can't retrieve ${line.nodeAId} for line ${line.id}",
            exc
          )
        case (_, Failure(exc)) =>
          throw ConversionException(
            s"Can't retrieve ${line.nodeBId} for line ${line.id}",
            exc
          )
      }
    })
  }

  def convert(
      input: Line,
      lengthPrefix: ConversionPrefix,
      lineType: LineTypeInput,
      nodeA: NodeInput,
      nodeB: NodeInput
  ): LineInput = {
    val id = input.id
    // fixme: StandardUnits conversion is handled in the PSDM as intended when 2.0.2 is released
    val length =
      (input.length * lengthPrefix.value).asMetre.to(StandardUnits.LINE_LENGTH)
    val geopos = input.gpsCoords match {
      case Some(gpsCoords) => CoordinateConverter.buildLineString(gpsCoords)
      case None => GridAndGeoUtils.buildSafeLineStringBetweenNodes(nodeA, nodeB)
    }

    new LineInput(
      UUID.randomUUID(),
      id,
      nodeA,
      nodeB,
      1,
      lineType,
      length,
      geopos,
      OlmCharacteristicInput.CONSTANT_CHARACTERISTIC
    )
  }
}
