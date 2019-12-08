package processor.units.branchPredictor

import processor.{ExecutionResult, ProgramCounter}

abstract class BranchPredictor {

  protected var predictions: Map[ProgramCounter, ProgramCounter] = Map()
  protected var correct: Int = 0
  protected var incorrect: Int = 0

  def predict(): ProgramCounter

  def ingest(instruction: String, programCounter: ProgramCounter): Unit

  def wasCorrect(executionResult: ExecutionResult): Boolean = {
    if(!this.predictions.contains(executionResult.getPC)) return false
    val result = executionResult.getResult == this.predictions(executionResult.getPC)
    if(result) correct += 1
    else incorrect += 1
    result
  }

  def print(): Unit = {
    println("Predictions:")
    println(predictions)
  }

  def printStatistics(): Unit = {
    println("-- Branch Predictor --")
    println(s"Correct Guesses: $correct")
    println(s"Incorrect Guesses: $incorrect")
    val percentage: Double = (correct/(correct + incorrect+0.0001))*100 //Prevent division by zero
    println(f"Percentage Correct: $percentage%2.1f")
  }

}
