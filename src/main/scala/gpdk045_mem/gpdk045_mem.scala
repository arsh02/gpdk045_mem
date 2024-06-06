
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
        val write_en     = Input(Bool())
        val addr_R       = Input(UInt(log2Ceil(mem_size).W))
        val addr_W       = Input(UInt(log2Ceil(mem_size).W))
        val data_W       = Input(proto)
        val data_R       = Output(proto)
   }

class gpdk045_mem[T <:Data] (proto: T,mem_size: Int) extends Module {
    val io = IO(new gpdk045_mem_io( proto=proto, mem_size=mem_size))
    

    val mem       =SyncReadMem(mem_size, proto.cloneType)
    
    val write_val =RegInit(0.U.asTypeOf(proto.cloneType))
    val write_addr=RegInit(0.U(log2Ceil(mem_size).W))
    val read_addr =RegInit(0.U(log2Ceil(mem_size).W))
    val read_val  =RegInit(0.U.asTypeOf(proto.cloneType))
    val write_en  =RegInit(0.B)
    
    write_addr:=io.addr_W
    read_addr :=io.addr_R
    
    write_en  :=io.write_en
    write_val :=io.data_W
   
    when (write_en){
      mem.write(write_addr,write_val)
    }

    read_val  :=mem.read(read_addr)
    io.data_R :=read_val
}

//This gives you verilog
object gpdk045_mem extends App {
    val annos = Seq(ChiselGeneratorAnnotation(() => new gpdk045_mem(
        proto=UInt(8.W), mem_size=scala.math.pow(2,12).toInt )
    ))
    (new ChiselStage).execute(args, annos)
}

