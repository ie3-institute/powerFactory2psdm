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
  SOLAR_HEIGHT
}
import edu.ie3.datamodel.models.input.connector.`type`.{
  LineTypeInput,
  Transformer2WTypeInput
}
import edu.ie3.datamodel.models.input.connector.`type`.LineTypeInput
import edu.ie3.datamodel.models.input.system.`type`.{
  SystemParticipantTypeInput,
  WecTypeInput
}
import edu.ie3.datamodel.models.input.system.FixedFeedInInput
import edu.ie3.datamodel.models.input.system.{PvInput, WecInput}
import edu.ie3.datamodel.models.input.system.characteristic.{
  CosPhiFixed,
  ReactivePowerCharacteristic,
  WecCharacteristicInput
}
import edu.ie3.datamodel.models.input.{NodeInput, OperatorInput}
import edu.ie3.datamodel.models.voltagelevels.GermanVoltageLevelUtils.LV
import edu.ie3.datamodel.models.{OperationTime, StandardUnits, UniqueEntity}
import edu.ie3.powerFactory2psdm.config.ConversionConfig
import edu.ie3.powerFactory2psdm.config.ConversionConfigUtils.{
  DependentQCharacteristic,
  FixedQCharacteristic
}
import edu.ie3.powerFactory2psdm.config.model.PvConversionConfig.PvModelGeneration
import edu.ie3.powerFactory2psdm.config.model.WecConversionConfig.WecModelGeneration
import edu.ie3.powerFactory2psdm.exception.io.GridParsingException
import edu.ie3.powerFactory2psdm.exception.pf.TestException
import edu.ie3.powerFactory2psdm.generator.ParameterSamplingMethod.{
  Fixed,
  UniformDistribution
}
import edu.ie3.powerFactory2psdm.io.PfGridParser
import edu.ie3.powerFactory2psdm.model.entity.{
  ConnectedElement,
  EntityModel,
  Node,
  StaticGenerator,
  Subnet
}
import edu.ie3.powerFactory2psdm.model.entity.types.{
  LineType,
  TransformerType2W
}
import edu.ie3.powerFactory2psdm.model.PreprocessedPfGridModel
import edu.ie3.util.quantities.PowerSystemUnits
import edu.ie3.util.quantities.PowerSystemUnits.{MEGAVOLTAMPERE, PU}
import org.locationtech.jts.geom.{Coordinate, GeometryFactory}
import pureconfig.ConfigSource
import tech.units.indriya.unit.Units.{OHM, PERCENT, SIEMENS}
import edu.ie3.util.quantities.PowerSystemUnits.{
  DEGREE_GEOM,
  KILOVOLT,
  VOLTAMPERE
}
import pureconfig.generic.auto._
import tech.units.indriya.quantity.Quantities

import java.io.File
import java.util.UUID
import javax.measure.MetricPrefix

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

  val generatePvs: Map[String, ConversionPair[StaticGenerator, PvInput]] = Map(
    "somePvPlant" -> ConversionPair(
      staticGenerator.copy(category = "Fotovoltaik"),
      new PvInput(
        UUID.randomUUID(),
        "someStatGen",
        getNodePair("someNode").result,
        new CosPhiFixed("cosPhiFixed:{(0.0, 0.91)}"),
        0.2,
        Quantities.getQuantity(0, AZIMUTH),
        Quantities.getQuantity(95, EFFICIENCY),
        Quantities.getQuantity(35, SOLAR_HEIGHT),
        1d,
        0.9,
        false,
        Quantities.getQuantity(11, MEGAVOLTAMPERE),
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
        Quantities.getQuantity(11d, MEGAVOLTAMPERE),
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
      Quantities.getQuantity(100, StandardUnits.CAPEX),
      Quantities.getQuantity(50, StandardUnits.ENERGY_PRICE),
      Quantities.getQuantity(11, PowerSystemUnits.MEGAVOLTAMPERE),
      0.91,
      new WecCharacteristicInput("cP:{(10.00,0.05),(15.00,0.10),(20.00,0.20)}"),
      Quantities.getQuantity(96, StandardUnits.EFFICIENCY),
      Quantities.getQuantity(45, StandardUnits.ROTOR_AREA),
      Quantities.getQuantity(200, StandardUnits.HUB_HEIGHT)
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
      staticGenerator.copy(id = "someWec", category = "Wind"),
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
        Quantities.getQuantity(45.375, MetricPrefix.MILLI(OHM)),
        Quantities.getQuantity(15.1249319, OHM),
        Quantities.getQuantity(40d, MetricPrefix.MEGA(VOLTAMPERE)),
        Quantities.getQuantity(110d, KILOVOLT),
        Quantities.getQuantity(10d, KILOVOLT),
        Quantities.getQuantity(826.4462809, MetricPrefix.NANO(SIEMENS)),
        Quantities
          .getQuantity(33047.519046, MetricPrefix.NANO(SIEMENS))
          .to(MetricPrefix.NANO(SIEMENS)),
        Quantities.getQuantity(2.5, PERCENT),
        Quantities.getQuantity(5d, DEGREE_GEOM),
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
