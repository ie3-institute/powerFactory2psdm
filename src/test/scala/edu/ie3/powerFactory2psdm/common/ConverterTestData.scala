/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.common

import java.io.File
import edu.ie3.powerFactory2psdm.exception.io.GridParsingException
import edu.ie3.powerFactory2psdm.io.PfGridParser
import edu.ie3.powerFactory2psdm.model.powerfactory.{
  PowerFactoryGrid,
  PowerFactoryGridMaps
}

import java.util.UUID

trait ConverterTestData {

  def nodeIdsToUuids(
      nodeId2Uuid: Map[String, UUID],
      ids: Set[String]
  ): Set[UUID] = {
    ids.map(id => nodeId2Uuid(id))
  }

  val testGridFile =
    s"${new File(".").getCanonicalPath}/src/test/resources/pfGrids/exampleGrid.json"

  val testGrid: PowerFactoryGrid = PfGridParser
    .parse(testGridFile)
    .getOrElse(
      throw GridParsingException(s"Couldn't parse the grid file $testGridFile")
    )

  val pfGridMaps: PowerFactoryGridMaps = PowerFactoryGridMaps(testGrid)

  val bus1Id = "Grid.ElmNet\\Bus_0001.ElmTerm"
  val bus2Id = "Grid.ElmNet\\Bus_0002.ElmTerm"
  val bus3Id = "Grid.ElmNet\\Bus_0003.ElmTerm"
  val bus4Id = "Grid.ElmNet\\Bus_0004.ElmTerm"
  val bus5Id = "Grid.ElmNet\\Bus_0005.ElmTerm"
  val bus6Id = "Grid.ElmNet\\Bus_0006.ElmTerm"
  val bus7Id = "Grid.ElmNet\\Bus_0007.ElmTerm"
  val bus8Id = "Grid.ElmNet\\Bus_0008.ElmTerm"
  val bus9Id = "Grid.ElmNet\\Bus_0009.ElmTerm"
  val bus10Id = "Grid.ElmNet\\Bus_0010.ElmTerm"
  val bus11Id = "Grid.ElmNet\\Bus_0011.ElmTerm"
  val bus12Id = "Grid.ElmNet\\Bus_0012.ElmTerm"
  val bus13Id = "Grid.ElmNet\\Bus_0013.ElmTerm"
  val bus14Id = "Grid.ElmNet\\Bus_0014.ElmTerm"
  val bus15Id = "Grid.ElmNet\\Bus_0015.ElmTerm"
  val busOns1Id = "Grid.ElmNet\\Ortsnetzstation.ElmTrfstat\\1.ElmTerm"
  val busOns2Id = "Grid.ElmNet\\Ortsnetzstation.ElmTrfstat\\2.ElmTerm"
  val busOnsLv =
    "Grid.ElmNet\\Ortsnetzstation.ElmTrfstat\\ON_Station_Lower.ElmTerm"

  val subnet1Uuids: Set[UUID] = nodeIdsToUuids(
    pfGridMaps.nodeId2Uuid,
    Set(
      bus1Id,
      bus2Id,
      bus3Id,
      bus4Id,
      bus5Id
    )
  )

  val subnet2Uuids: Set[UUID] =
    nodeIdsToUuids(pfGridMaps.nodeId2Uuid, Set(bus7Id))

  val subnet3Uuids: Set[UUID] =
    nodeIdsToUuids(pfGridMaps.nodeId2Uuid, Set(bus8Id))

  val subnet4Uuids: Set[UUID] = nodeIdsToUuids(
    pfGridMaps.nodeId2Uuid,
    Set(
      bus6Id,
      bus9Id,
      bus10Id,
      bus11Id,
      bus12Id,
      bus13Id,
      bus14Id,
      bus15Id,
      busOns1Id,
      busOns2Id,
      busOnsLv
    )
  )

}
