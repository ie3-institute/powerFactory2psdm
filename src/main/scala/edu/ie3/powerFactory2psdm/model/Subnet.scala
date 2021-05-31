/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.model

import edu.ie3.datamodel.models.voltagelevels.VoltageLevel

import java.util.UUID

/**
  * Data type that wraps necessary information for the different subnets
  *
  * @param id of the subnet
  * @param nodeUuids UUIDS of the nodes inside the subnet
  * @param voltLvl voltage level that the nodes live in
  */
final case class Subnet(id: Int, nodeUuids: Set[UUID], voltLvl: VoltageLevel)
