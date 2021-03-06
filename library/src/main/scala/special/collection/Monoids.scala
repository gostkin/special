package special.collection {
  import scalan._

  trait Monoids extends Base { self: Library =>
    import Monoid._;
    import MonoidBuilder._;
    trait Monoid[T] extends Def[Monoid[T]] {
      implicit def eT: Elem[T];
      def zero: Rep[T];
      def plus(x: Rep[T], y: Rep[T]): Rep[T];
      def power(x: Rep[T], n: Rep[Int]): Rep[T]
    };
    trait MonoidBuilder extends Def[MonoidBuilder] {
      def intPlusMonoid: Rep[Monoid[Int]];
      def intMaxMonoid: Rep[Monoid[Int]];
      def intMinMonoid: Rep[Monoid[Int]];
      def longPlusMonoid: Rep[Monoid[Long]];
      def longMaxMonoid: Rep[Monoid[Long]];
      def longMinMonoid: Rep[Monoid[Long]];
      def pairMonoid[A, B](m1: Rep[Monoid[A]], m2: Rep[Monoid[B]]): Rep[Monoid[scala.Tuple2[A, B]]]
    };
    trait MonoidCompanion;
    trait MonoidBuilderCompanion
  }
}