package processor

import processor.units.{Decoder, Fetcher}

class Pipeline(instructionMemory: InstructionMemory) {

  val state: PipelineState = new PipelineState
  val decoder: Decoder = new Decoder()
  val fetcher: Fetcher = new Fetcher(this.state, instructionMemory)

  def tick(): Unit = {
    val line: String = this.fetcher.fetchNext()
    val instruction = this.decoder.decodeNext(line)
    println(instruction)
    this.state.increment()
  }

  def run(): Unit = {
    while(state.getPc < instructionMemory.memory.length) {
      this.tick()
    }
  }
}
