package edu.ie3.powerFactory2psdm.generator

sealed trait ParameterSamplingMethod

object ParameterSamplingMethod {

  /** Use the given value fixed value
   *
   * @param value
   *   to be used
   */
  final case class Fixed(
    value: Double
  ) extends ParameterSamplingMethod

  /** Sample a value between [[lowerBound]] and [[upperBound]] from a uniform
   * distribution
   *
   * @param lowerBound
   *   of the distribution
   * @param upperBound
   *   of the distribution
   */
  final case class UniformDistribution(
                                        lowerBound: Double,
                                        upperBound: Double
                                      ) extends ParameterSamplingMethod

  /** Sample a value from a normal distribution
   *
   * @param mean
   *   of the distribution
   * @param standardDeviation
   *   of the distribution
   */
  final case class NormalDistribution(
                                       mean: Double,
                                       standardDeviation: Double
                                     ) extends ParameterSamplingMethod
}


