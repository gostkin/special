package scalanizer.collections

import scala.collection.mutable
import scala.language.reflectiveCalls
import scalan._

class CostedTests extends BaseCtxTests {
  class Ctx extends TestContext with Library {
    val WA = WArrayMethods
    object IsProjectFirst {
      def unapply[A,B](f: Rep[A => B]): Option[Rep[A=>B]] = f match {
        case Def(Lambda(_,_,x, Def(First(p)))) if p == x => Some(f)
        case _ => None
      }
    }
    object IsProjectSecond {
      def unapply[A,B](f: Rep[A => B]): Option[Rep[A=>B]] = f match {
        case Def(Lambda(_,_,x, Def(Second(p)))) if p == x => Some(f)
        case _ => None
      }
    }
    override def rewriteDef[T](d: Def[T]) = d match {
      case WA.length(WA.map(xs, _)) => xs.length
      case WA.zip(WA.map(xs, IsProjectFirst(_)), WA.map(ys, IsProjectSecond(_))) if xs == ys => xs
      case WA.map(WA.map(_xs, f: RFunc[a, b]), _g: RFunc[_,c]) =>
        implicit val ea = f.elem.eDom
        val xs = _xs.asRep[WArray[a]]
        val g  = _g.asRep[b => c]
        xs.map(fun { x: Rep[a] => g(f(x)) })
      case _ => super.rewriteDef(d)
    }

    def plus(x: Rep[Int], n: Int) = {
      Range(0, n).foldLeft(x)((y, i) => y + i)
    }

    val b = ColOverArrayBuilder()

    def byteSize[T](eT: BaseElem[T]): Int = eT match {
      case BooleanElement => 1
      case ByteElement => 1
      case ShortElement => 2
      case IntElement => 4
      case LongElement => 8
    }

    def dataCost[T:Elem](x: Rep[T]): Rep[Costed[T]] = element[T] match {
      case be: BaseElem[_] => CostedPrim(x, byteSize(be).toLong)
      case pe: PairElem[a,b] =>
        val l = dataCost(x.asRep[(a,b)]._1)(pe.eFst)
        val r = dataCost(x.asRep[(a,b)]._2)(pe.eSnd)
        CostedPair(l.value, r.value, l.cost + r.cost)
      case ae: WArrayElem[_,_] =>
        ae.eItem match {
          case be: BaseElem[a] =>
            val values = b.fromArray(x.asRep[WArray[a]])
            val costs = ReplCol(byteSize(be).toLong, values.length)
            CostedArray(values, costs).asRep[Costed[T]]
          case pe: PairElem[a,b] =>
            val arr = x.asRep[WArray[(a,b)]]
            implicit val ea = arr.elem.eItem.eFst
            implicit val eb = arr.elem.eItem.eSnd
            val ls = dataCost[WArray[a]](arr.map(fun(_._1)))
            val rs = dataCost[WArray[b]](arr.map(fun(_._2)))
            CostedPairArray(ls, rs).asRep[Costed[T]]
          case ae: WArrayElem[a,_] =>
            implicit val ea = ae.eItem
            val arr = b.fromArray(x.asRep[WArray[WArray[a]]])
            val rows = arr.map(fun(r => dataCost[WArray[a]](r)))
            CostedNestedArray(rows).asRep[Costed[T]]
        }
    }

    def result[T](dc: Rep[Costed[T]]): Rep[(T, Long)] = Pair(dc.value, dc.cost)

    def split[T,R](f: Rep[T => Costed[R]]): Rep[(T => R, T => Long)] = {
      implicit val eT = f.elem.eDom
      val calc = fun { x: Rep[T] => f(x).value }
      val cost = fun { x: Rep[T] => f(x).cost }
      Pair(calc, cost)
    }
  }

  def measure[T](nIters: Int)(action: Int => Unit): Unit = {
    for (i <- 1 to nIters) {
      val start = System.currentTimeMillis()
      val res = action(i)
      val end = System.currentTimeMillis()
      val iterTime = end - start
      println(s"Iter $i: $iterTime ms")
    }
  }
  lazy val ctx = new Ctx { }
  import ctx._

  def buildGraph[T](nIters: Int, name: String)(action: Int => Rep[T]) = {
    val buf = mutable.ArrayBuilder.make[Rep[T]]()
    measure(nIters) { i =>
      buf += action(i)
    }
    ctx.emit(name, buf.result(): _*)
  }


  test("measure: plus const propagation") {
    buildGraph(10, "measure_plus_const") { i =>
      plus(i * 1000, 1000)
    }
  }

  test("plus fresh var") {
    buildGraph(10, "measure_plus_var") { i =>
      plus(fresh[Int], 1000)
    }
  }

  test("measure: dataCost") {
    buildGraph(10, "measure_dataCost") { i =>
      val data = Range(0, 20).foldLeft[Rep[Any]](toRep(i))((y, k) => Pair(y, k))
      result(dataCost(data)(data.elem))
    }
  }

  test("data cost") {
    ctx.emit("dataCost",
      result(dataCost(Pair(10, 20.toByte))),
      result(dataCost(Pair(30, Pair(40.toByte, 50L))))
    )
  }

  test("split") {
    ctx.emit("split",
      split(fun { x: Rep[(Int, Byte)] => dataCost(x) }),
      split(fun { x: Rep[Int] => dataCost(Pair(x, 20.toByte)) })
    )
  }

  test("split arrays") {
    ctx.emit("split_arrays",
      split(fun { in: Rep[(WArray[Int], Byte)] =>
        dataCost(in)
      })
    )
  }

  test("measure: split arrays") {
    buildGraph(10, "measure_split_arrays") { i =>
      var res: Rep[Any] = null
      for (k <- 1 to 1000) {
        res = split(fun { in: Rep[(WArray[Int], Byte)] =>
          val Pair(x, b) = in
          dataCost(Pair(x, b + i.toByte))
        })
      }
      res
    }
  }

  test("split pair arrays") {
    ctx.emit("split_pair_arrays",
      split(fun { in: Rep[(WArray[(Int, Short)], Byte)] =>
        dataCost(in)
      })
    )
    ctx.emit("split_pair_arrays2",
      split(fun { in: Rep[(WArray[(Int, (Short, Boolean))], Byte)] =>
        dataCost(in)
      })
    )
  }

  test("split nested arrays") {
    ctx.emit("split_nested_arrays",
      split(fun { in: Rep[(WArray[WArray[Int]], Byte)] =>
        dataCost(in)
      })
    )
  }

}
