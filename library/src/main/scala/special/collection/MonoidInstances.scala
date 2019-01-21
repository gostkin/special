package special.collection {
  import scalan._

  trait MonoidInstances extends Base { self: Library =>
    import IntPlusMonoid._;
    import LongPlusMonoid._;
    import Monoid._;
    import MonoidBuilder._;
    import PairMonoid._;
    abstract class MonoidBuilderInst extends MonoidBuilder {
      override def pairMonoid[A, B](m1: Rep[Monoid[A]], m2: Rep[Monoid[B]]): Rep[Monoid[scala.Tuple2[A, B]]] = RPairMonoid(m1, m2);
      val `intPlusMonoid ` : Rep[IntPlusMonoid] = RIntPlusMonoid(toRep(0.asInstanceOf[Int]));
      def intPlusMonoid: Rep[IntPlusMonoid] = MonoidBuilderInst.this.`intPlusMonoid `;
      val `longPlusMonoid ` : Rep[LongPlusMonoid] = RLongPlusMonoid(toRep(0L.asInstanceOf[Long]));
      def longPlusMonoid: Rep[LongPlusMonoid] = MonoidBuilderInst.this.`longPlusMonoid `
    };
    abstract class IntPlusMonoid(val zero: Rep[Int]) extends Monoid[Int] {
      def plus(x: Rep[Int], y: Rep[Int]): Rep[Int] = x.+(y);
      def power(x: Rep[Int], n: Rep[Int]): Rep[Int] = x.*(n)
    };
    abstract class LongPlusMonoid(val zero: Rep[Long]) extends Monoid[Long] {
      def plus(x: Rep[Long], y: Rep[Long]): Rep[Long] = x.+(y);
      def power(x: Rep[Long], n: Rep[Int]): Rep[Long] = x.*(n.toLong)
    };
    abstract class PairMonoid[A, B](val m1: Rep[Monoid[A]], val m2: Rep[Monoid[B]]) extends Monoid[scala.Tuple2[A, B]] {
      override def zero: Rep[scala.Tuple2[A, B]] = Pair(PairMonoid.this.m1.zero, PairMonoid.this.m2.zero);
      override def plus(x: Rep[scala.Tuple2[A, B]], y: Rep[scala.Tuple2[A, B]]): Rep[scala.Tuple2[A, B]] = Pair(PairMonoid.this.m1.plus(x._1, y._1), PairMonoid.this.m2.plus(x._2, y._2));
      override def power(x: Rep[scala.Tuple2[A, B]], n: Rep[Int]): Rep[scala.Tuple2[A, B]] = Pair(PairMonoid.this.m1.power(x._1, n), PairMonoid.this.m2.power(x._2, n))
    };
    trait MonoidBuilderInstCompanion;
    trait IntPlusMonoidCompanion;
    trait LongPlusMonoidCompanion;
    trait PairMonoidCompanion
  }
}