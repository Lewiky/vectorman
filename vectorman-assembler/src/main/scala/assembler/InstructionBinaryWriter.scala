package assembler

import java.io._

class InstructionBinaryWriter(outfile: File) {

  val fileWriter = new PrintWriter(this.outfile)

  private def toBin(i: Register): String = {
    if (0 > i || i > 15) {
      throw new Error("Invalid register number")
    }
    val s = i.toBinaryString
    f"$s%4s".replace(' ', '0')
  }

  private def buildBinaryString(instruction: Instruction): String =
    instruction.bin + instruction.params.map(this.toBin).mkString("")

  def write(instructions: List[Instruction]) {
    instructions.foreach(
      instruction => this.fileWriter.write(this.buildBinaryString(instruction))
    )
  }
}
