package processor

import processor.units.{Decoder, Executor, Fetcher}

class Pipeline(instructionMemory: InstructionMemory) {

  val state: PipelineState = new PipelineState
  val decoder: Decoder = new Decoder()
  val fetcher: Fetcher = new Fetcher(this.state, instructionMemory)
  val executor: Executor = new Executor(this.state)

  def tick(): Unit = {
    val line: String = this.fetcher.fetchNext()
    val instruction = this.decoder.decodeNext(line)
    this.executor.execute(instruction)
    println(instruction)
    this.state.printRegisters()
    this.state.increment()
  }

  def run(): Unit = {
    while(state.getPc < instructionMemory.memory.length) {
      this.tick()
    }
  }
}
