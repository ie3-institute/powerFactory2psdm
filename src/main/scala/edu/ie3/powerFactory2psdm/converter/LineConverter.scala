/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter

import edu.ie3.datamodel.models.input.connector.LineInput
import edu.ie3.datamodel.models.input.connector.`type`.LineTypeInput
import edu.ie3.powerFactory2psdm.exception.pf.ElementConfigurationException
import edu.ie3.powerFactory2psdm.model.powerfactory.PowerFactoryGrid.Lines
import edu.ie3.powerFactory2psdm.model.powerfactory.PowerFactoryGridMaps

object LineConverter {

  def convert(input: Lines, lineTypeId2LineTypeInput: Map[String, LineTypeInput], pfGridMaps: PowerFactoryGridMaps): LineInput = {

    //    uuid: UUID

    //    id: String
    val id = input.id.getOrElse(throw ElementConfigurationException(s"The line: $input has no ID."))

    //    operator: OperatorInput

    //    operationTime: OperationTime

    //    nodeA: NodeInput
    val nodeA = input.bus1Id.getOrElse()

    //    nodeB: NodeInput

    //    parallelDevices: Int

    //    `type`: LineTypeInput

    //    length: ComparableQuantity[Length]

    //    geoPosition: LineString

    //    olmCharacteristic: OlmCharacteristicInput
    (input.id, input.bus1Id, input.bus2Id, input.dline, )

    ???
  }

}
