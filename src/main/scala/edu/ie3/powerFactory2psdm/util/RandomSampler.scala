package edu.ie3.powerFactory2psdm.util

import edu.ie3.powerFactory2psdm.config.ConversionConfig
import edu.ie3.powerFactory2psdm.config.ConversionConfig.GenerationMethod
import org.apache.commons.math3.distribution.NormalDistribution

object RandomSampler {

  def sample(generationMethod: GenerationMethod): Double = generationMethod match {
    case ConversionConfig.Fixed(value) => value
    case ConversionConfig.UniformDistribution(lowerBound, upperBound) =>
      scala.util.Random.between(lowerBound, upperBound)
    case ConversionConfig.NormalDistribution(mean, standardDeviation) =>
      new NormalDistribution(mean, standardDeviation).sample()
  }

}
