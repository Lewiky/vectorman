package processor

import ch.qos.logback.classic.{Level, Logger}
import org.slf4j.LoggerFactory
import processor.units.{Decoder, EUnit, Executor, Fetcher, WriteBack}

class Pipeline(instructionMemory: InstructionMemory) {

  val state: PipelineState = new PipelineState
  val decoder: Decoder = new Decoder()
  val fetcher: Fetcher = new Fetcher(this.state, instructionMemory)
  val executor: Executor = new Executor(this.state)
  val writeBack: WriteBack = new WriteBack(this.state, this)
  val units: List[EUnit[_, _]] = List(decoder, fetcher, executor, writeBack)
  private var verbose: Boolean = false

  private def pipe[T](a: EUnit[_, T], b: EUnit[T, _]): Unit = {
    if (b.input.isEmpty) {
      b.input = a.output
      a.output = None
    }
  }

  def flush(): Unit = {
    this.units.foreach( unit =>{
      unit.input = None
      unit.output = None
    } )
    this.executor.flush()
    logger.debug("Flushed pipeline")
  }

  def tick(): Unit = {
    //I tried to map pipe over the units list but scala doesn't support dependant typing so it doesn't compile :(
    this.pipe(this.fetcher, this.decoder)
    this.pipe(this.decoder, this.executor)
    this.pipe(this.executor, this.writeBack)
    this.units.foreach {
      _.tick()
    }
    state.tickTime(1)
  }

  def run(): Unit = {
    while (state.getPc >= 0) {
      this.tick()
    }
    println(s"Executed in ${state.getTime} cycles")
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
