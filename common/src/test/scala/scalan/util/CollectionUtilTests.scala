package scalan.util

import scalan.BaseTests
import scala.collection.{Seq, mutable}

class CollectionUtilTests extends BaseTests {
  import scalan.util.CollectionUtil._

  def join(l: Map[Int,Int], r: Map[Int,Int]) =
    outerJoin(l, r)((_,l) => l, (_,r) => r, (k,l,r) => l + r)
  def joinSeqs(l: Seq[Int], r: Seq[Int]) =
    outerJoinSeqs(l, r)(l => l, r => r)((_,l) => l, (_,r) => r, (k,l,r) => l + r).map(_._2)
  def joinPairs(l: Seq[(String,Int)], r: Seq[(String,Int)]) =
    outerJoinSeqs(l, r)(l => l._1, r => r._1)((_,l) => l._2, (_,r) => r._2, (k,l,r) => l._2 + r._2)

  test("outerJoin maps") {
    val left = Map(1 -> 1, 2 -> 2, 3 -> 3)
    val right = Map(2 -> 2, 3 -> 3, 4 -> 4)

    assertResult(Map(1 -> 1, 2 -> 4, 3 -> 6, 4 -> 4))(join(left,right))
    assertResult(Map(1 -> 1, 2 -> 2, 3 -> 3))(join(left,Map()))
    assertResult(Map(2 -> 2, 3 -> 3, 4 -> 4))(join(Map(), right))
    assertResult(Map(2 -> 4, 3 -> 6, 4 -> 8))(join(right, right))
  }

  test("outerJoinSeqs") {
    val left = Seq(1, 2, 3)
    val right = Seq(2, 3, 4)

    assertResult(Seq(1, 4, 6, 4))(joinSeqs(left, right))
    assertResult(Seq(1, 2, 3))(joinSeqs(left,Seq()))
    assertResult(Seq(2, 3, 4))(joinSeqs(Seq(), right))
    assertResult(Seq(4, 6, 8))(joinSeqs(right, right))

    val inner = Seq("a" -> 1, "b" -> 2, "c" -> 3)
    val outer = Seq("b" -> 2, "c" -> 3, "d" -> 4)

    assertResult(Seq("a" -> 1, "b" -> 4, "c" -> 6, "d" -> 4))(joinPairs(inner, outer))
    assertResult(Seq("a" -> 1, "b" -> 2, "c" -> 3))(joinPairs(inner,Seq()))
    assertResult(Seq("b" -> 2, "c" -> 3, "d" -> 4))(joinPairs(Seq(), outer))
    assertResult(Seq("b" -> 4, "c" -> 6, "d" -> 8))(joinPairs(outer, outer))
  }

  test("filterMap") {
    val xs = List(1, 2, 3)
    xs.filterMap(x => if (x <= 2) Some(s"x = $x") else None) should be(List("x = 1", "x = 2"))
  }

  test("mapFirst") {
    val xs = List(1, 2, 3)
    xs.findMap(x => if (x > 2) Some(s"x = $x") else None) should be(Some("x = 3"))
    xs.findMap(x => if (x > 3) Some(x) else None) should be(None)
  }

  val items: Iterable[(Int, String)] = Array((1, "a"), (2, "b"), (1, "c"))

  test("distinctBy") {
    val res = items.distinctBy(_._1)
    assertResult(Array((1, "a"), (2, "b")))(res)
  }

  test("mapReduce") {
    val res = items.mapReduce(p => (p._1, p._2))((v1, v2) => v1 + v2)
    assertResult(List((1, "ac"), (2, "b")))(res)
  }

  test("mergeWith") {
    type V = (Int, String)
    def key(p: V) = p._1
    def merge(v1: V, v2: V) = (v1._1, v1._2 + v2._2)

    {
      val res = List().mergeWith(List(), key, merge)
      assertResult(List())(res)
    }
    {
      val res = List((1, "a"), (2, "b"), (1, "c")).mergeWith(List(), key, merge)
      assertResult(List((1, "ac"), (2, "b")))(res)
    }
    {
      val res = List().mergeWith(List((1, "a"), (2, "b"), (1, "c")), key, merge)
      assertResult(List((1, "ac"), (2, "b")))(res)
    }
    {
      val ys = List((2, "c"), (3, "d"))
      val res = List((1, "a"), (2, "b"), (1, "c")).mergeWith(ys, key, merge)
      assertResult(List((1, "ac"), (2, "bc"), (3, "d")))(res)
    }
  }

  test("zipWithExpandedBy") {
    assertResult(Array((2, 0), (2, 1)))(2.zipWithExpandedBy(x => List.range(0,x)))
    assertResult(Array((3, 0), (3, 1), (3, 2)))(3.zipWithExpandedBy(x => List.range(0,x)))
  }

  def treeStep(tree: Array[List[Int]]): Int => List[Int] = i => tree(i)

  test("traverseDepthFirst") {
    {
      val tree = Array(
        List(1, 2), // 0
        List(),     // 1
        List(3),    // 2
        List())     // 3
      assertResult(List(0, 1, 2, 3))(0.traverseDepthFirst(treeStep(tree)))
    }
    {
      /*
       0
         1
           3
             5
             6
         2
           4
      */
      val tree = Array(
        List(1, 2),  // 0
        List(3),     // 1
        List(4),     // 2
        List(5,6),   // 3
        List(),      // 4
        List(),      // 5
        List()       // 6
      )
      assertResult(List(0, 1, 3, 5, 6, 2, 4))(0.traverseDepthFirst(treeStep(tree)))
    }
  }

  test("partitionByType") {
    val xs: List[Any] = List(1, "a", "b", 2, 3, 1.0, 2.0)
    val (ints, others) = xs.partitionByType[Integer, Any]
    ints shouldBe(List(1,2,3))
    val (strs, doubles) = others.partitionByType[String, Double]
    strs shouldBe(List("a", "b"))
    doubles shouldBe(List(1.0, 2.0))
  }

  test("mapConserve") {
    class A(val x: Int)
    val x = new A(10)
    val opt = Option(x)
    opt.mapConserve(a => a) shouldBe theSameInstanceAs(opt)
    opt.mapConserve(a => new A(a.x)) should not be theSameInstanceAs(opt)
  }

  test("transformConserve") {
    class A(val x: Int)
    val x = new A(10)
    x.transformConserve(a => a) shouldBe theSameInstanceAs(x)
    x.transformConserve(a => new A(a.x)) should not be theSameInstanceAs(x)
  }

  test("sameElements2") {
    Seq(1, 2).sameElements2(List(1, 2)) shouldBe true
    new mutable.WrappedArray.ofInt(Array(1, 2)).sameElements2(Vector(1, 2)) shouldBe true
    Seq(new mutable.WrappedArray.ofInt(Array(1, 2)), 3).sameElements2(Array(Vector(1, 2), 3)) shouldBe true
    Seq(Array(1, 2), 3).sameElements2(Array(Vector(1, 2), 3)) shouldBe true
    Seq(Array(1, 2), Option(3)).sameElements2(Array(Vector(1, 2), List(3))) shouldBe false

    Seq(1, 2).sameElements2(List(1, 2, 3)) shouldBe false
    new mutable.WrappedArray.ofInt(Array(1, 2, 3)).sameElements2(Vector(1, 2)) shouldBe false
    Seq(new mutable.WrappedArray.ofInt(Array(1, 2, 3)), 3).sameElements2(Array(Vector(1, 2), 3)) shouldBe false

  }
}
