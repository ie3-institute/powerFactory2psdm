package edu.ie3.powerFactory2psdm.model.powerfactory
import edu.ie3.powerFactory2psdm.model.powerfactory.PowerFactoryGrid.{
  Nodes,
  Lines
}

final case class PowerFactoryGrid(
    nodes: Option[List[Nodes]],
    lines: Option[List[Lines]]
)

object PowerFactoryGrid {

  final case class Dieter()

  final case class Peter(
      loc_name: Option[String],
      additionalParam: Option[String]
  )

  final case class Nodes(
      vtarget: Option[Double],
      cpZone: Option[List[Option[CpZone]]],
      GPSlon: Option[Double],
      Vtarget: Option[Double],
      vmax: Option[Double],
      karl: Option[Karl],
      uknom: Option[Double],
      nestedArray: Option[List[Option[List[Option[String]]]]],
      loc_name: Option[String],
      GPSlat: Option[Double],
      root_id: Option[String],
      vmin: Option[Double],
      cpSubstat: Option[String],
      AccessTime: Option[Double],
      dieter: Option[Dieter]
  )

  final case class CpZone(
      loc_name: Option[String],
      additionalParam: Option[String],
      peter: Option[List[Option[Peter]]]
  )

  final case class Lines(length: Option[Double])

  final case class Karl(a: Option[String], b: Option[String])

}
