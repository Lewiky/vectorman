package processor.units.branchPredictor

class AlwaysNotTakenPredictor extends BranchPredictor {

  override protected def predictTaken(string: String): Boolean = false
}
