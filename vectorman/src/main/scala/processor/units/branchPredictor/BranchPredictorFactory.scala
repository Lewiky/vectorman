package processor.units.branchPredictor


object branchPredictorType extends Enumeration {
  val alwaysNotTaken, alwaysTaken = Value
}

object BranchPredictorFactory {
  def create(bp_type: branchPredictorType.Value): BranchPredictor = {
    bp_type match {
      case branchPredictorType.alwaysTaken => new AlwaysNotTakenPredictor()
      case branchPredictorType.alwaysNotTaken => new AlwaysNotTakenPredictor()
    }
  }

}
