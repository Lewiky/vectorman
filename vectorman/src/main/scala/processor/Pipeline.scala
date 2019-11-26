package processor

import ch.qos.logback.classic.{Level, Logger}
import org.slf4j.LoggerFactory
import processor.units.{Decoder, EUnit, Executor, Fetcher, WriteBack}

class Pipeline(instructionMemory: InstructionMemory, instructionsPerCycle: Int, executeUnits: Int) {

  val state: PipelineState = new PipelineState
  val fetcher: Fetcher = new Fetcher(this.state, instructionMemory, instructionsPerCycle)
  val executors: List[Executor] = List.fill(executeUnits)(new Executor(this.state))
  val decoder: Decoder = new Decoder(executors)
  val writeBack: WriteBack = new WriteBack(this.state, this)
  val units: List[EUnit[_, _]] = List(fetcher, decoder, writeBack) ++ executors
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
        case (out, unit) => if(unit.input.isEmpty) unit.input = Some(out)
      }
      case None => ()
    }
    a.output = None
  }

  def flush(): Unit = {
    this.units.foreach( unit =>{
      unit.input = None
      unit.output = None
    } )
    this.executors.foreach(_.flush())
    this.decoder.reservationStation.flush()
    logger.debug("Flushed pipeline")
  }

  def tick(): Unit = {
    //I tried to map pipe over the units list but scala doesn't support dependant typing so it doesn't compile :(
    this.pipe(this.fetcher, this.decoder)
    this.pipeMany(this.decoder, this.executors)
    //TODO: Reorder Buffer here
    this.pipe(this.executors(0), this.writeBack)
    this.units.foreach {
      _.tick()
    }
    state.tickTime(1)
  }

  def printStatistics(): Unit = {
    val cycles = state.getTime
    val instructions = state.getInstructionsCompleted
    val rate = instructions.toFloat/cycles
    println(s"Executed $instructions instructions in $cycles cycles (rate $rate inst/cycle)")
  }

  def run(): Unit = {
    while (state.getPc >= 0) {
      this.tick()
    }
    this.printStatistics()
  }

  def toggleVerbose(): Unit = {
    val root: Logger = LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME).asInstanceOf[Logger]
    if(this.verbose) {
      root.setLevel(Level.INFO)
      println("-- Verbose Off --")
    } else{
      root.setLevel(Level.DEBUG)
      println("-- Verbose On  --")
    }
    this.verbose = !this.verbose
  }
}
