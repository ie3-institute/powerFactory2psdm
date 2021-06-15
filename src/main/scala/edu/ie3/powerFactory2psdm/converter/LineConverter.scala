/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter

import edu.ie3.util.quantities.PowerSystemUnits.KILOMETRE
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.connector.LineInput
import edu.ie3.datamodel.models.input.connector.`type`.LineTypeInput
import edu.ie3.datamodel.models.input.system.characteristic.OlmCharacteristicInput
import edu.ie3.datamodel.utils.GridAndGeoUtils
import edu.ie3.powerFactory2psdm.exception.pf.ConversionException
import edu.ie3.powerFactory2psdm.model.powerfactory.Line
import org.locationtech.jts.geom.LineString
import tech.units.indriya.quantity.Quantities
import java.util.UUID

object LineConverter {

  def convert(
               input: Line,
               lineTypeId2lineTypeInput: Map[String, LineTypeInput],
               id2psdmNodes: Map[String, NodeInput]
  ): LineInput = {

    val id = input.id
    val nodeA = id2psdmNodes(input.nodeAId)
    val nodeB = id2psdmNodes(input.nodeBId)
    val lineType = lineTypeId2lineTypeInput.getOrElse(
      input.typId,
      throw ConversionException(
        s"Line type: ${input.typId} of line ${input.id} couldn't be found."
      )
    )
    val length = Quantities.getQuantity(
      input.length,
      KILOMETRE
    )

    val geopos  = input.gpsCoords match {
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
