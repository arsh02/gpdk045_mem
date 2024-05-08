package gpdk045_mem

import scala.math._
import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec
import dsptools.{DspTester, DspTesterOptionsManager, DspTesterOptions}
import dsptools.numbers._

class gpdk045_memSpec extends AnyFlatSpec with ChiselScalatestTester {

  it should "propagate values in the register" in {
    test(new gpdk045_mem(proto = UInt(8.W), mem_size=scala.math.pow(2,13).toInt  )) { dut =>
      dut.io.AW poke 0
      dut.io.DW poke 5 
      dut.clock.step(1)
      dut.io.AR poke 0
      dut.io.QR expect 5
    }
  }
}
