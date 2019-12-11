package processor

import ch.qos.logback.classic.{Level, Logger}
import org.slf4j.LoggerFactory
import processor.units.branchPredictor.{BranchPredictor, BranchPredictorFactory}
import processor.units.branchPredictor.branchPredictorType._
import processor.units.{Decoder, EUnit, Executor, Fetcher, ReorderBuffer, WriteBack}

class Pipeline(instructionMemory: InstructionMemory, instructionsPerCycle: Int, executeUnits: Int, branchPredictorType: Value) {

  val branchPredictor: BranchPredictor = BranchPredictorFactory.create(branchPredictorType)
  val reorderBuffer = new ReorderBuffer
  val state: PipelineState = new PipelineState
  val fetcher: Fetcher = new Fetcher(this.state, instructionMemory, instructionsPerCycle, branchPredictor)
  var executors: List[Executor] = List.fill(executeUnits)(new Executor(this.state))
  var decoder: Decoder = new Decoder(this.executors, this.reorderBuffer, this.state)
  val writeBack: WriteBack = new WriteBack(this.state, this, this.reorderBuffer, branchPredictor)
  var units: List[EUnit[_, _]] = List(fetcher, decoder, writeBack) ++ executors
  private var verbose: Boolean = false

  private def pipe[T](a: EUnit[_, T], b: EUnit[T, _]): Unit = {
    if (b.input.isEmpty) {
      b.input = a.output
      a.output = None
    }
  }

  private def pipeMany[T](a: EUnit[_, List[T]], b: List[EUnit[T, _]]): Unit = {
    a.output match {
      case Some(output) => (output zip b).foreach {
        case (out, unit) => if (unit.input.isEmpty) unit.input = Some(out)
      }
      case None => ()
    }
    a.output = None
  }

  def flush(): Unit = {
    this.units.foreach(unit => {
      unit.input = None
      unit.output = None
    })
    this.executors.foreach(_.flush())
    this.decoder.reservationStation.flush()
    this.reorderBuffer.flush()
    this.state.flushScoreboard()
    logger.debug("Flushed pipeline")
  }

  def tick(): Unit = {
    //I tried to map pipe over the units list but scala doesn't support dependant typing so it doesn't compile :(
    this.fetcher.tick()
    this.pipe(this.fetcher, this.decoder)
    this.decoder.tick()
    this.pipeMany(this.decoder, this.executors.filter(unit => unit.isReady))
    this.executors.foreach {
      _.tick()
    }
    this.writeBack.tick()
    state.tickTime(1)
  }

  def printStatistics(): Unit = {
    val cycles = state.getTime
    val instructionsCompleted = state.getInstructionsCompleted
    val instructions = state.getInstructionsExecuted
    val rate = instructions.toFloat / cycles
    println(f"Executed $instructions instructions ($instructionsCompleted completed) in $cycles cycles (rate $rate%1.2f inst/cycle)")
    branchPredictor.printStatistics()
  }

  def run(): Unit = {
    while (state.getPc >= 0) {
      this.tick()
    }
    this.printStatistics()
  }

  def setExecutors(n: Int): Unit = {
    this.executors = List.fill(executeUnits)(new Executor(this.state))
    this.decoder = new Decoder(this.executors, this.reorderBuffer, this.state)
    this.units = List(fetcher, decoder, writeBack) ++ executors
  }

  def toggleVerbose(): Unit = {
    val root: Logger = LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME).asInstanceOf[Logger]
    if (this.verbose) {
      root.setLevel(Level.INFO)
      println("-- Verbose Off --")
    } else {
      root.setLevel(Level.DEBUG)
      println("-- Verbose On  --")
    }
    this.verbose = !this.verbose
  }
}
