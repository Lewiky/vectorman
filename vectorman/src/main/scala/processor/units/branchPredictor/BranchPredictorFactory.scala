package processor.units.branchPredictor


object branchPredictorType extends Enumeration {
  val alwaysNotTaken, static, dynamic, neural = Value
}

object BranchPredictorFactory {
  def create(bp_type: branchPredictorType.Value): BranchPredictor = {
    bp_type match {
      case branchPredictorType.static => new StaticPredictor()
      case branchPredictorType.alwaysNotTaken => new AlwaysNotTakenPredictor()
      case branchPredictorType.dynamic => new DynamicPredictor()
      case branchPredictorType.neural => new NeuralPredictor()
    }
  }

}
