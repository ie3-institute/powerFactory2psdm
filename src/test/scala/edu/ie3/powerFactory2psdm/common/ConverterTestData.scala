/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.common

import com.typesafe.scalalogging.LazyLogging
import edu.ie3.datamodel.models.input.connector.LineInput
import edu.ie3.datamodel.models.input.connector.`type`.{
  LineTypeInput,
  Transformer2WTypeInput
}
import edu.ie3.datamodel.models.input.system.characteristic.CosPhiFixed
import edu.ie3.datamodel.models.input.system.{FixedFeedInInput, PvInput}
import edu.ie3.datamodel.models.input.system.characteristic.OlmCharacteristicInput
import edu.ie3.datamodel.models.{OperationTime, StandardUnits, UniqueEntity}
import edu.ie3.datamodel.models.input.{NodeInput, OperatorInput}
import edu.ie3.datamodel.models.voltagelevels.GermanVoltageLevelUtils.LV
import edu.ie3.datamodel.models.{OperationTime, UniqueEntity}
import edu.ie3.powerFactory2psdm.config.ConversionConfig
import edu.ie3.powerFactory2psdm.config.ConversionConfig.{
  Fixed,
  FixedQCharacteristic,
  PvModelGeneration,
  UniformDistribution
}
import edu.ie3.powerFactory2psdm.converter.CoordinateConverter
import java.io.File
import edu.ie3.powerFactory2psdm.exception.io.GridParsingException
import edu.ie3.powerFactory2psdm.exception.pf.TestException
import edu.ie3.powerFactory2psdm.io.PfGridParser
import edu.ie3.powerFactory2psdm.model.entity.{
  ConnectedElement,
  EntityModel,
  Line,
  Node,
  StaticGenerator,
  Subnet
}
import edu.ie3.powerFactory2psdm.model.entity.types.{
  LineType,
  TransformerType2W
}
import edu.ie3.powerFactory2psdm.model.PreprocessedPfGridModel
import edu.ie3.powerFactory2psdm.util.QuantityUtils.RichQuantityDouble
import org.locationtech.jts.geom.{Coordinate, GeometryFactory}
import pureconfig.ConfigSource
import edu.ie3.util.quantities.PowerSystemUnits.{
  DEGREE_GEOM,
  KILOMETRE,
  KILOVOLT,
  PU,
  VOLTAMPERE
}
import org.locationtech.jts.geom.{Coordinate, GeometryFactory}
import pureconfig.ConfigSource
import tech.units.indriya.quantity.Quantities
import tech.units.indriya.unit.Units.{OHM, PERCENT, SIEMENS}
import pureconfig.generic.auto._
import java.io.File
import java.util.UUID

object ConverterTestData extends LazyLogging {

  val config: ConversionConfig =
    ConfigSource.default.at("conversion-config").loadOrThrow[ConversionConfig]

  /** Case class to denote a consistent pair of input and expected output of a
    * conversion
    *
    * @param input
    *   Input model
    * @param result
    *   Resulting, converted model
    * @tparam I
    *   Type of input model
    * @tparam R
    *   Type of result class
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

  val testGrid: PreprocessedPfGridModel = PreprocessedPfGridModel.build(
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
        1d.toPu,
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
        1d.toPu,
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
          151.51515197753906.toMicroSiemensPerKilometre,
          1.543.toMicroSiemensPerKilometre,
          6.753542423248291.toOhmPerKilometre,
          20.61956214904785.toOhmPerKilometre,
          1.toKiloAmpere,
          132.0.toKiloVolt
        )
      )
  )

  def getLineTypePair(key: String): ConversionPair[LineType, LineTypeInput] = {
    lineTypes.getOrElse(
      key,
      throw TestException(
        s"Cannot find input/result pair for ${LineType.getClass.getSimpleName} with key: $key "
      )
    )
  }

  val staticGenerator: StaticGenerator = StaticGenerator(
    id = "someStatGen",
    busId = "someNode",
    sRated = 11,
    cosPhi = 0.91,
    indCapFlag = 0,
    category = "Statischer Generator"
  )

  val statGenCosPhiExcMsg: String => String = (id: String) =>
    s"Can't determine cos phi rated for static generator: $id. Exception: The inductive capacitive specifier should be either 0 (inductive) or 1 (capacitive)"

  val pvModelGeneration: PvModelGeneration = PvModelGeneration(
    albedo = Fixed(0.2),
    azimuth = UniformDistribution(-90, 90),
    etaConv = Fixed(0.95),
    elevationAngle = UniformDistribution(20, 50),
    qCharacteristic = FixedQCharacteristic,
    kG = Fixed(0.9),
    kT = Fixed(1)
  )

  val generatePvs = Map(
    "somePvPlant" -> ConversionPair(
      staticGenerator.copy(category = "Fotovoltaik"),
      new PvInput(
        UUID.randomUUID(),
        "someStatGen",
        getNodePair("someNode").result,
        new CosPhiFixed("cosPhiFixed:{(0.0, 0.91)}"),
        0.2,
        0.toDegreeGeom,
        95.toPercent,
        35.toDegreeGeom,
        1d,
        0.9,
        false,
        11.toMegaVoltAmpere,
        0.91
      )
    )
  )

  def getGeneratePvPair(
      key: String
  ): ConversionPair[StaticGenerator, PvInput] = {
    generatePvs.getOrElse(
      key,
      throw TestException(
        s"Cannot find input/result pair for StaticGenerator/PvInput with key: $key "
      )
    )
  }

  val staticGenerator2FeedInPair = Map(
    "someStatGen" -> ConversionPair(
      staticGenerator,
      new FixedFeedInInput(
        UUID.randomUUID(),
        "someStatGen",
        getNodePair("someNode").result,
        new CosPhiFixed("cosPhiFixed:{(0.0, 0.91)}"),
        11.toMegaVoltAmpere,
        0.91
      )
    )
  )

  def getStaticGenerator2FixedFeedInPair(
      key: String
  ): ConversionPair[StaticGenerator, FixedFeedInInput] = {
    staticGenerator2FeedInPair.getOrElse(
      key,
      throw TestException(
        s"Cannot find input/result pair for static generator to fixed feed in with key: $key"
      )
    )
  }

  val lines = Map(
    "someLine" ->
      ConversionPair(
        Line(
          "someLine",
          "someNode",
          "someSlackNode",
          "someLineType",
          1.5,
          Some(List((11.1123, 52.1425), (11.1153, 52.1445)))
        ),
        new LineInput(
          UUID.randomUUID(),
          "someLine",
          getNodePair("someNode").result,
          getNodePair("someSlackNode").result,
          1,
          getLineTypePair("someLineType").result,
          Quantities.getQuantity(1.5, KILOMETRE),
          CoordinateConverter.buildLineString(
            List((11.1123, 52.1425), (11.1153, 52.1445))
          ),
          OlmCharacteristicInput.CONSTANT_CHARACTERISTIC
        )
      )
  )
  def getLinePair(key: String): ConversionPair[Line, LineInput] = {
    lines.getOrElse(
      key,
      throw TestException(
        s"Cannot find input/result pair for ${Line.getClass.getSimpleName} with key: $key "
      )
    )
  }

  val transformerTypes = Map(
    "SomeTrafo2wType" -> ConversionPair(
      TransformerType2W(
        id = "SomeTrafo2wType",
        sRated = 40d,
        vRatedA = 110d,
        vRatedB = 10d,
        dV = 2.5,
        dPhi = 5d,
        tapSide = 0,
        tapNeutr = 0,
        tapMin = -10,
        tapMax = 10,
        uk = 5,
        iNoLoad = 1,
        pFe = 10,
        pCu = 6
      ),
      new Transformer2WTypeInput(
        UUID.randomUUID(),
        "SomeTrafo2wType",
        45.375.toMilliOhm,
        15.1249319.toOhm,
        40d.toMegaVoltAmpere,
        110d.toKiloVolt,
        10d.toKiloVolt,
        826.4462809.toNanoSiemens,
        33047.519046.toNanoSiemens,
        2.5.toPercent,
        5d.toDegreeGeom,
        false,
        0,
        -10,
        10
      )
    )
  )

  def getTransformer2wType(
      key: String
  ): ConversionPair[TransformerType2W, Transformer2WTypeInput] = {
    transformerTypes.getOrElse(
      key,
      throw TestException(
        s"Cannot find input/result pair for ${TransformerType2W.getClass.getSimpleName} with key: $key "
      )
    )
  }

}
