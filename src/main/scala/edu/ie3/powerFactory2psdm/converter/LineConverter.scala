/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter

import tech.units.indriya.unit.Units.METRE
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.connector.LineInput
import edu.ie3.datamodel.models.input.connector.`type`.LineTypeInput
import edu.ie3.datamodel.models.input.system.characteristic.OlmCharacteristicInput
import edu.ie3.datamodel.utils.GridAndGeoUtils
import edu.ie3.powerFactory2psdm.model.powerfactory.Line
import tech.units.indriya.quantity.Quantities
import java.util.UUID

object LineConverter {

  def convert(
      input: Line,
      lengthPrefix: Double,
      lineType: LineTypeInput,
      nodeA: NodeInput,
      nodeB: NodeInput
  ): LineInput = {

    val id = input.id
    val length = Quantities.getQuantity(
      input.length * lengthPrefix,
      METRE
    )

    val geopos = input.gpsCoords match {
      case Some(gpsCoords) => CoordinateConverter.buildLineString(gpsCoords)
      case None            => GridAndGeoUtils.buildSafeLineStringBetweenNodes(nodeA, nodeB)
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
