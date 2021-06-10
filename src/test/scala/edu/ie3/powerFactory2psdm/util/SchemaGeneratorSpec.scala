/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.util

import org.scalatest.PrivateMethodTester
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class SchemaGeneratorSpec
    extends Matchers
    with AnyWordSpecLike
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
           |  final case class Nodes()
           |
           |  final case class Lines()
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

  "generate a valid class if we have multiple nested complex fields that are nested in several objects" in {

    val json =
      """
        |{
        | "lines": [
        |    {
        |      "conElms": [
        |        {
        |          "loc_name": "Klemmleiste(1)",
        |          "pfCls": "ElmTerm"
        |        },
        |        {
        |          "loc_name": "Klemmleiste",
        |          "pfCls": "ElmTerm"
        |        }
        |      ]
        |    }
        |  ],
        |  "trafos3w": [
        |    {
        |      "conElms": [
        |        {
        |          "loc_name": "Klemmleiste OS",
        |          "pfCls": "ElmTerm"
        |        },
        |        {
        |          "loc_name": "Klemmleiste MS",
        |          "pfCls": "ElmTerm"
        |        },
        |        {
        |          "loc_name": "Klemmleiste MS2",
        |          "pfCls": "ElmTerm"
        |        }
        |      ]
        |    }
        | ]
        |}
        |""".stripMargin

    genCode(json) shouldBe Some(
      """|package edu.ie3.powerFactory2psdm.model.powerfactory
         |import edu.ie3.powerFactory2psdm.model.powerfactory.PowerFactoryGrid.{
         |  Lines,
         |  Trafos3w
         |}
         |
         |final case class PowerFactoryGrid(
         |    lines: Option[List[Lines]],
         |    trafos3w: Option[List[Trafos3w]]
         |)
         |
         |object PowerFactoryGrid {
         |
         |  final case class Trafos3w(conElms: Option[List[Option[ConElms]]])
         |
         |  final case class ConElms(loc_name: Option[String], pfCls: Option[String])
         |
         |  final case class Lines(conElms: Option[List[Option[ConElms]]])
         |
         |}
         |""".stripMargin
    )

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
         |  final case class CpZone(
         |      loc_name: Option[String],
         |      additionalParam: Option[String],
         |      leet: Option[List[Option[Leet]]]
         |  )
         |
         |  final case class Nodes(
         |      loc_name: Option[String],
         |      root_id: Option[String],
         |      AccessTime: Option[Double],
         |      GPSlon: Option[Double],
         |      nestedArray: Option[List[Option[List[Option[String]]]]],
         |      cpZone: Option[List[Option[CpZone]]],
         |      vmin: Option[Double],
         |      GPSlat: Option[Double],
         |      foo: Option[Foo],
         |      cpSubstat: Option[String],
         |      Vtarget: Option[Double],
         |      uknom: Option[Double],
         |      bar: Option[Bar],
         |      vtarget: Option[Double],
         |      vmax: Option[Double]
         |  )
         |
         |  final case class Leet(
         |      loc_name: Option[String],
         |      additionalParam: Option[String]
         |  )
         |
         |  final case class Bar(a: Option[String], b: Option[String])
         |
         |  final case class Lines(length: Option[Double])
         |
         |  final case class Foo()
         |
         |}
         |""".stripMargin
    )

  }

  "generate valid GPSCoords for simple case" in {
    val json =
      """
        |{
        |  "lines": [
        |    {
        |      "id": "Grid.ElmNet\\Line_0001_0002/1.ElmLne",
        |      "GPScoords": [
        |        [
        |          51.4843281,
        |          7.4116482
        |        ],
        |        [
        |          52.2895,
        |          12.8273
        |        ],
        |        [
        |          52.4895,
        |          13.8273
        |        ]
        |      ],
        |      "bus1Id": "Grid.ElmNet\\Bus_0001.ElmTerm",
        |      "bus2Id": "Grid.ElmNet\\Bus_0002.ElmTerm"
        |    }
        |  ]
        |}""".stripMargin

    genCode(json) shouldBe Some(
      """package edu.ie3.powerFactory2psdm.model.powerfactory
        |import edu.ie3.powerFactory2psdm.model.powerfactory.PowerFactoryGrid.{Lines}
        |
        |final case class PowerFactoryGrid(
        |    lines: Option[List[Lines]]
        |)
        |
        |object PowerFactoryGrid {
        |
        |  final case class Lines(
        |      id: Option[String],
        |      GPScoords: Option[List[Option[List[Option[Double]]]]],
        |      bus1Id: Option[String],
        |      bus2Id: Option[String]
        |  )
        |
        |}
        |""".stripMargin
    )
  }

  "generate valid GPSCoords if field values differ" in {
    val json =
      """
        |{
        |  "lines": [
        |    {
        |      "id": "Grid.ElmNet\\Line_0009_0014.ElmLne",
        |      "GPScoords": [
        |        []
        |      ],
        |      "bus1Id": "Grid.ElmNet\\Bus_0014.ElmTerm",
        |      "bus2Id": "Grid.ElmNet\\Bus_0009.ElmTerm"
        |    },
        |    {
        |      "id": "Grid.ElmNet\\Line_0001_0002/1.ElmLne",
        |      "GPScoords": [
        |        [
        |          51.4843281,
        |          7.4116482
        |        ],
        |        [
        |          52.2895,
        |          12.8273
        |        ],
        |        [
        |          52.4895,
        |          13.8273
        |        ]
        |      ],
        |      "bus1Id": "Grid.ElmNet\\Bus_0001.ElmTerm",
        |      "bus2Id": "Grid.ElmNet\\Bus_0002.ElmTerm"
        |    },
        |     {
        |      "id": "Grid.ElmNet\\Line_0009_0014.ElmLne",
        |      "GPScoords": [
        |        []
        |      ],
        |      "bus1Id": "Grid.ElmNet\\Bus_0014.ElmTerm",
        |      "bus2Id": "Grid.ElmNet\\Bus_0009.ElmTerm"
        |    }
        |  ]
        |}""".stripMargin

    genCode(json) shouldBe Some(
      """package edu.ie3.powerFactory2psdm.model.powerfactory
        |import edu.ie3.powerFactory2psdm.model.powerfactory.PowerFactoryGrid.{Lines}
        |
        |final case class PowerFactoryGrid(
        |    lines: Option[List[Lines]]
        |)
        |
        |object PowerFactoryGrid {
        |
        |  final case class Lines(
        |      id: Option[String],
        |      GPScoords: Option[List[Option[List[Option[Double]]]]],
        |      bus1Id: Option[String],
        |      bus2Id: Option[String]
        |  )
        |
        |}
        |""".stripMargin
    )
  }

}
