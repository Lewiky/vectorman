package assembler

object Main extends App {
  val parser = new InstructionParser
  parser.parse(parser.program, "ADD r0 r1 r2 SUB r1 r0 r2") match {
    case parser.Success(matched, _) => println(matched)
    case parser.Failure(msg, _) => println("FAILURE: " + msg)
    case parser.Error(msg, _) => println("ERROR: " + msg)
  }
}