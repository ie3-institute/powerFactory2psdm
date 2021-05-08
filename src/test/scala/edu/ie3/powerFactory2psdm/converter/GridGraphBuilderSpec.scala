/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.converter

import edu.ie3.powerFactory2psdm.common.ConverterTestData
import edu.ie3.powerFactory2psdm.exception.pf.{ElementConfigurationException, PfException, TestException}
import edu.ie3.powerFactory2psdm.model.powerfactory.PowerFactoryGrid
import edu.ie3.powerFactory2psdm.model.powerfactory.PowerFactoryGrid.ConElms
import org.jgrapht.alg.connectivity.BiconnectivityInspector
import org.scalatest.{Matchers, WordSpecLike}

import java.util.UUID
import scala.jdk.CollectionConverters._
import scala.util.{Failure, Success}

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

    def nodeSetToUuidSet(ids: Set[String]) = {
      ids.map(id => pfGridMaps.findNodeUuidFromLocName(id)).map {
        case Success(uuid)      => uuid
        case Failure(exception) => throw exception
      }
    }

    "add the correct number of nodes to the graph" in {
      graph.vertexSet().size shouldBe 15
    }

    "add the correct number of edges to the graph" in {
      graph.edgeSet().size shouldBe 17
    }

    "generate the correct number of subnets" in {
      inspect.getConnectedComponents.size shouldBe 4
    }

    "aggregate all nodes of subnet 1 in one of the subgraphs" in {
      val subnet1: Set[UUID] = nodeSetToUuidSet(
        Set(
          "Bus_0001",
          "Bus_0002",
          "Bus_0003",
          "Bus_0004",
          "Bus_0005"
        )
      )
      vertexSets.contains(subnet1.asJava) shouldBe true
    }

    "aggregate all nodes of subnet 2 in one of the subgraphs" in {
      val subnet2 = nodeSetToUuidSet(Set("Bus_0007"))
      vertexSets.contains(subnet2.asJava) shouldBe true
    }

    "aggregate all nodes of subnet 3 in one of the subgraphs" in {
      val subnet3 = nodeSetToUuidSet(Set("Bus_0008"))
      vertexSets.contains(subnet3.asJava) shouldBe true
    }

    "aggregate all nodes of subnet 4 in one of the subgraphs" in {
      val subnet4 = nodeSetToUuidSet(
        Set(
          "Bus_0006",
          "Bus_0009",
          "Bus_0010",
          "Bus_0011",
          "Bus_0012",
          "Bus_0013",
          "Bus_0014",
          "Bus_0015"
        )
      )
      vertexSets.contains(subnet4.asJava) shouldBe true
    }

    "return a failure if connected elements of an edge do not contain node ids" in {
      val invalidConElms =List(
        ConElms(
          None,
          Some("ElmTerm")),
        ConElms(
          None,
          Some("ElmTerm")),
      )
      val exc = GridGraphBuilder
        .conElms2nodeUuids(invalidConElms, pfGridMaps) match {
        case Success(_) => TestException("The conversion unexpectedly worked.")
        case Failure(exc: PfException) => exc
      }
      exc.getMessage shouldBe "The connected elements do not contain an id."
    }

    }

    "return a Failure if an edge contains more than two connected elements" in {
      val invalidConElms =List(
          ConElms(
            Some("conNodeA"),
            Some("ElmTerm")),
          ConElms(
            Some("conNodeB"),
            Some("ElmTerm")),
          ConElms(
            Some("conNodeC"),
            Some("ElmTerm"))
      )
      val exc = GridGraphBuilder
        .conElms2nodeUuids(invalidConElms, pfGridMaps) match {
        case Success(_) => TestException("The conversion unexpectedly worked.")
        case Failure(exc: PfException) => exc
      }
      exc.getMessage shouldBe "There are more or less connected elements for the edge."
    }


}
