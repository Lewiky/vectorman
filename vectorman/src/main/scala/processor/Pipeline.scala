package processor

import processor.units.{Decoder, Executor, Fetcher, WriteBack}

class Pipeline(instructionMemory: InstructionMemory) {

  val state: PipelineState = new PipelineState
  val decoder: Decoder = new Decoder()
  val fetcher: Fetcher = new Fetcher(this.state, instructionMemory)
  val executor: Executor = new Executor(this.state)
  val writeBack: WriteBack = new WriteBack(this.state)

  def tick(): Unit = {
    val line = this.fetcher.fetchNext()
    val instruction = this.decoder.decodeNext(line)
    val result = this.executor.execute(instruction)
    this.writeBack.writeResults(result)
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
