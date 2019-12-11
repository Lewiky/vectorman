package processor.units

import processor._

import scala.collection.mutable.ListBuffer

class RegisterRenameUnit(architecturalRegisters: Int = 16, physicalRegisters: Int = 32) {

  private var renameTable: Map[Register, ListBuffer[Register]] = {
    var table: Map[Register, ListBuffer[Register]] = Map()
    for (i <- 0.until(architecturalRegisters)) {
      table += (i -> ListBuffer(i))
    }
    table
  }

  def renameDestination(register: Register): Register = {
    //Remove unused regs from list
    renameTable foreach {
      case(register, buff) => renameTable += (register -> ListBuffer(buff.last))
    }
    //Find available physical regs
    val all_used: ListBuffer[Register] = renameTable.values.foldRight(ListBuffer.empty[Register])(_ ++ _)
    val available = (0.until(physicalRegisters).toList diff all_used).sorted
    val reg = available.head
    renameTable += (register -> renameTable(register).append(reg))
    logger.debug(s"Rename $register to $reg")
    reg
  }


  def rename(instruction: Instruction): Instruction = {
    instruction match {
      case End(_,_) => return instruction
      case default => ()
    }
    var renamedParams = instruction.getParams map {
      param =>
        renameTable(param).last
    }
    if(!List(PC, MEM).contains(instruction.getDestination)){
      renamedParams = renameDestination(instruction.getDestination) :: renamedParams
    }
    val x = instruction match {
      case Add(_, str) => Add(renamedParams, str)
      case Sub(_, str) => Sub(renamedParams, str)
      case Mul(_, str) => Mul(renamedParams, str)
      case Div(_, str) => Div(renamedParams, str)
      case Lod(_, str) => Lod(renamedParams, str)
      case Str(_, str) => Str(renamedParams, str)
      case Bra(_, str) => Bra(renamedParams, str)
      case Jmp(_, str) => Jmp(renamedParams, str)
      case Ble(_, str) => Ble(renamedParams, str)
      case Cmp(_, str) => Cmp(renamedParams, str)
      case And(_, str) => And(renamedParams, str)
      case Not(_, str) => Not(renamedParams, str)
      case Rsh(_, str) => Rsh(renamedParams, str)
      case Beq(_, str) => Beq(renamedParams, str)
      case Cpy(_, str) => Cpy(renamedParams, str)
      case Loi(_, str, immediate) => Loi(renamedParams, str, immediate)
      case End(params, str) => End(params, str)
    }
    x
  }

  def print(): Unit = {
    println("-- Rename Table --")
    println(renameTable)
  }
}
