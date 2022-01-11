/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.common

import com.typesafe.scalalogging.LazyLogging
import edu.ie3.datamodel.models.input.connector.{
  LineInput,
  SwitchInput,
  Transformer2WInput
}
import edu.ie3.datamodel.models.input.system.`type`.{
  SystemParticipantTypeInput,
  WecTypeInput
}
import edu.ie3.datamodel.models.input.system.characteristic.{
  OlmCharacteristicInput,
  ReactivePowerCharacteristic,
  WecCharacteristicInput
}
import edu.ie3.datamodel.models.input.system.{
  FixedFeedInInput,
  PvInput,
  WecInput
}
import edu.ie3.datamodel.models.input.connector.`type`.{
  LineTypeInput,
  Transformer2WTypeInput
}
import edu.ie3.datamodel.models.input.system.LoadInput
import edu.ie3.datamodel.models.input.system.characteristic.CosPhiFixed
import edu.ie3.datamodel.models.BdewLoadProfile
import edu.ie3.datamodel.models.input.{NodeInput, OperatorInput}
import edu.ie3.datamodel.models.voltagelevels.GermanVoltageLevelUtils.LV
import edu.ie3.datamodel.models.{OperationTime, UniqueEntity}
import edu.ie3.powerFactory2psdm.config.ConversionConfigUtils.{
  DependentQCharacteristic,
  FixedQCharacteristic
}
import edu.ie3.powerFactory2psdm.config.model.PvConversionConfig.PvModelGeneration
import edu.ie3.powerFactory2psdm.config.model.WecConversionConfig.WecModelGeneration
import edu.ie3.powerFactory2psdm.converter.CoordinateConverter
import edu.ie3.powerFactory2psdm.exception.io.GridParsingException
import edu.ie3.powerFactory2psdm.exception.pf.TestException
import edu.ie3.powerFactory2psdm.generator.ParameterSamplingMethod.{
  Fixed,
  UniformDistribution
}
import edu.ie3.powerFactory2psdm.io.PfGridParser
import edu.ie3.powerFactory2psdm.model.entity.{
  Line,
  StaticGenerator,
  Transformer2W
}
import edu.ie3.powerFactory2psdm.util.QuantityUtils.RichQuantityDouble
import edu.ie3.powerFactory2psdm.model.entity.types.Transformer2WType
import edu.ie3.powerFactory2psdm.model.entity._
import edu.ie3.powerFactory2psdm.config.ConversionConfig
import edu.ie3.powerFactory2psdm.model.entity.{
  ConnectedElement,
  EntityModel,
  Node,
  Subnet
}
import edu.ie3.powerFactory2psdm.model.entity.types.LineType
import edu.ie3.powerFactory2psdm.model.{PreprocessedPfGridModel, RawPfGridModel}
import org.locationtech.jts.geom.{Coordinate, GeometryFactory}
import pureconfig.ConfigSource
import pureconfig.generic.auto._
import edu.ie3.datamodel.models.voltagelevels.GermanVoltageLevelUtils.MV_10KV
import edu.ie3.powerFactory2psdm.model.entity.StaticGenerator.StatGenCategories.{
  OTHER,
  PV,
  WEC
}

import java.io.File
import java.util.{Locale, UUID}
import scala.util.{Failure, Success, Try}

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

  /** Case class to denote a consistent pair of input and expected output of a
    * conversion
    *
    * @param input
    *   Input model
    * @param resultModel
    *   Resulting, converted model
    * @param resultType
    *   Resulting, converted type of the model
    * @tparam I
    *   Type of input model
    * @tparam M
    *   Type of result class
    */
  final case class ConversionPairWithType[
      I <: EntityModel,
      M <: UniqueEntity,
      T <: SystemParticipantTypeInput
  ](
      input: I,
      resultModel: M,
      resultType: T
  )

  val testGridFile =
    s"${new File(".").getCanonicalPath}/src/test/resources/pfGrids/exampleGrid.json"

  def parseRawGrid: RawPfGridModel =
    PfGridParser
      .parse(testGridFile)
      .getOrElse(
        throw GridParsingException(
          s"Couldn't parse the grid file $testGridFile"
        )
      )

  def buildPreProcessedTestGrid: PreprocessedPfGridModel =
    Try {
      PreprocessedPfGridModel.build(
        parseRawGrid,
        config.modelConfigs.sRatedSource,
        config.modelConfigs.cosPhiSource
      )
    } match {
      case Failure(exc) =>
        throw TestException(
          s"Could not build the grid in $testGridFile and therefore was not able to initialize the ConverterTestData. " +
            s"This is probably due to the generated grid schema of the SchemaGenerator and the test grid being out of sync.",
          exc
        )
      case Success(grid) => grid
    }

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
        1d.asPu,
        false,
        geometryFactory.createPoint(new Coordinate(11.1123, 52.1425)),
        LV,
        1
      )
    ),
    "someSlackNode" -> ConversionPair(
      Node(
        "someSlackNode",
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
        1d.asPu,
        true,
        geometryFactory.createPoint(new Coordinate(11.1123, 52.1425)),
        LV,
        2
      )
    ),
    "someMvNode" -> ConversionPair(
      Node(
        "someMvNode",
        "someMvNode",
        10.0,
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
        "someMvNode",
        OperatorInput.NO_OPERATOR_ASSIGNED,
        OperationTime.notLimited(),
        1d.asPu,
        true,
        geometryFactory.createPoint(new Coordinate(11.1123, 52.1425)),
        MV_10KV,
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
          151.51515197753906.asMicroSiemensPerKilometre,
          1.543.asMicroSiemensPerKilometre,
          6.753542423248291.asOhmPerKilometre,
          20.61956214904785.asOhmPerKilometre,
          1.asKiloAmpere,
          132.0.asKiloVolt
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
    category = OTHER
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

  val generatePvs: Map[String, ConversionPair[StaticGenerator, PvInput]] = Map(
    "somePvPlant" -> ConversionPair(
      staticGenerator.copy(category = PV),
      new PvInput(
        UUID.randomUUID(),
        "someStatGen",
        getNodePair("someNode").result,
        new CosPhiFixed("cosPhiFixed:{(0.0, 0.91)}"),
        0.2,
        0.asDegreeGeom,
        95.asPercent,
        35.asDegreeGeom,
        1d,
        0.9,
        false,
        11.asMegaVoltAmpere,
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
        11.asMegaVoltAmpere,
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

  val wecModelGeneration: WecModelGeneration = WecModelGeneration(
    capex = Fixed(100d),
    opex = Fixed(50d),
    cpCharacteristic = "cP:{(10.00,0.05),(15.00,0.10),(20.00,0.20)}",
    hubHeight = Fixed(200),
    rotorArea = Fixed(45),
    etaConv = Fixed(96),
    qCharacteristic =
      DependentQCharacteristic("cosPhiP:{(0.0,1.0),(0.9,1.0),(1.2,-0.3)}")
  )

  val wecType: Map[String, WecTypeInput] = Map(
    "someWecType" -> new WecTypeInput(
      UUID.randomUUID(),
      "someWecType",
      100.asEuro,
      50.asEuroPerMegaWattHour,
      11.asMegaVoltAmpere,
      0.91,
      new WecCharacteristicInput("cP:{(10.00,0.05),(15.00,0.10),(20.00,0.20)}"),
      96.asPercent,
      45.asSquareMetre,
      200.asMetre
    )
  )

  def getWecType(key: String): WecTypeInput = {
    wecType.getOrElse(
      key,
      throw TestException(s"Cannot find WEC type with key: $key")
    )
  }
  val generateWecs: Map[String, ConversionPairWithType[
    StaticGenerator,
    WecInput,
    WecTypeInput
  ]] = Map(
    "someWec" -> ConversionPairWithType(
      staticGenerator.copy(id = "someWec", category = WEC),
      new WecInput(
        UUID.randomUUID(),
        "someWec",
        getNodePair("someNode").result,
        ReactivePowerCharacteristic.parse(
          "cosPhiP:{(0.0,1.0),(0.9,1.0),(1.2,-0.3)}"
        ),
        getWecType("someWecType"),
        false
      ),
      getWecType("someWecType")
    )
  )

  def getGenerateWecPair(
      key: String
  ): ConversionPairWithType[StaticGenerator, WecInput, WecTypeInput] = {
    generateWecs.getOrElse(
      key,
      throw TestException(s"Cannot find WEC generation pair with key: $key")
    )
  }

  val lines = Map(
    "someLine" ->
      ConversionPair(
        Line(
          "someLine",
          "someNode",
          "someSlackNode",
          Some("someLineType"),
          None,
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
          1.5.asKilometre,
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

  val loads = Map(
    "someLoad" -> ConversionPair(
      Load(
        "someLoad",
        "someNode",
        13231.23,
        0.97812,
        0,
        isScaled = false,
        None
      ),
      new LoadInput(
        UUID.randomUUID(),
        "someLoad",
        OperatorInput.NO_OPERATOR_ASSIGNED,
        OperationTime.notLimited(),
        getNodePair("someNode").result,
        new CosPhiFixed(
          "cosPhiFixed:{(0.0,%#.2f)}".formatLocal(Locale.ENGLISH, 0.97812)
        ),
        BdewLoadProfile.H0,
        false,
        0d.asKiloWattHour,
        13231.23.asVoltAmpere,
        0.97812
      )
    )
  )

  def getLoadPair(key: String): ConversionPair[Load, LoadInput] = {
    loads.getOrElse(
      key,
      throw TestException(
        s"Cannot find input/result pair for ${Load.getClass.getSimpleName} with key: $key "
      )
    )
  }

  val transformerTypes = Map(
    "someTrafo2WType" -> ConversionPair(
      Transformer2WType(
        id = "someTrafo2WType",
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
        "someTrafo2WType",
        45.375.asMilliOhm,
        15.1249319.asOhm,
        40d.asMegaVoltAmpere,
        110d.asKiloVolt,
        10d.asKiloVolt,
        826.4462809.asNanoSiemens,
        33047.519046.asNanoSiemens,
        2.5.asPercent,
        5d.asDegreeGeom,
        false,
        0,
        -10,
        10
      )
    ),
    "10 -> 0.4" -> ConversionPair(
      Transformer2WType(
        id = "10 -> 0.4",
        sRated = 40d,
        vRatedA = 10d,
        vRatedB = 0.4,
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
        "10 -> 0.4",
        45.375.asMilliOhm,
        15.1249319.asOhm,
        40d.asMegaVoltAmpere,
        10d.asKiloVolt,
        0.4.asKiloVolt,
        2480.5790.asNanoSiemens,
        32972.94113.asNanoSiemens,
        2.5.asPercent,
        5d.asDegreeGeom,
        false,
        0,
        -10,
        10
      )
    )
  )

  def getTransformer2WTypePair(
      key: String
  ): ConversionPair[Transformer2WType, Transformer2WTypeInput] = {
    transformerTypes.getOrElse(
      key,
      throw TestException(
        s"Cannot find input/result pair for ${Transformer2WType.getClass.getSimpleName} with key: $key "
      )
    )
  }

  val transformers2w = Map(
    "someTransformer2W" -> ConversionPair(
      Transformer2W(
        "someTransformer2W",
        "someNode",
        "someMvNode",
        "10 -> 0.4",
        1,
        1,
        None
      ),
      new Transformer2WInput(
        UUID.randomUUID(),
        "someTransformer2W",
        OperatorInput.NO_OPERATOR_ASSIGNED,
        OperationTime.notLimited(),
        getNodePair("someMvNode").result,
        getNodePair("someNode").result,
        1,
        getTransformer2WTypePair("10 -> 0.4").result,
        1,
        true
      )
    )
  )

  def getTransformer2WPair(
      key: String
  ): ConversionPair[Transformer2W, Transformer2WInput] = {
    transformers2w.getOrElse(
      key,
      throw TestException(
        s"Cannot find input/result pair for ${Transformer2W.getClass.getSimpleName} with key: $key "
      )
    )
  }

  val switches = Map(
    "someSwitch" -> ConversionPair(
      Switch(
        "someSwitch",
        getNodePair("someNode").input.id,
        getNodePair("someSlackNode").input.id,
        1
      ),
      new SwitchInput(
        UUID.randomUUID(),
        "someSwitch",
        getNodePair("someNode").result,
        getNodePair("someSlackNode").result,
        true
      )
    )
  )

  def getSwitches(key: String): ConversionPair[Switch, SwitchInput] = {
    switches.getOrElse(
      key,
      throw TestException(
        s"Cannot find input/result pair for ${Switch.getClass.getSimpleName} with key: $key "
      )
    )
  }

  def main(args: Array[String]) = {
    println("hi")
  }

}
