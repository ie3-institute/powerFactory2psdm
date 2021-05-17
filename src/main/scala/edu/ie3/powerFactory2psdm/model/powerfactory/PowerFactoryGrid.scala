package edu.ie3.powerFactory2psdm.model.powerfactory
import edu.ie3.powerFactory2psdm.model.powerfactory.PowerFactoryGrid.{
  ExtGrid,
  PowerPlants,
  Trafos3w,
  Loads,
  Lines,
  Pvs,
  Switches,
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
    switches: Option[List[Switches]],
    loadsLV: Option[List[LoadsLV]],
    statGen: Option[List[StatGen]],
    loads: Option[List[Loads]],
    trafos3w: Option[List[Trafos3w]],
    extGrid: Option[List[ExtGrid]],
    trafoTypes2w: Option[List[TrafoTypes2w]],
    lines: Option[List[Lines]]
)

object PowerFactoryGrid {

  final case class Switches(
      id: Option[String],
      conElms: Option[List[Option[ConElms]]]
  )

  final case class Pvs()

  final case class ConElms(id: Option[String], pfCls: Option[String])

  final case class Loads(id: Option[String])

  final case class TrafoTypes2w(id: Option[String])

  final case class LineTypes(id: Option[String])

  final case class StatGen()

  final case class Lines(
      id: Option[String],
      loc_name: Option[String],
      conElms: Option[List[Option[ConElms]]]
  )

  final case class PowerPlants(id: Option[String])

  final case class Trafos3w()

  final case class ExtGrid(id: Option[String])

  final case class LoadsLV()

  final case class Nodes(
      vtarget: Option[Double],
      iUsage: Option[Double],
      cpZone: Option[String],
      conElms: Option[List[Option[ConElms]]],
      GPSlat: Option[Double],
      id: Option[String],
      GPSlon: Option[Double],
      cpArea: Option[String]
  )

  final case class Trafos2w(
      id: Option[String],
      conElms: Option[List[Option[ConElms]]]
  )

  final case class TrafoTypes3w()

  final case class LoadsMV()

}
