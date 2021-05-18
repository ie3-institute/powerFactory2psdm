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

  "The grid graph builder" should {

    val graph = GridGraphBuilder.build(pfGridMaps)
    val inspect = new BiconnectivityInspector(graph)
    val vertexSets = inspect.getConnectedComponents.asScala
      .map(
        graph => graph.vertexSet()
      )

    def nodeIdsToUUIDs(ids: Set[String]) = {
      ids.map(id => pfGridMaps.nodeId2UUID(id))
    }

    "add the correct number of nodes to the graph" in {
      graph.vertexSet().size shouldBe pfGridMaps.UUID2node.size
    }

    "add the correct number of edges to the graph" in {
      graph
        .edgeSet()
        .size shouldBe (pfGridMaps.UUID2switch ++ pfGridMaps.UUID2line).size
    }

    "generate the correct number of subnets" in {
      inspect.getConnectedComponents.size shouldBe 5
    }

    "aggregate all nodes of subnet 1 in one of the subgraphs" in {
      val subnet1: Set[UUID] = nodeIdsToUUIDs(
        Set(
          idPrefix + "Grid.ElmNet\\Bus_0001.ElmTerm",
          idPrefix + "Grid.ElmNet\\Bus_0002.ElmTerm",
          idPrefix + "Grid.ElmNet\\Bus_0003.ElmTerm",
          idPrefix + "Grid.ElmNet\\Bus_0004.ElmTerm",
          idPrefix + "Grid.ElmNet\\Bus_0005.ElmTerm"
        )
      )
      vertexSets.contains(subnet1.asJava) shouldBe true
    }

    "aggregate all nodes of subnet 2 in one of the subgraphs" in {
      val subnet2 =
        nodeIdsToUUIDs(Set(idPrefix + "Grid.ElmNet\\Bus_0007.ElmTerm"))
      vertexSets.contains(subnet2.asJava) shouldBe true
    }

    "aggregate all nodes of subnet 3 in one of the subgraphs" in {
      val subnet3 =
        nodeIdsToUUIDs(Set(idPrefix + "Grid.ElmNet\\Bus_0008.ElmTerm"))
      vertexSets.contains(subnet3.asJava) shouldBe true
    }

    "aggregate all nodes of subnet 4 in one of the subgraphs" in {
      val subnet4 = nodeIdsToUUIDs(
        Set(
          idPrefix + "Grid.ElmNet\\Bus_0006.ElmTerm",
          idPrefix + "Grid.ElmNet\\Bus_0009.ElmTerm",
          idPrefix + "Grid.ElmNet\\Bus_0011.ElmTerm",
          idPrefix + "Grid.ElmNet\\Bus_0010.ElmTerm",
          idPrefix + "Grid.ElmNet\\Bus_0012.ElmTerm",
          idPrefix + "Grid.ElmNet\\Bus_0013.ElmTerm",
          idPrefix + "Grid.ElmNet\\Bus_0014.ElmTerm",
          idPrefix + "Grid.ElmNet\\Bus_0015.ElmTerm",
          idPrefix + "Grid.ElmNet\\Ortsnetzstation.ElmTrfstat\\1.ElmTerm",
          idPrefix + "Grid.ElmNet\\Ortsnetzstation.ElmTrfstat\\2.ElmTerm",
          idPrefix + "Grid.ElmNet\\Ortsnetzstation.ElmTrfstat\\ON_Station_Lower.ElmTerm"
        )
      )
      vertexSets.contains(subnet4.asJava) shouldBe true
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
