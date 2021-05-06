/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter

import edu.ie3.powerFactory2psdm.common.ConverterTestData
import edu.ie3.powerFactory2psdm.model.powerfactory.PowerFactoryGrid.Nodes
import org.jgrapht.alg.connectivity.BiconnectivityInspector
import org.scalatest.{Matchers, WordSpecLike}
import scala.jdk.CollectionConverters._

class GridGraphBuilderSpec
    extends Matchers
    with ConverterTestData
    with WordSpecLike {

  "The grid graph builder" should {

    val graph = GridGraphBuilder.build(pfGridMaps)
    val inspect = new BiconnectivityInspector(graph)
    val vertexSets = inspect.getConnectedComponents.asScala
      .map(
        graph => graph.vertexSet()
      )

    "add the correct number of nodes to the graph" in {
      graph.vertexSet().size shouldBe 15
    }

    "add the correct number of edges to the graph" in {
      graph.edgeSet().size shouldBe 17
    }

    "generate the correct number of subnets" in {
      inspect.getConnectedComponents.size shouldBe 4
    }

//    "aggregate all nodes of subnet 1 in one of the subgraphs" in {
//
//      val subnet1: Set[Nodes] =
//        Set("Bus_0001", "Bus_0002", "Bus_0003", "Bus_0004", "Bus_0005").map(
//          id => nodesMap(id)
//        )
//
//      vertexSets.contains(subnet1.asJava) shouldBe true
//    }
//
//    "aggregate all nodes of subnet 2 in one of the subgraphs" in {
//
//      val subnet2 = Set("Bus_0007").map(id => nodesMap(id))
//      vertexSets.contains(subnet2.asJava) shouldBe true
//    }
//
//    "aggregate all nodes of subnet 3 in one of the subgraphs" in {
//
//      val subnet3 = Set("Bus_0008").map(id => nodesMap(id))
//      vertexSets.contains(subnet3.asJava) shouldBe true
//    }
//
//    "aggregate all nodes of subnet 4 in one of the subgraphs" in {
//
//      val subnet4 = Set(
//        "Bus_0006",
//        "Bus_0009",
//        "Bus_0010",
//        "Bus_0011",
//        "Bus_0012",
//        "Bus_0013",
//        "Bus_0014"
//      ).map(id => nodesMap(id))
//      vertexSets.contains(subnet4.asJava) shouldBe true
//    }
  }
}
