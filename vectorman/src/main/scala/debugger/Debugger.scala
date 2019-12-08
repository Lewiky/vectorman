package debugger

import processor.units.branchPredictor.{AlwaysNotTakenPredictor, BranchPredictor}
import processor.{InstructionMemory, Pipeline}

import scala.io.StdIn

class Debugger(instructionMemory: InstructionMemory,
               var instructionsPerCycle: Int = 5,
               var executeUnits: Int = 2,
               var branchPredictor: BranchPredictor = new AlwaysNotTakenPredictor) {
  var pipeline = new Pipeline(instructionMemory, instructionsPerCycle, executeUnits, branchPredictor)

  private def buildPipeline(executeUnits: Int = this.executeUnits): Pipeline = {
    this.executeUnits = executeUnits
    new Pipeline(instructionMemory, instructionsPerCycle, executeUnits, branchPredictor)
  }

  def debug(): Unit = {
    print(Assets.banner)
    println(Assets.subheading)
    println(Assets.subsubheading)
    var last = 's'
    while (true) {
      var keypress = last
      val input = StdIn.readLine()
      if (!input.isEmpty) keypress = input(0)
      if (keypress == 'r') {
        pipeline.run()
        println(" -- Finished --")
        pipeline = buildPipeline()
      }
      if (keypress == 's') {
        if (pipeline.state.getPc >= 0) pipeline.tick()
        else {
          println("-- Finished --")
          pipeline = buildPipeline()
        }
      }
      if (keypress == 'v') {
        pipeline.toggleVerbose()
      }
      if (keypress == 'p') {
        pipeline.state.printRegisters()
        pipeline.decoder.reservationStation.print()
        pipeline.state.printScoreboard()
        pipeline.branchPredictor.print()
      }
      if (keypress == 'h') {
        print(Assets.help)
      }
      if (keypress == 'x') {
        return
      }
      if (keypress == 'i') {
        pipeline.printStatistics()
      }
      if (keypress == 'u') {
        pipeline = buildPipeline(executeUnits=input(2).asDigit)
        println(s"Executing with $executeUnits units")
      }
      last = keypress
    }
  }
}
