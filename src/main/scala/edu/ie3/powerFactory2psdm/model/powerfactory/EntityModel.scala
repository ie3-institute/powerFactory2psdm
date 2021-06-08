/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.model.powerfactory

/**
 * Common Entity parameters
 */
trait EntityModel {

  /**
   * Id of the entity
   */
  val id: String
}

object EntityModel {

  private var assignedIds: Set[String] = Set()

  /**
   * Checks if id is already assigned and adds it to assignedIds
   *
   * @param id
   * @return
   */
  def isUniqueId(id: String): Boolean ={
    if (getAssignedIds.contains(id)) return false
    addId(id)
    true
  }

  def getAssignedIds: Set[String] = assignedIds

  private def addId(id: String): Unit = {
    assignedIds += id
    println("")
  }

}
