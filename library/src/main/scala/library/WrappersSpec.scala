package library {
  import scalan._

  trait WrappersSpec extends Base { self: Library =>
    trait WrapSpec extends Def[WrapSpec];
    abstract class ArrayWrapSpec extends WrapSpec {
      def zip[A, B](xs: Rep[WArray[A]], ys: Rep[WArray[B]]): Rep[WArray[scala.Tuple2[A, B]]] = xs.zip(ys);
      def map[A, B](xs: Rep[WArray[A]], f: Rep[scala.Function1[A, B]]): Rep[WArray[B]] = xs.map(f);
      def length[A](xs: Rep[WArray[A]]): Rep[Int] = xs.length;
      def fill[A](n: Rep[Int], elem: Rep[A]): Rep[WArray[A]] = WArray.fill[A](n, Thunk(elem));
      def slice[A](xs: Rep[WArray[A]], from: Rep[Int], until: Rep[Int]): Rep[WArray[A]] = xs.slice(from, until);
      def foldLeft[A, B](xs: Rep[WArray[A]], zero: Rep[B], op: Rep[scala.Function1[scala.Tuple2[B, A], B]]): Rep[B] = xs.foldLeft(zero, op);
      def filter[A](xs: Rep[WArray[A]], p: Rep[scala.Function1[A, Boolean]]): Rep[WArray[A]] = xs.filter(p);
      def forall[A](xs: Rep[WArray[A]], p: Rep[scala.Function1[A, Boolean]]): Rep[Boolean] = xs.forall(p);
      def exists[A](xs: Rep[WArray[A]], p: Rep[scala.Function1[A, Boolean]]): Rep[Boolean] = xs.exists(p);
      def foreach[A](xs: Rep[WArray[A]], p: Rep[scala.Function1[A, Unit]]): Rep[Unit] = xs.foreach(p);
      def apply[A](xs: Rep[WArray[A]], i: Rep[Int]): Rep[A] = xs.apply(i)
    };
    abstract class SpecialPredefWrapSpec extends WrapSpec {
      def loopUntil[A](s1: Rep[A], isMatch: Rep[scala.Function1[A, Boolean]], step: Rep[scala.Function1[A, A]]): Rep[A] = WSpecialPredef.loopUntil[A](s1, isMatch, step)
    };
    trait WrapSpecCompanion;
    trait ArrayWrapSpecCompanion;
    trait SpecialPredefWrapSpecCompanion
  }
}