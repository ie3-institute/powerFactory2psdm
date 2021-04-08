/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.util

import org.scalatest.{Matchers, PrivateMethodTester, WordSpecLike}

class SchemaGeneratorSpec
    extends Matchers
    with WordSpecLike
    with PrivateMethodTester {

  private val className = "PowerFactoryGrid"
  private val packageName = "edu.ie3.powerFactory2psdm.model.powerfactory"

  private def genCode(json: String): Option[String] =
    SchemaGenerator.run(json, className, packageName)

  "A SchemaGenerator" should {

    "not generate code if the json object is empty" in {

      val json =
        """
          | {}
          |""".stripMargin

      genCode(json) shouldBe None
    }

    "generate a valid class for an empty top level collection" in {

      val json =
        """
          |{
          |  "lines": []
          |}
          |""".stripMargin

      genCode(json) shouldBe Some(
        """|package edu.ie3.powerFactory2psdm.model.powerfactory
           |import edu.ie3.powerFactory2psdm.model.powerfactory.PowerFactoryGrid.{Lines}
           |
           |final case class PowerFactoryGrid(
           |    lines: Option[List[Lines]]
           |)
           |
           |object PowerFactoryGrid {
           |
           |  final case class Lines()
           |
           |}
           |""".stripMargin
      )

    }

    "generate a valid class for multiple empty top level collections" in {
      val json =
        """
          |{
          |  "lines": [],
          |  "nodes": []
          |}
          |""".stripMargin

      genCode(json) shouldBe Some(
        """|package edu.ie3.powerFactory2psdm.model.powerfactory
           |import edu.ie3.powerFactory2psdm.model.powerfactory.PowerFactoryGrid.{
           |  Lines,
           |  Nodes
           |}
           |
           |final case class PowerFactoryGrid(
           |    lines: Option[List[Lines]],
           |    nodes: Option[List[Nodes]]
           |)
           |
           |object PowerFactoryGrid {
           |
           |  final case class Lines()
           |
           |  final case class Nodes()
           |
           |}
           |""".stripMargin
      )
    }

    "generate a valid class for a top level collection with one simple attribute" in {

      val json =
        """
          |{
          |  "lines": [
          |    {
          |      "length": 1
          |    }
          |  ]
          |}
          |""".stripMargin

      genCode(json) shouldBe Some(
        """|package edu.ie3.powerFactory2psdm.model.powerfactory
           |import edu.ie3.powerFactory2psdm.model.powerfactory.PowerFactoryGrid.{Lines}
           |
           |final case class PowerFactoryGrid(
           |    lines: Option[List[Lines]]
           |)
           |
           |object PowerFactoryGrid {
           |
           |  final case class Lines(length: Option[Double])
           |
           |}
           |""".stripMargin
      )

    }

    "generate a valid class for a top level collection with multiple simple attribute" in {

      val json =
        """
          |{
          |  "lines": [
          |    {
          |      "length": 1,
          |      "AccessTime": 0.0,
          |      "GPSlat": 0.0,
          |      "GPSlon": 0.0,
          |      "Vtarget": 11.0,
          |      "cpSubstat": null
          |    }
          |  ]
          |}
          |""".stripMargin

      genCode(json) shouldBe Some(
        """|package edu.ie3.powerFactory2psdm.model.powerfactory
           |import edu.ie3.powerFactory2psdm.model.powerfactory.PowerFactoryGrid.{Lines}
           |
           |final case class PowerFactoryGrid(
           |    lines: Option[List[Lines]]
           |)
           |
           |object PowerFactoryGrid {
           |
           |  final case class Lines(
           |      AccessTime: Option[Double],
           |      GPSlat: Option[Double],
           |      GPSlon: Option[Double],
           |      length: Option[Double],
           |      cpSubstat: Option[String],
           |      Vtarget: Option[Double]
           |  )
           |
           |}
           |""".stripMargin
      )

    }

    "generate a valid class for a top level collection with nested class fields" in {

      val json =
        """
          |{
          |  "lines": [
          |    {
          |       "obj": {
          |        "a": "x",
          |        "b": "y"
          |      }
          |    }
          |  ]
          |}
          |""".stripMargin

      genCode(json) shouldBe Some(
        """|package edu.ie3.powerFactory2psdm.model.powerfactory
           |import edu.ie3.powerFactory2psdm.model.powerfactory.PowerFactoryGrid.{Lines}
           |
           |final case class PowerFactoryGrid(
           |    lines: Option[List[Lines]]
           |)
           |
           |object PowerFactoryGrid {
           |
           |  final case class Obj(a: Option[String], b: Option[String])
           |
           |  final case class Lines(obj: Option[Obj])
           |
           |}
           |""".stripMargin
      )

    }
  }

  "generate a valid class for a complex json" in {

    val json =
      """
        |{
        |  "nodes": [
        |    {
        |      "AccessTime": 0.0,
        |      "GPSlat": 0.0,
        |      "GPSlon": 0.0,
        |      "Vtarget": 11.0,
        |      "cpSubstat": null,
        |      "bar": {
        |        "a": "asd",
        |        "b": "ass"
        |      },
        |      "foo": {
        |      },
        |      "cpZone": [
        |        {
        |          "loc_name": "myZone",
        |          "additionalParam": "myParam",
        |          "leet": [
        |            {
        |              "loc_name": "myZone",
        |              "additionalParam": "myParam"
        |            }
        |          ]
        |        },
        |        {
        |          "loc_name": "myZone",
        |          "additionalParam": "myParam",
        |          "leet": [
        |            {
        |              "loc_name": "myZone",
        |              "additionalParam": "myParam"
        |            }
        |          ]
        |        }
        |      ],
        |      "nestedArray": [
        |        []
        |      ],
        |      "loc_name": "Klemmleiste MS2",
        |      "root_id": null,
        |      "uknom": 11.0,
        |      "vmax": 1.0499999523162842,
        |      "vmin": 0.0,
        |      "vtarget": 1.0
        |    },
        |    {
        |      "AccessTime": 0.0,
        |      "GPSlat": 0.0,
        |      "GPSlon": 0.0,
        |      "Vtarget": 11.0,
        |      "cpSubstat": null,
        |      "cpZone": [
        |        {
        |          "loc_name": "myZone",
        |          "additionalParam": "myParam",
        |          "leet": [
        |            {
        |              "loc_name": "myZone",
        |              "additionalParam": "myParam"
        |            }
        |          ]
        |        },
        |        {
        |          "loc_name": "myZone",
        |          "additionalParam": "myParam",
        |          "leet": [
        |            {
        |              "loc_name": "myZone",
        |              "additionalParam": "myParam"
        |            }
        |          ]
        |        }
        |      ],
        |      "nestedArray": [
        |        []
        |      ],
        |      "loc_name": "Klemmleiste MS2",
        |      "root_id": null,
        |      "uknom": 11.0,
        |      "vmax": 1.0499999523162842,
        |      "vmin": 0.0,
        |      "vtarget": 1.0
        |    }
        |  ],
        |  "lines": [
        |    {
        |      "length": 1
        |    }
        |  ]
        |}
        |""".stripMargin

    genCode(json) shouldBe Some(
      """|package edu.ie3.powerFactory2psdm.model.powerfactory
         |import edu.ie3.powerFactory2psdm.model.powerfactory.PowerFactoryGrid.{
         |  Nodes,
         |  Lines
         |}
         |
         |final case class PowerFactoryGrid(
         |    nodes: Option[List[Nodes]],
         |    lines: Option[List[Lines]]
         |)
         |
         |object PowerFactoryGrid {
         |
         |  final case class Leet(
         |      loc_name: Option[String],
         |      additionalParam: Option[String]
         |  )
         |
         |  final case class CpZone(
         |      loc_name: Option[String],
         |      additionalParam: Option[String],
         |      leet: Option[List[Option[Leet]]]
         |  )
         |
         |  final case class Bar(a: Option[String], b: Option[String])
         |
         |  final case class Foo()
         |
         |  final case class Nodes(
         |      vmin: Option[Double],
         |      root_id: Option[String],
         |      vtarget: Option[Double],
         |      nestedArray: Option[List[Option[List[Option[String]]]]],
         |      cpZone: Option[List[Option[CpZone]]],
         |      vmax: Option[Double],
         |      AccessTime: Option[Double],
         |      GPSlat: Option[Double],
         |      GPSlon: Option[Double],
         |      bar: Option[Bar],
         |      loc_name: Option[String],
         |      cpSubstat: Option[String],
         |      Vtarget: Option[Double],
         |      uknom: Option[Double],
         |      foo: Option[Foo]
         |  )
         |
         |  final case class Lines(length: Option[Double])
         |
         |}
         |""".stripMargin
    )

  }
}
