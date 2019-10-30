package processor.units

abstract class EUnit[A, B] {
  var input: Option[A]
  var output: Option[B]

  def tick(): Unit
}
