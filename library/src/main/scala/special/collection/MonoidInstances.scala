package special.collection {
  import scalan._

  trait MonoidInstances extends Base { self: Library =>
    import IntMaxMonoid._;
    import IntMinMonoid._;
    import IntPlusMonoid._;
    import LongMaxMonoid._;
    import LongMinMonoid._;
    import LongPlusMonoid._;
    import Monoid._;
    import MonoidBuilder._;
    import PairMonoid._;
    abstract class MonoidBuilderInst extends MonoidBuilder {
      override def pairMonoid[A, B](m1: Rep[Monoid[A]], m2: Rep[Monoid[B]]): Rep[Monoid[scala.Tuple2[A, B]]] = RPairMonoid(m1, m2);
      val `intPlusMonoid ` : Rep[IntPlusMonoid] = RIntPlusMonoid(toRep(0.asInstanceOf[Int]));
      def intPlusMonoid: Rep[IntPlusMonoid] = MonoidBuilderInst.this.`intPlusMonoid `;
      val `intMaxMonoid ` : Rep[IntMaxMonoid] = RIntMaxMonoid(toRep(-2147483648.asInstanceOf[Int]));
      def intMaxMonoid: Rep[IntMaxMonoid] = MonoidBuilderInst.this.`intMaxMonoid `;
      val `intMinMonoid ` : Rep[IntMinMonoid] = RIntMinMonoid(toRep(2147483647.asInstanceOf[Int]));
      def intMinMonoid: Rep[IntMinMonoid] = MonoidBuilderInst.this.`intMinMonoid `;
      val `longPlusMonoid ` : Rep[LongPlusMonoid] = RLongPlusMonoid(toRep(0L.asInstanceOf[Long]));
      def longPlusMonoid: Rep[LongPlusMonoid] = MonoidBuilderInst.this.`longPlusMonoid `;
      val `longMaxMonoid ` : Rep[LongMaxMonoid] = RLongMaxMonoid(toRep(-9223372036854775808L.asInstanceOf[Long]));
      def longMaxMonoid: Rep[LongMaxMonoid] = MonoidBuilderInst.this.`longMaxMonoid `;
      val `longMinMonoid ` : Rep[LongMinMonoid] = RLongMinMonoid(toRep(9223372036854775807L.asInstanceOf[Long]));
      def longMinMonoid: Rep[LongMinMonoid] = MonoidBuilderInst.this.`longMinMonoid `
    };
    abstract class IntPlusMonoid(val zero: Rep[Int]) extends Monoid[Int] {
      def plus(x: Rep[Int], y: Rep[Int]): Rep[Int] = x.+(y);
      def power(x: Rep[Int], n: Rep[Int]): Rep[Int] = x.*(n)
    };
    abstract class IntMaxMonoid(val zero: Rep[Int]) extends Monoid[Int] {
      @NeverInline def plus(x: Rep[Int], y: Rep[Int]): Rep[Int] = delayInvoke;
      @NeverInline def power(x: Rep[Int], n: Rep[Int]): Rep[Int] = delayInvoke
    };
    abstract class IntMinMonoid(val zero: Rep[Int]) extends Monoid[Int] {
      @NeverInline def plus(x: Rep[Int], y: Rep[Int]): Rep[Int] = delayInvoke;
      @NeverInline def power(x: Rep[Int], n: Rep[Int]): Rep[Int] = delayInvoke
    };
    abstract class LongPlusMonoid(val zero: Rep[Long]) extends Monoid[Long] {
      def plus(x: Rep[Long], y: Rep[Long]): Rep[Long] = x.+(y);
      def power(x: Rep[Long], n: Rep[Int]): Rep[Long] = x.*(n.toLong)
    };
    abstract class LongMaxMonoid(val zero: Rep[Long]) extends Monoid[Long] {
      @NeverInline def plus(x: Rep[Long], y: Rep[Long]): Rep[Long] = delayInvoke;
      @NeverInline def power(x: Rep[Long], n: Rep[Int]): Rep[Long] = delayInvoke
    };
    abstract class LongMinMonoid(val zero: Rep[Long]) extends Monoid[Long] {
      @NeverInline def plus(x: Rep[Long], y: Rep[Long]): Rep[Long] = delayInvoke;
      @NeverInline def power(x: Rep[Long], n: Rep[Int]): Rep[Long] = delayInvoke
    };
    abstract class PairMonoid[A, B](val m1: Rep[Monoid[A]], val m2: Rep[Monoid[B]]) extends Monoid[scala.Tuple2[A, B]] {
      override def zero: Rep[scala.Tuple2[A, B]] = Pair(PairMonoid.this.m1.zero, PairMonoid.this.m2.zero);
      override def plus(x: Rep[scala.Tuple2[A, B]], y: Rep[scala.Tuple2[A, B]]): Rep[scala.Tuple2[A, B]] = Pair(PairMonoid.this.m1.plus(x._1, y._1), PairMonoid.this.m2.plus(x._2, y._2));
      override def power(x: Rep[scala.Tuple2[A, B]], n: Rep[Int]): Rep[scala.Tuple2[A, B]] = Pair(PairMonoid.this.m1.power(x._1, n), PairMonoid.this.m2.power(x._2, n))
    };
    trait MonoidBuilderInstCompanion;
    trait IntPlusMonoidCompanion;
    trait IntMaxMonoidCompanion;
    trait IntMinMonoidCompanion;
    trait LongPlusMonoidCompanion;
    trait LongMaxMonoidCompanion;
    trait LongMinMonoidCompanion;
    trait PairMonoidCompanion
  }
}