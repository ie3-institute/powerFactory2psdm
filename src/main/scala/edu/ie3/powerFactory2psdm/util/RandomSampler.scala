/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.util

import edu.ie3.powerFactory2psdm.generator.ParameterSamplingMethod
import edu.ie3.powerFactory2psdm.generator.ParameterSamplingMethod.{
  Fixed,
  NormalDistribution,
  UniformDistribution
}
import org.apache.commons.math3.distribution.{
  NormalDistribution => MathNormalDistribution
}

object RandomSampler {

  def sample(generationMethod: ParameterSamplingMethod): Double =
    generationMethod match {
      case Fixed(value) => value
      case UniformDistribution(lowerBound, upperBound) =>
        scala.util.Random.between(lowerBound, upperBound)
      case NormalDistribution(mean, standardDeviation) =>
        new MathNormalDistribution(mean, standardDeviation).sample()
    }

}
