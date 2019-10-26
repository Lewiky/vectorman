import com.typesafe.scalalogging.Logger

package object processor {
  type Register = Int
  type ProgramCounter = Int
  val logger = Logger("root")
  val PC: Register = -1
}
