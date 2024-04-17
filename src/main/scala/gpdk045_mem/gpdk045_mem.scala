
// Dsp-block gpdk045_mem
// Description here
// Inititally written by dsp-blocks initmodule.sh, 20240417
package gpdk045_mem

import chisel3.experimental._
import chisel3._
import dsptools.{DspTester, DspTesterOptionsManager, DspTesterOptions}
import dsptools.numbers._
import breeze.math.Complex
import scala.math._

class gpdk045_mem_io[T <:Data](proto: T,mem_size: Int)
   extends Bundle {
        val AR       = Input(UInt(log2ceil(mem_size).W))
        val AW       = Input(UInt(log2ceil(mem_size).W))
        val DW       = Input(proto)
        val QR       = Output(proto)
        override def cloneType = (new gpdk045_mem_io(proto.cloneType,n)).asInstanceOf[this.type]
   }

class gpdk045_mem[T <:Data] (proto: T,mem_size: Int) extends Module {
    val io = IO(new gpdk045_mem_io( proto=proto, mem_size=mem_size))
    
    val write_val=RegInit(0.U.asTypeOf(proto.cloneType))
    val mem =SyncReadMem(memsize, proto.cloneType)
    val write_addr =RegInit(0.U(log2Ceil(memsize).W))
    val read_addr =RegInit(0.U(log2Ceil(memsize).W))
    val read_val =RegInit(0.U.asTypeOf(proto.cloneType))
    write_addr:=io.AW
    write_val:=io.DW
    read_addr:=io.AR
    
    mem.write(write_addr,write_val)

    read_val:=mem.read(read_addr)
    
    io.QR:=read_val
}

//This gives you verilog
object gpdk045_mem extends App {
    chisel3.Driver.execute(args, () => new gpdk045_mem(
        proto=UInt(16.W), memsize=scala.math.pow(2,13).toInt )
    )
}

//This is a simple unit tester for demonstration purposes
class unit_tester(c: gpdk045_mem[DspComplex[UInt]] ) extends DspTester(c) {
//Tests are here 
    poke(c.io.AW, 0)
    poke(c.io.DW, 2)
    step(1)
    poke(c.io.AR, 0)
    fixTolLSBs.withValue(1) {
        expect(c.io.QR, 2)
    }
}

//This is the test driver 
object unit_test extends App {
    iotesters.Driver.execute(args, () => new gpdk045_mem(
            proto=UInt(16.W), memsize=scala.math.pow(2,13).toInt
          )
    ){
            c=>new unit_tester(c)
    }
}
