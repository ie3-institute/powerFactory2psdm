/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.common

import com.typesafe.scalalogging.LazyLogging
import edu.ie3.datamodel.models.StandardUnits.{
  AZIMUTH,
  EFFICIENCY,
  SOLAR_HEIGHT,
  S_RATED
}
import edu.ie3.datamodel.models.input.connector.`type`.LineTypeInput
import edu.ie3.datamodel.models.input.system.PvInput
import edu.ie3.datamodel.models.input.system.characteristic.CosPhiFixed
import edu.ie3.datamodel.models.{OperationTime, StandardUnits, UniqueEntity}
import edu.ie3.datamodel.models.input.{NodeInput, OperatorInput}
import edu.ie3.datamodel.models.voltagelevels.GermanVoltageLevelUtils.LV
import edu.ie3.powerFactory2psdm.config.ConversionConfig

import java.io.File
import edu.ie3.powerFactory2psdm.exception.io.GridParsingException
import edu.ie3.powerFactory2psdm.exception.pf.TestException
import edu.ie3.powerFactory2psdm.io.PfGridParser
import edu.ie3.powerFactory2psdm.model.Subnet
import edu.ie3.powerFactory2psdm.model.powerfactory.{
  ConnectedElement,
  EntityModel,
  GridModel,
  LineType,
  Node,
  StaticGenerator
}
import edu.ie3.util.quantities.PowerSystemUnits.PU
import org.locationtech.jts.geom.{Coordinate, GeometryFactory}
import pureconfig.ConfigSource
import tech.units.indriya.quantity.Quantities
import pureconfig.generic.auto._

import java.util.UUID

object ConverterTestData extends LazyLogging {

  val config: ConversionConfig =
    // ConfigSource.file("src/test/resources/application.conf").loadOrThrow[ConversionConfig]
    ConfigSource.default.at("conversion-config").loadOrThrow[ConversionConfig]

  /**
    * Case class to denote a consistent pair of input and expected output of a conversion
    *
    * @param input  Input model
    * @param result Resulting, converted model
    * @tparam I     Type of input model
    * @tparam R     Type of result class
    */
  final case class ConversionPair[I <: EntityModel, R <: UniqueEntity](
      input: I,
      result: R
  ) {
    def getPair: (I, R) = (input, result)
  }

  logger.warn("Building the grid model")

  val testGridFile =
    s"${new File(".").getCanonicalPath}/src/test/resources/pfGrids/exampleGrid.json"

  val testGrid: GridModel = GridModel.build(
    PfGridParser
      .parse(testGridFile)
      .getOrElse(
        throw GridParsingException(
          s"Couldn't parse the grid file $testGridFile"
        )
      )
  )

  val id2node: Map[String, Node] =
    testGrid.nodes.map(node => (node.id, node)).toMap

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

  val subnet1Ids: Set[String] =
    Set(
      bus1Id,
      bus2Id,
      bus3Id,
      bus4Id,
      bus5Id
    )

  val subnet2Ids: Set[String] = Set(bus7Id)

  val subnet3Ids: Set[String] = Set(bus8Id)

  val subnet4Ids: Set[String] =
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

  val geometryFactory = new GeometryFactory()

  val nodes = Map(
    "someNode" -> ConversionPair(
      Node(
        "someNode",
        0.4,
        1.0,
        Some(11.1123),
        Some(52.1425),
        List(
          ConnectedElement(
            "someConnectedElement",
            "ElmLne"
          )
        )
      ),
      new NodeInput(
        UUID.randomUUID(),
        "someNode",
        OperatorInput.NO_OPERATOR_ASSIGNED,
        OperationTime.notLimited(),
        Quantities.getQuantity(1d, PU),
        false,
        geometryFactory.createPoint(new Coordinate(11.1123, 52.1425)),
        LV,
        1
      )
    ),
    "someSlackNode" -> ConversionPair(
      Node(
        "someSlackNode",
        0.4,
        1.0,
        Some(11.1123),
        Some(52.1425),
        List(
          ConnectedElement(
            "someConnectedElement",
            "ElmXnet"
          )
        )
      ),
      new NodeInput(
        UUID.randomUUID(),
        "someSlackNode",
        OperatorInput.NO_OPERATOR_ASSIGNED,
        OperationTime.notLimited(),
        Quantities.getQuantity(1d, PU),
        true,
        geometryFactory.createPoint(new Coordinate(11.1123, 52.1425)),
        LV,
        2
      )
    )
  )

  def getNodePair(key: String): ConversionPair[Node, NodeInput] = {
    nodes.getOrElse(
      key,
      throw TestException(
        s"Cannot find input/result pair for ${Node.getClass.getSimpleName} with key: $key "
      )
    )
  }

  val subnets = Map(
    "someSubnet" -> Subnet(
      1,
      Set(getNodePair("someNode").input),
      LV
    )
  )

  def getSubnet(key: String): Subnet = subnets.getOrElse(
    key,
    throw TestException(s"Cannot find subnet with key: $key")
  )

  val lineTypes = Map(
    "someLineType" ->
      ConversionPair(
        LineType(
          "someLineType",
          132.0,
          1.0,
          6.753542423248291,
          20.61956214904785,
          151.51515197753906,
          1.543
        ),
        new LineTypeInput(
          UUID.randomUUID(),
          "someLineType",
          Quantities.getQuantity(
            151.51515197753906,
            StandardUnits.ADMITTANCE_PER_LENGTH
          ),
          Quantities.getQuantity(
            1.543,
            StandardUnits.ADMITTANCE_PER_LENGTH
          ),
          Quantities.getQuantity(
            6.753542423248291,
            StandardUnits.IMPEDANCE_PER_LENGTH
          ),
          Quantities.getQuantity(
            20.61956214904785,
            StandardUnits.IMPEDANCE_PER_LENGTH
          ),
          Quantities.getQuantity(
            1000,
            StandardUnits.ELECTRIC_CURRENT_MAGNITUDE
          ),
          Quantities.getQuantity(
            132.0,
            StandardUnits.RATED_VOLTAGE_MAGNITUDE
          )
        )
      )
  )

  def getLineType(key: String): ConversionPair[LineType, LineTypeInput] = {
    lineTypes.getOrElse(
      key,
      throw TestException(
        s"Cannot find input/result pair for ${LineType.getClass.getSimpleName} with key: $key "
      )
    )
  }

  val sampledPvs = Map(
    "somePvPlant" -> ConversionPair(
      StaticGenerator(
        id = "someStatGen",
        busId = "someNode",
        sRated = 11,
        cosPhi = 0.91,
        indCapFlag = 0
      ),
      new PvInput(
        UUID.randomUUID(),
        "someStatGen",
        getNodePair("someNode").result,
        new CosPhiFixed("cosPhiFixed:{(0.0, 0.91)}"),
        0.2,
        Quantities.getQuantity(0, AZIMUTH),
        Quantities.getQuantity(0.95, EFFICIENCY),
        Quantities.getQuantity(35, SOLAR_HEIGHT),
        1d,
        0.9,
        false,
        Quantities.getQuantity(11, S_RATED),
        0.91
      )
    )
  )

  def getSampledPvPair(
      key: String
  ): ConversionPair[StaticGenerator, PvInput] = {
    sampledPvs.getOrElse(
      key,
      throw TestException(
        s"Cannot find input/result pair for ${StaticGenerator.getClass.getSimpleName} with key: $key "
      )
    )
  }

}
