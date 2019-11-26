package processor.units.circularBuffer


class CircularBuffer[A: Manifest](size: Int) {
  private val buffer: Array[A] = new Array[A](size)
  var headPointer: Int = 0
  var tailPointer: Int = 0
  private var isEmpty = true

  def read(): Option[A] = {
    if (!isEmpty) {
      val x = buffer(tailPointer)
      tailPointer = (tailPointer + 1) % size
      if(tailPointer == headPointer) isEmpty = true
      return Some(x)
    }
    None
  }

  def peek(): A = {
    this.buffer(tailPointer)
  }

  def write(item: A): Unit = {
    if (headPointer != tailPointer || isEmpty) {
      buffer(headPointer) = item
      headPointer = (headPointer + 1) % size
      isEmpty = false
    }
  }

  def show(): Unit = {
    for(i <- 0.until(size)){
      print(s"${buffer(i)} ")
    }
    println()
    val lowest = math.min(headPointer, tailPointer)
    val largest = math.max(headPointer, tailPointer)
    for(_<- 0.until(lowest)) print(" ")
    if(lowest == headPointer) print("H") else print("T")
    for(_<-lowest.until(largest)) print(" ")
    if(largest == headPointer) print("H") else print("T")
    println()
  }

  def empty: Boolean = isEmpty
}
