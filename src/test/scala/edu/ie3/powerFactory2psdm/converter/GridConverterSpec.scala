/**
 * © 2021. Johannes Hiry,
 **/

package edu.ie3.powerFactory2psdm.converter

import edu.ie3.powerFactory2psdm.common.ConverterTestData
import edu.ie3.powerFactory2psdm.model.entity.StaticGenerator.StatGenCategories.{
  PV,
  WEC
}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class GridConverterSpec extends Matchers with AnyWordSpecLike {

  "A grid converter" should {
    val config = ConverterTestData.config
    val rawGrid = ConverterTestData.parseRawGrid
    val preProcessedGrid = ConverterTestData.buildPreProcessedTestGrid
    val result = GridConverter.convert(rawGrid, config)

    "convert all grid elements" in {
      val gridElements = result.getRawGrid
      gridElements.getNodes.size shouldBe preProcessedGrid.nodes.size
      gridElements.getLines.size shouldBe preProcessedGrid.lines.size
      gridElements.getSwitches.size shouldBe preProcessedGrid.switches.size
      gridElements.getTransformer2Ws.size shouldBe preProcessedGrid.transformers2W.size
    }

    "convert all system participants" in {
      result.getSystemParticipants.getLoads.size shouldBe preProcessedGrid.loads.size
      val expectedPvs = preProcessedGrid.staticGenerators.count(statgen =>
        statgen.category == PV
      )
      result.getSystemParticipants.getPvPlants.size shouldBe expectedPvs
      val expectedWecs = preProcessedGrid.staticGenerators.count(statgen =>
        statgen.category == WEC
      )
      result.getSystemParticipants.getWecPlants.size shouldBe expectedWecs

    }
  }
}
