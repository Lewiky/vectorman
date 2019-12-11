package debugger

import processor.units.branchPredictor.branchPredictorType
import processor.units.branchPredictor.branchPredictorType._
import processor.{InstructionMemory, Pipeline}

import scala.io.StdIn

class Debugger(instructionMemory: InstructionMemory,
               userMemory: Option[InstructionMemory] = None,
               var instructionsPerCycle: Int = 5,
               var executeUnits: Int = 2,
               var branchPredictor: branchPredictorType.Value = dynamic) {
  var pipeline = new Pipeline(instructionMemory, userMemory, instructionsPerCycle, executeUnits, branchPredictor)
  private var renamingEnabled: Boolean = false

  private def buildPipeline(executeUnits: Int = this.executeUnits): Pipeline = {
    this.executeUnits = executeUnits
    this.instructionsPerCycle = Math.max(5, executeUnits)
    val x = new Pipeline(instructionMemory, userMemory, instructionsPerCycle, executeUnits, branchPredictor)
    //x.decoder.enableRenaming = renamingEnabled
    x
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
        pipeline.reorderBuffer.show()
        //pipeline.decoder.registerRenameUnit.print()
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
        pipeline = buildPipeline(executeUnits = input(2).asDigit)
        println(s"Executing with $executeUnits units")
      }
      if (keypress == 'b') {
        val number = input(2).asDigit
        this.branchPredictor = number match {
          case 0 => alwaysNotTaken
          case 1 => static
          case 2 => dynamic
          case 3 => neural
          case _ => alwaysNotTaken
        }
        println(s"Branch Predictor switched to: $branchPredictor")
        pipeline = buildPipeline()
      }
      if (keypress == 'z') {
        renamingEnabled = !renamingEnabled
        println(s"Renaming: $renamingEnabled")
        pipeline = buildPipeline()
      }
      last = keypress
    }
  }
}
