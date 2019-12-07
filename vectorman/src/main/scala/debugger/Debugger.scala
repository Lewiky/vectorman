package debugger

import processor.{InstructionMemory, Pipeline}

import scala.io.StdIn

class Debugger(instructionMemory: InstructionMemory, var instructionsPerCycle: Int = 5, var executeUnits: Int = 2) {
  var pipeline = new Pipeline(instructionMemory, instructionsPerCycle, executeUnits)

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
        pipeline = new Pipeline(instructionMemory, instructionsPerCycle, executeUnits)
      }
      if (keypress == 's') {
        if (pipeline.state.getPc >= 0) pipeline.tick()
        else {
          println("-- Finished --")
          pipeline = new Pipeline(instructionMemory, instructionsPerCycle, executeUnits)
        }
      }
      if (keypress == 'v') {
        pipeline.toggleVerbose()
      }
      if (keypress == 'p') {
        pipeline.state.printRegisters()
        pipeline.decoder.reservationStation.print()
        pipeline.state.printScoreboard()
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
        executeUnits = input(2).asDigit
        pipeline = new Pipeline(instructionMemory, instructionsPerCycle, executeUnits)
        println(s"Executing with $executeUnits units")
      }
      last = keypress
    }
  }
}
