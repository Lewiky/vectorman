package processor.units.branchPredictor

class StaticPredictor extends BranchPredictor {

  override protected def predictTaken(string: String): Boolean = {
    val unconditionalBranches = List("BRA", "JMP")
    unconditionalBranches.foreach(branch => if (string.contains(branch)) return true)
    //Predict taken if branch target is backwards and not taken if forwards
    if (branchTargets.contains(lastSeen._2)) {
      val difference = branchTargets(lastSeen._2) - lastSeen._2
      return difference < 0
    }
    false
  }
}
