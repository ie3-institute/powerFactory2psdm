/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter

import edu.ie3.powerFactory2psdm.common.ConverterTestData.{
  subnet1Ids,
  subnet2Ids,
  subnet3Ids,
  subnet4Ids,
  testGrid
}
import edu.ie3.powerFactory2psdm.exception.pf.ElementConfigurationException
import org.jgrapht.alg.connectivity.BiconnectivityInspector
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

import scala.jdk.CollectionConverters._

class GridGraphBuilderSpec extends Matchers with AnyWordSpecLike {

  "The GridGraphBuilder" should {

    val gridGraph = GridGraphBuilder.build(
      testGrid.nodes,
      testGrid.lines ++ testGrid.switches
    )
    val inspect = new BiconnectivityInspector(gridGraph)
    val vertexSets = inspect.getConnectedComponents.asScala
      .map(graph => graph.vertexSet())

    "add the correct number of nodes to the gridGraph" in {
      gridGraph.vertexSet().size shouldBe testGrid.nodes.size
    }

    "add the correct number of edges to the gridGraph" in {
      gridGraph
        .edgeSet()
        .size shouldBe (testGrid.lines ++ testGrid.switches).size
    }

    "generate the correct number of subnets" in {
      inspect.getConnectedComponents.size shouldBe 5
    }

    "aggregate all nodes of subnet 1 in one of the subgraphs" in {
      vertexSets.contains(subnet1Ids.asJava) shouldBe true
    }

    "aggregate all nodes of subnet 2 in one of the subgraphs" in {
      vertexSets.contains(subnet2Ids.asJava) shouldBe true
    }

    "aggregate all nodes of subnet 3 in one of the subgraphs" in {
      vertexSets.contains(subnet3Ids.asJava) shouldBe true
    }

    "aggregate all nodes of subnet 4 in one of the subgraphs" in {
      vertexSets.contains(subnet4Ids.asJava) shouldBe true
    }

    "throw an Exception when trying to unpack busses of singly connected edge" in {

      val thrown = intercept[ElementConfigurationException](
        GridGraphBuilder.unpackConnectedBusses("myEdge", Some("bus1"), None)
      )

      thrown.getMessage shouldBe "Exception occurred while adding an edge. " +
        "Exc: Edge with id: myEdge is missing at least one connected node"
    }
  }
}
