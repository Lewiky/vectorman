package processor.units.branchPredictor

import processor.{ExecutionResult, ProgramCounter}

import scala.annotation.tailrec

class NeuralPredictor extends BranchPredictor {
  //http://webspace.ulbsibiu.ro/lucian.vintan/html/USA.pdf Referenced from here

  private val i = 3 //Number of bits to store PC
  private val k = 3 //Number of bits to store global history
  private val l = 3 //Number of bits to store local history
  private val a = 0.1 //Learning rate
  private var v_t: Vector[Byte] = Vector.fill(i + k + l)(1)
  private var v_nt: Vector[Byte] = Vector.fill(i + k + l)(0)
  private var HR_g: Vector[Byte] = Vector.fill(k)(0)
  private var HR_l: Map[ProgramCounter, Vector[Byte]] = Map()
  private var epoch = 0
  private var winner: Boolean = true
  private var update: Vector[Byte] = Vector.empty[Byte]

  private def hamming(a: Vector[Byte], b: Vector[Byte]): Int = {
    a.zip(b).foldRight(0) {
      case (tup, x) => x + Math.pow((tup._1 - tup._2), 2).toInt
    }
  }

  private def vAdd(a: Vector[Byte], b:Vector[Byte]): Vector[Byte] = {
    a.zip(b) map {
      x => (x._1 + x._2).toByte
    }
  }

  private def vMul(a: Double, b: Vector[Byte]): Vector[Byte] = {
    b.map(x => (a*x).toByte)
  }

  private def vSub(a: Vector[Byte], b:Vector[Byte]): Vector[Byte] = {
    a.zip(b) map {
      x => (x._1 - x._2).toByte
    }
  }

  @tailrec
  private def bitVector(n: Int, bin: List[Int] = List.empty[Int]): Vector[Byte] = {
    if (n / 2 == 1) (1 :: (n % 2) :: bin).toVector.map(x => x.toByte).takeRight(i)
    else {
      val r = n % 2
      val q = n / 2
      bitVector(q, r :: bin)
    }
  }

  override def wasCorrect(executionResult: ExecutionResult): Boolean = {
    val correct = super.wasCorrect(executionResult)
    //Update vectors
    if(!HR_l.contains(executionResult.getPC)) return correct
    val bit:Byte = if(executionResult.hasResult) 1 else 0
    HR_g = (HR_g :+ bit).takeRight(k)
    HR_l += (executionResult.getPC -> (HR_l(executionResult.getPC) :+ bit).takeRight(l))
    if(winner){
       v_t = if(correct) vAdd(v_t, update) else vSub(v_t, update)
    } else {
      v_nt = if(correct) vAdd(v_nt, update) else vSub(v_nt, update)
    }
    epoch += 1
    correct
  }

  override protected def predictTaken(string: String): Boolean = {
    val unconditionalBranches = List("BRA", "JMP", "END")
    unconditionalBranches.foreach(branch => if (string.contains(branch)) return true)
    if(!HR_l.contains(lastSeen._2)) HR_l += (lastSeen._2 -> Vector.fill(l)(0))
    val x: Vector[Byte] = HR_g.concat(HR_l(lastSeen._2)).concat(bitVector(lastSeen._2))
    val hamming_t = hamming(x, v_t)
    val hamming_nt = hamming(x, v_nt)
    if (hamming_t > hamming_nt) {
      winner = true
      update = vMul(a, vSub(x, v_t))
      true
    } else {
      winner = false
      update = vMul(a ,(vSub(x, v_nt)))
      false
    }
  }

}
