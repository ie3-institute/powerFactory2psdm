/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.reduce

import edu.ie3.datamodel.io.naming.FileNamingStrategy
import edu.ie3.powerFactory2psdm.io.IoUtils
import edu.ie3.powerFactory2psdm.reduce.GridModelReducer.reduceGrid
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

import java.io.File
import scala.jdk.CollectionConverters.SetHasAsScala

class GridModelReducerSpec extends Matchers with AnyWordSpecLike {

  "A grid model reducer" should {

    "reduce a grid successfully" in {
      val gridName = "exampleGrid"
      val csvSep = ","
      val inputDir = new File(
        "."
      ).getCanonicalPath + "/src/test/resources/psdmGrid/vn_146_lv_small"
      val namingStrategy = new FileNamingStrategy()
      val reducedGridName = "reduced_" + gridName

      val inputGrid =
        IoUtils.parsePsdmGrid(
          reducedGridName,
          csvSep,
          inputDir,
          namingStrategy
        )

      val reducedGrid = reduceGrid(
        reducedGridName,
        inputGrid
      )
      val nodes = reducedGrid.getRawGrid.getNodes
      val fixedFeedIns = reducedGrid.getSystemParticipants.getFixedFeedIns
      val mappedNodes = fixedFeedIns.asScala.map(f => f.getNode)

      reducedGrid.getSystemParticipants.getFixedFeedIns.size shouldBe nodes.size
      nodes.asScala.toSet.diff(mappedNodes) shouldBe Set()

    }
  }
}
