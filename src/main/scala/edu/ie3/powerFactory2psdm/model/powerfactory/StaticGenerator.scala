package edu.ie3.powerFactory2psdm.model.powerfactory

import edu.ie3.powerFactory2psdm.model.powerfactory.RawGridModel.StatGen

final case class StaticGenerator(id: String) extends EntityModel {

  def build(input: StatGen) = ???

}
