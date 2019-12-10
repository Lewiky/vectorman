package processor.units.branchPredictor

import processor.{ProgramCounter, logger}

class StaticPredictor extends BranchPredictor {

  private var lastSeen: (String, ProgramCounter) = ("", -1)

  private def isBranch(string: String): Boolean = {
    this.branches foreach {
      branch => if (string.contains(branch)) return true
    }
    false
  }

  private def predictTaken(string: String): Boolean = {
    val unconditionalBranches = List("BRA", "JMP")
    unconditionalBranches.foreach(branch => if(string.contains(branch)) return true)
    //Predict taken if branch target is backwards and not taken if forwards
    if(branchTargets.contains(lastSeen._2)){
      val difference = branchTargets(lastSeen._2) - lastSeen._2
      return difference < 0
    }
    false
  }

  override def predict(): ProgramCounter = {
    var prediction = lastSeen._2 + 1

    //Do static analysis on previous instruction type
    val instruction = lastSeen._1
    if(isBranch(instruction)){
      if (predictTaken(instruction)) {
        if(branchTargets.contains(lastSeen._2)){
          prediction = branchTargets(lastSeen._2)
        }
      }
    }
    this.predictions += (this.lastSeen._2 -> prediction)
    logger.debug(s"Predicting: $prediction")
    prediction
  }

  override def ingest(instruction: String, programCounter: ProgramCounter): Unit = {
    lastSeen = (instruction, programCounter)
    logger.debug(s"Ingested $lastSeen")
  }
}
