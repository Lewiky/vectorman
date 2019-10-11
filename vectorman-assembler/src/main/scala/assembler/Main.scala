package assembler

import java.io.File

import scala.io.Source

object Main {

  private def parseArgs(args: Array[String]): String = {
    if (args.length == 0) {
      throw new Error("Required filename missing")
    }
    args(0)
  }

  private def createNewFile(fileName: String): File = {
    val name = fileName.split("\\.")(0)
    val file = new File(name + ".o")
    if (file.exists()) file.delete()
    file.createNewFile()
    file
  }

  def main(args: Array[String]): Unit = {
    val fileName = this.parseArgs(args)
    val outFile = this.createNewFile(fileName)
    val parser = new InstructionParser
    val source = Source.fromFile(fileName)
    val writer = new InstructionBinaryWriter(outFile)
    source.getLines().foreach {
      line => {
        parser.parse(parser.program, line) match {
          case parser.Success(matched: List[Instruction], _) => writer.write(matched)
          case parser.Failure(msg, _) => println("FAILURE: " + msg)
          case parser.Error(msg, _) => println("ERROR: " + msg)
        }
      }
    }
    source.close()
    writer.fileWriter.close()
  }
}
