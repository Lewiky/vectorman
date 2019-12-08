package processor.units.branchPredictor

import processor.ProgramCounter

abstract class BranchPredictor {
  def predict(): ProgramCounter
  def ingest(instruction: String, programCounter: ProgramCounter): Unit
}
