/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.powerFactory2psdm.util

import edu.ie3.powerFactory2psdm.config.ConversionConfig
import edu.ie3.powerFactory2psdm.config.ConversionConfig.ParameterSamplingMethod
import org.apache.commons.math3.distribution.NormalDistribution

object RandomSampler {

  def sample(parameterSamplingMethod: ParameterSamplingMethod): Double =
    parameterSamplingMethod match {
      case ConversionConfig.Fixed(value) => value
      case ConversionConfig.UniformDistribution(lowerBound, upperBound) =>
        scala.util.Random.between(lowerBound, upperBound)
      case ConversionConfig.NormalDistribution(mean, standardDeviation) =>
        new NormalDistribution(mean, standardDeviation).sample()
    }

}
