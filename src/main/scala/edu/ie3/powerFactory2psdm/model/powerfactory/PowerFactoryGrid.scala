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

  final case class Nodes(
      vmin: Option[Double],
      root_id: Option[String],
      vtarget: Option[Double],
      additionalParam: Option[List[Option[String]]],
      vmax: Option[Double],
      AccessTime: Option[Double],
      GPSlat: Option[Double],
      GPSlon: Option[Double],
      loc_name: Option[String],
      cpSubstat: Option[String],
      Vtarget: Option[Double],
      uknom: Option[Double]
  )

  final case class Lines(length: Option[Double])

}
