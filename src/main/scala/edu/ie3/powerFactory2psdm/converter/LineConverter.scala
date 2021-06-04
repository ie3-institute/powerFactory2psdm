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
import edu.ie3.powerFactory2psdm.exception.pf.{
  ElementConfigurationException,
  GridConfigurationException,
  MissingParameterException
}
import edu.ie3.powerFactory2psdm.model.powerfactory.PowerFactoryGrid.Lines
import edu.ie3.powerFactory2psdm.model.powerfactory.PowerFactoryGridMaps
import org.jgrapht.graph.{DefaultEdge, Multigraph}
import org.locationtech.jts.geom.LineString
import tech.units.indriya.quantity.Quantities

import java.util
import java.util.UUID
import scala.jdk.CollectionConverters.CollectionHasAsScala
import scala.util.{Failure, Success, Try}

object LineConverter {

  def convert(
      input: Lines,
      lineTypeId2LineTypeInput: Map[String, LineTypeInput],
      nodeId2Uuid: Map[String, UUID],
      uuid2NodeInput: Map[UUID, NodeInput]
  ): LineInput = {

    val id = input.id.getOrElse(
      throw MissingParameterException(s"The line: $input has no ID.")
    )
    val nodeAId = input.bus1Id.getOrElse(
      throw MissingParameterException(s"Line $id has no defined bus1Id.")
    )
    val nodeBId = input.bus2Id.getOrElse(
      throw MissingParameterException(s"Line $id has no defined bus2Id.")
    )
    val nodeA = uuid2NodeInput(nodeId2Uuid(nodeAId))
    val nodeB = uuid2NodeInput(nodeId2Uuid(nodeBId))
    val lineType = lineTypeId2LineTypeInput(
      input.typId.getOrElse(
        throw MissingParameterException(
          s"Id of the line type of line: $id, isn't defined"
        )
      )
    )
    val length = Quantities.getQuantity(
      input.dline.getOrElse(
        throw MissingParameterException(
          "Length of the line: $id, isn't defined"
        )
      ),
      KILOMETRE
    )
    val geopos: LineString =
      GridAndGeoUtils.buildSafeLineStringBetweenNodes(nodeA, nodeB)
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
