package processor

object Main {
  def main(args: Array[String]): Unit = {
    val instructionMemory = new InstructionMemory(args(0))
    val pipeline = new Pipeline(instructionMemory)
    pipeline.run()
  }
}
