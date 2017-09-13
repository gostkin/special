package scalan.it.lms

import scalan.compilation.lms.scalac.LmsCompilerScala
import scalan.primitives.{StructExamples, StructItTests}
import scalan.ScalanDsl
import scalan.common.{MetaTestsModule, SegmentsModule}

class LmsStructItTests extends StructItTests {
  class ProgExp extends ScalanDsl with StructExamples with SegmentsModule with MetaTestsModule

  val progStaged = new LmsCompilerScala(new ProgExp)

  val defaultCompilers = compilers(progStaged)

  import progStd._

//  test("Generate CPP source") {
//    compileSource(s => s.t1.asInstanceOf[s.Rep[Int => Struct]], cxxOnly)
//
//    compileSource(s => s.singleFieldStructIn.asInstanceOf[s.Rep[Struct => Int]], cxxOnly)
//
//    compileSource(s => s.structInOut.asInstanceOf[s.Rep[Struct => Struct]], cxxOnly)
//
//    compileSource(_.structInside, cxxOnly)
//  }
}
