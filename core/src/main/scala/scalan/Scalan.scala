package scalan

import scalan.compilation.GraphVizExport
import scalan.primitives._
import scalan.staged.{BaseExp, Expressions, Transforming, TransformingExp}
import scalan.util.{ExceptionsDsl, ExceptionsDslExp}

abstract class Scalan
  extends Base
  with Debugging
  with TypeDescs
  with TypeWrappers
  with ViewsDsl
  with Entities
  with Proxy
  with Tuples
  with Loops
  with TypeSum
  with UnBinOps
  with LogicalOps
  with OrderingOps
  with NumericOps
  with StringOps
  with Equal
  with UniversalOps
  with MathOps
  with Functions
  with IfThenElse
  with Blocks
  with Monoids
  with PatternMatching
//  with MapOps
//  with MapViews
  with Transforming
  with Analyzing
//  with ArrayOps
//  with ArrayBuffers
  with Exceptions
//  with ArrayViews
  with Thunks
  with Effects
  with Metadata
//  with ListOps
//  with ListViews
  with ConvertersDsl
  with Effectful
  with StructsDsl

trait ScalanDsl
extends Scalan
  with ExceptionsDsl

abstract class ScalanExp
  extends Scalan
  with BaseExp
  with TypeDescsExp
  with TypeWrappersExp
  with ViewsDslExp
  with ProxyExp
  with TuplesExp
  with LoopsExp
  with TypeSumExp
  with NumericOpsExp
  with UnBinOpsExp
  with LogicalOpsExp
  with OrderingOpsExp
  with EqualExp
  with UniversalOpsExp
  with FunctionsExp
  with IfThenElseExp
  with BlocksExp
  with PatternMatchingExp
//  with MapOpsExp
//  with MapViewsExp
  with TransformingExp
  with AnalyzingExp
//  with ArrayOpsExp
//  with ArrayBuffersExp
  with ExceptionsExp
//  with ArrayViewsExp
  with StringOpsExp
  with ThunksExp
  with EffectsExp
  with MetadataExp
//  with ListOpsExp
//  with ListViewsExp
  with ConvertersDslExp
  with EffectfulExp
  with RewriteRulesExp
  with GraphVizExport
  with StructsDslExp

class ScalanDslExp
extends ScalanExp
  with ScalanDsl
  with Expressions
  with ExceptionsDslExp
