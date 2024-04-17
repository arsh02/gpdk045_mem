
// Dsp-block gpdk045_mem
// Description here
// Inititally written by dsp-blocks initmodule.sh, 20240417
package gpdk045_mem

import chisel3.experimental._
import chisel3._
import dsptools.{DspTester, DspTesterOptionsManager, DspTesterOptions}
import dsptools.numbers._
import scala.math._
import chisel3.util._
import chisel3.stage.{ChiselStage, ChiselGeneratorAnnotation}

class gpdk045_mem_io[T <:Data](proto: T,mem_size: Int)
   extends Bundle {
        val AR       = Input(UInt(log2Ceil(mem_size).W))
        val AW       = Input(UInt(log2Ceil(mem_size).W))
        val DW       = Input(proto)
        val QR       = Output(proto)
   }

class gpdk045_mem[T <:Data] (proto: T,mem_size: Int) extends Module {
    val io = IO(new gpdk045_mem_io( proto=proto, mem_size=mem_size))
    
    val write_val=RegInit(0.U.asTypeOf(proto.cloneType))
    val mem =SyncReadMem(mem_size, proto.cloneType)
    val write_addr =RegInit(0.U(log2Ceil(mem_size).W))
    val read_addr =RegInit(0.U(log2Ceil(mem_size).W))
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
    val annos = Seq(ChiselGeneratorAnnotation(() => new gpdk045_mem(
        proto=UInt(16.W), mem_size=scala.math.pow(2,13).toInt )
    ))
    (new ChiselStage).execute(args, annos)
}

