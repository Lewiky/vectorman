package processor.exceptions

final case class InstructionParseException(private val message: String = "",
                                 private val cause: Throwable = None.orNull)
  extends Exception(message, cause)