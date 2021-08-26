package edu.ie3.powerFactory2psdm.config

object ConversionConfigUtils {

  trait ConversionMode

  /** Groups different sources for certain parameters of power factory models
   */
  sealed trait ParameterSource

  /** Take values from the load flow (Lastfluss) specification of the model
   */
  final case object LoadFlowSource extends ParameterSource

  /** Take values from the basic data (Basisdaten) specification of the model
   */
  final case object BasicDataSource extends ParameterSource

  /** Trait to group QCharacteristic (reactive power characteristic)
   */
  sealed trait QCharacteristic

  /** Use the cosinus phi power factor of the model to establish a fixed
   * QCharacteristic
   */
  final case object FixedQCharacteristic extends QCharacteristic

  /** Dependent power characteristic dependent on either power or nodal voltage
   * magnitude.
   *
   * @param characteristic
   *   to follow
   * @see
   *   See
   *   [[https://powersystemdatamodel.readthedocs.io/en/latest/models/input/participant/general.html?highlight=reactive#reactive-power-characteristics PowerSystemDataModel]]
   *   for details and how the [[characteristic]] string has to look like.
   */
  final case class DependentQCharacteristic(characteristic: String)
    extends QCharacteristic
}
