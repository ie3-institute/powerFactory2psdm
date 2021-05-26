/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter

import edu.ie3.powerFactory2psdm.common.ConverterTestData
import edu.ie3.powerFactory2psdm.exception.pf.ElementConfigurationException
import org.jgrapht.alg.connectivity.BiconnectivityInspector
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

import java.util.UUID
import scala.jdk.CollectionConverters._

class GridGraphBuilderSpec
    extends Matchers
    with ConverterTestData
    with AnyWordSpecLike {

  "The GridGraphBuilder" should {

    val gridGraph = GridGraphBuilder.build(pfGridMaps)
    val inspect = new BiconnectivityInspector(gridGraph)
    val vertexSets = inspect.getConnectedComponents.asScala
      .map(
        graph => graph.vertexSet()
      )

    "add the correct number of nodes to the gridGraph" in {
      gridGraph.vertexSet().size shouldBe pfGridMaps.uuid2Node.size
    }

    "add the correct number of edges to the gridGraph" in {
      gridGraph
        .edgeSet()
        .size shouldBe (pfGridMaps.uuid2Switch ++ pfGridMaps.uuid2Line).size
    }

    "generate the correct number of subnets" in {
      inspect.getConnectedComponents.size shouldBe 5
    }

    "aggregate all nodes of subnet 1 in one of the subgraphs" in {
      vertexSets.contains(subnet1Uuids.asJava) shouldBe true
    }

    "aggregate all nodes of subnet 2 in one of the subgraphs" in {
      vertexSets.contains(subnet2Uuids.asJava) shouldBe true
    }

    "aggregate all nodes of subnet 3 in one of the subgraphs" in {
      vertexSets.contains(subnet3Uuids.asJava) shouldBe true
    }

    "aggregate all nodes of subnet 4 in one of the subgraphs" in {
      vertexSets.contains(subnet4Uuids.asJava) shouldBe true
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
