package debugger

import processor.{InstructionMemory, Pipeline}

import scala.io.StdIn

class Debugger(instructionMemory: InstructionMemory) {
  var pipeline = new Pipeline(instructionMemory)
  def debug(): Unit = {
    print(Assets.banner)
    println(Assets.subheading)
    while(true){
      val keypress = StdIn.readChar()
      if(keypress == 'r'){
        pipeline.run()
        println(" -- Finished --")
        pipeline = new Pipeline(instructionMemory)
      }
      if(keypress == 's'){
        if(pipeline.state.getPc >= 0) pipeline.tick()
        else {
          println("-- Finished --")
          pipeline = new Pipeline(instructionMemory)
        }
      }
      if(keypress == 'v'){
        pipeline.toggleVerbose()
      }
      if(keypress == 'p'){
        pipeline.state.printRegisters()
      }
      if(keypress == 'h'){
        print(Assets.help)
      }
      if(keypress == 'x'){
        return
      }
    }
  }
}
