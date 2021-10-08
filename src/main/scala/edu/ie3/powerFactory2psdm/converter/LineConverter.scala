/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter

import edu.ie3.datamodel.models.StandardUnits
import tech.units.indriya.unit.Units.METRE
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.connector.LineInput
import edu.ie3.datamodel.models.input.connector.`type`.LineTypeInput
import edu.ie3.datamodel.models.input.system.characteristic.OlmCharacteristicInput
import edu.ie3.datamodel.utils.GridAndGeoUtils
import edu.ie3.powerFactory2psdm.converter.types.LineTypeConverter
import edu.ie3.powerFactory2psdm.model.entity.Line
import edu.ie3.powerFactory2psdm.model.setting.ConversionPrefixes.ConversionPrefix
import edu.ie3.powerFactory2psdm.util.QuantityUtils.RichQuantityDouble
import tech.units.indriya.quantity.Quantities

import java.util.UUID

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
      LineConverter.convert(
        line,
        lineLengthPrefix,
        LineTypeConverter.getLineType(line.typId, lineTypes),
        NodeConverter.getNode(line.nodeAId, nodes),
        NodeConverter.getNode(line.nodeBId, nodes)
      )
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
