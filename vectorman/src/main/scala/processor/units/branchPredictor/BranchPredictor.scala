package processor.units.branchPredictor

import processor.{ExecutionResult, ProgramCounter, logger}

abstract class BranchPredictor {

  protected var predictions: Map[ProgramCounter, ProgramCounter] = Map()
  protected var branchTargets: Map[ProgramCounter, ProgramCounter] = Map()
  protected var lastSeen: (String, ProgramCounter) = ("", -1)
  protected var correct: Int = 0
  protected var incorrect: Int = 0
  protected val branches: List[String] = List("BRA", "JMP", "BLE", "BEQ")


  protected def isBranch(string: String): Boolean = {
    this.branches foreach {
      branch => if (string.contains(branch)) return true
    }
    false
  }

  protected def predictTaken(string: String): Boolean

  def predict(): ProgramCounter = {
    var prediction = lastSeen._2 + 1
    if(lastSeen._1.contains("END")) return lastSeen._2
    val instruction = lastSeen._1
    if (isBranch(instruction)) {
      if (predictTaken(instruction)) {
        if (branchTargets.contains(lastSeen._2)) {
          prediction = branchTargets(lastSeen._2)
        }
      }
    }
    this.predictions += (this.lastSeen._2 -> prediction)
    logger.debug(s"Predicting: $prediction")
    prediction
  }

  def ingest(instruction: String, programCounter: ProgramCounter): Unit = {
    lastSeen = (instruction, programCounter)
    logger.debug(s"Ingested $lastSeen")
  }

  def wasCorrect(executionResult: ExecutionResult): Boolean = {
    if (!this.predictions.contains(executionResult.getPC)) return false
    val result = executionResult.getResult == this.predictions(executionResult.getPC)
    if (result) correct += 1
    else incorrect += 1
    if (executionResult.hasResult) {
      branchTargets += (executionResult.getPC -> executionResult.getResult)
    } else {
      branchTargets += (executionResult.getPC -> (executionResult.getPC + 1))
    }
    result
  }

  def print(): Unit = {
    println("-- Predictions --")
    println(predictions)
  }

  def printStatistics(): Unit = {
    println("-- Branch Predictor --")
    println(s"Correct Guesses: $correct")
    println(s"Incorrect Guesses: $incorrect")
    val percentage: Double = (correct / (correct + incorrect + 0.000001)) * 100 //Prevent division by zero
    println(f"Percentage Correct: $percentage%2.1f")
  }

}
