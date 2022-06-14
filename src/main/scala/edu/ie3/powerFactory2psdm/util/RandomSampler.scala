/**
 * © 2021. Johannes Hiry,
 **/

package edu.ie3.powerFactory2psdm.util

import edu.ie3.powerFactory2psdm.generator.ParameterSamplingMethod
import edu.ie3.powerFactory2psdm.generator.ParameterSamplingMethod._
import org.apache.commons.math3.distribution.{
  NormalDistribution => GaussianDistribution
}

import scala.util.Random

object RandomSampler {

  private val seed = 42
  private val random = new Random(seed)

  def sample(generationMethod: ParameterSamplingMethod): Double =
    generationMethod match {
      case Fixed(value) => value
      case UniformDistribution(lowerBound, upperBound) =>
        random.between(lowerBound, upperBound)
      case NormalDistribution(mean, standardDeviation) =>
        val distribution = new GaussianDistribution(mean, standardDeviation)
        distribution.reseedRandomGenerator(seed)
        distribution.sample()
    }
}
