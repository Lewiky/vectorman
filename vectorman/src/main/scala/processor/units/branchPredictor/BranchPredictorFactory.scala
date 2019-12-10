package processor.units.branchPredictor


object branchPredictorType extends Enumeration {
  val alwaysNotTaken, static = Value
}

object BranchPredictorFactory {
  def create(bp_type: branchPredictorType.Value): BranchPredictor = {
    bp_type match {
      case branchPredictorType.static => new StaticPredictor()
      case branchPredictorType.alwaysNotTaken => new AlwaysNotTakenPredictor()
    }
  }

}
