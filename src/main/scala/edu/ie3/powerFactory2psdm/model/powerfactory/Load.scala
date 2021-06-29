package edu.ie3.powerFactory2psdm.model.powerfactory

import edu.ie3.powerFactory2psdm.model.powerfactory.RawGridModel.Loads

final case class Load(
                     id: String,
                     s: Double,
                     p: Double,
                     q: Double,
                     cosphi: Double
                     ) extends EntityModel

object Load{
  def build(input: Loads)= {

  }
}

