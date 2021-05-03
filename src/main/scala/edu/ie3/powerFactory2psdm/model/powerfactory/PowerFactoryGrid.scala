/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.model.powerfactory
import edu.ie3.powerFactory2psdm.model.powerfactory.PowerFactoryGrid.{
  ExtGrid,
  PowerPlants,
  Trafos3w,
  Loads,
  Lines,
  Pvs,
  TrafoTypes3w,
  LoadsLV,
  LoadsMV,
  Nodes,
  Trafos2w,
  StatGen,
  LineTypes,
  TrafoTypes2w
}

final case class PowerFactoryGrid(
    trafos2w: Option[List[Trafos2w]],
    loadsMV: Option[List[LoadsMV]],
    nodes: Option[List[Nodes]],
    powerPlants: Option[List[PowerPlants]],
    trafoTypes3w: Option[List[TrafoTypes3w]],
    pvs: Option[List[Pvs]],
    lineTypes: Option[List[LineTypes]],
    loadsLV: Option[List[LoadsLV]],
    statGen: Option[List[StatGen]],
    loads: Option[List[Loads]],
    trafos3w: Option[List[Trafos3w]],
    extGrid: Option[List[ExtGrid]],
    trafoTypes2w: Option[List[TrafoTypes2w]],
    lines: Option[List[Lines]]
)

object PowerFactoryGrid {

  final case class Pvs()

  final case class Loads()

  final case class TrafoTypes2w()

  final case class LineTypes()

  final case class StatGen()

  final case class ConElms(loc_name: Option[String], pfCls: Option[String])

  final case class Lines(
      loc_name: Option[String],
      conElms: Option[List[Option[ConElms]]]
  )

  final case class PowerPlants()

  final case class Trafos3w(conElms: Option[List[Option[ConElms]]])

  final case class ExtGrid()

  final case class LoadsLV()

  final case class CpZone(loc_name: Option[String])

  final case class CpArea(loc_name: Option[String])

  final case class Nodes(
      vtarget: Option[Double],
      cpZone: Option[List[Option[CpZone]]],
      conElms: Option[List[Option[ConElms]]],
      GPSlat: Option[Double],
      GPSlon: Option[Double],
      cpArea: Option[List[Option[CpArea]]],
      loc_name: Option[String]
  )

  final case class Trafos2w(
      loc_name: Option[String],
      conElms: Option[List[Option[ConElms]]]
  )

  final case class TrafoTypes3w()

  final case class LoadsMV()

}
