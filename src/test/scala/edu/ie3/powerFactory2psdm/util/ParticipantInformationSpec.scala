/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.util

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

import java.io.File

class ParticipantInformationSpec extends Matchers with AnyWordSpecLike {

  "ParticipantAdjustments" should {

    "be parsed from Json" in {

      val map = ParticipantInformation.fromJson(
        s"${new File(".").getCanonicalPath}/src/test/resources/nodal_participants_info.json"
      )

      map.keySet should contain allOf ("Node-A-ID", "Node-B-ID")
    }
  }

}
