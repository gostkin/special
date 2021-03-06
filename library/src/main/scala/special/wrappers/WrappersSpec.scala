package special.wrappers {
  import scalan._

  trait WrappersSpec extends Base { self: Library =>
    import WArray._;
    import WEither._;
    import WOption._;
    import WRType._;
    import WSpecialPredef._;
    import WrapSpecBase._;
    trait WrapSpecBase extends Def[WrapSpecBase] with WrapSpec;
    trait ArrayWrapSpec extends WrapSpecBase {
      def zip[A, B](xs: Rep[WArray[A]], ys: Rep[WArray[B]]): Rep[WArray[scala.Tuple2[A, B]]] = xs.zip(ys);
      def map[A, B](xs: Rep[WArray[A]], f: Rep[scala.Function1[A, B]]): Rep[WArray[B]] = xs.map(f);
      def length[A](xs: Rep[WArray[A]]): Rep[Int] = xs.length;
      def fill[A](n: Rep[Int], elem: Rep[A]): Rep[WArray[A]] = RWArray.fill[A](n, Thunk(elem));
      def slice[A](xs: Rep[WArray[A]], from: Rep[Int], until: Rep[Int]): Rep[WArray[A]] = xs.slice(from, until);
      @NeverInline def foldLeft[A, B](xs: Rep[WArray[A]], zero: Rep[B], op: Rep[scala.Function1[scala.Tuple2[B, A], B]]): Rep[B] = delayInvoke;
      def filter[A](xs: Rep[WArray[A]], p: Rep[scala.Function1[A, Boolean]]): Rep[WArray[A]] = xs.filter(p);
      def forall[A](xs: Rep[WArray[A]], p: Rep[scala.Function1[A, Boolean]]): Rep[Boolean] = xs.forall(p);
      def exists[A](xs: Rep[WArray[A]], p: Rep[scala.Function1[A, Boolean]]): Rep[Boolean] = xs.exists(p);
      def foreach[A](xs: Rep[WArray[A]], p: Rep[scala.Function1[A, Unit]]): Rep[Unit] = xs.foreach(p);
      def apply[A](xs: Rep[WArray[A]], i: Rep[Int]): Rep[A] = xs.apply(i)
    };
    trait OptionWrapSpec extends WrapSpecBase {
      def get[A](xs: Rep[WOption[A]]): Rep[A] = xs.get;
      @NeverInline def getOrElse[A](xs: Rep[WOption[A]], default: Rep[Thunk[A]]): Rep[A] = delayInvoke;
      def map[A, B](xs: Rep[WOption[A]], f: Rep[scala.Function1[A, B]]): Rep[WOption[B]] = xs.map[B](f);
      def flatMap[A, B](xs: Rep[WOption[A]], f: Rep[scala.Function1[A, WOption[B]]]): Rep[WOption[B]] = xs.flatMap[B](f);
      def filter[A](xs: Rep[WOption[A]], f: Rep[scala.Function1[A, Boolean]]): Rep[WOption[A]] = xs.filter(f);
      def isDefined[A](xs: Rep[WOption[A]]): Rep[Boolean] = xs.isDefined;
      def isEmpty[A](xs: Rep[WOption[A]]): Rep[Boolean] = xs.isEmpty;
      @NeverInline def fold[A, B](xs: Rep[WOption[A]], ifEmpty: Rep[Thunk[B]], f: Rep[scala.Function1[A, B]]): Rep[B] = delayInvoke
    };
    trait EitherWrapSpec extends WrapSpecBase {
      def fold[A, B, C](xs: Rep[WEither[A, B]], fa: Rep[scala.Function1[A, C]], fb: Rep[scala.Function1[B, C]]): Rep[C] = xs.fold[C](fa, fb);
      def cond[A, B](c: Rep[Boolean], a: Rep[Thunk[A]], b: Rep[Thunk[B]]): Rep[WEither[A, B]] = RWEither.cond[A, B](c, b, a)
    };
    trait SpecialPredefWrapSpec extends WrapSpecBase {
      def loopUntil[A](s1: Rep[A], isMatch: Rep[scala.Function1[A, Boolean]], step: Rep[scala.Function1[A, A]]): Rep[A] = RWSpecialPredef.loopUntil[A](s1, isMatch, step);
      def cast[A](v: Rep[Any])(implicit cA: Elem[A]): Rep[WOption[A]] = RWSpecialPredef.cast[A](v);
      def mapSum[A, B, C, D](e: Rep[WEither[A, B]], fa: Rep[scala.Function1[A, C]], fb: Rep[scala.Function1[B, D]]): Rep[WEither[C, D]] = RWSpecialPredef.eitherMap[A, B, C, D](e, fa, fb);
      def some[A](x: Rep[A]): Rep[WOption[A]] = RWSpecialPredef.some[A](x);
      def none[A](implicit cA: Elem[A]): Rep[WOption[A]] = RWSpecialPredef.none[A];
      def left[A, B](a: Rep[A])(implicit cB: Elem[B]): Rep[WEither[A, B]] = RWSpecialPredef.left[A, B](a);
      def right[A, B](b: Rep[B])(implicit cA: Elem[A]): Rep[WEither[A, B]] = RWSpecialPredef.right[A, B](b);
      def optionGetOrElse[A](opt: Rep[WOption[A]], default: Rep[A]): Rep[A] = RWSpecialPredef.optionGetOrElse[A](opt, default)
    };
    trait RTypeWrapSpec extends WrapSpecBase {
      def name[T](d: Rep[WRType[T]]): Rep[String] = d.name
    };
    trait WrapSpecBaseCompanion;
    trait ArrayWrapSpecCompanion;
    trait OptionWrapSpecCompanion;
    trait EitherWrapSpecCompanion;
    trait SpecialPredefWrapSpecCompanion;
    trait RTypeWrapSpecCompanion
  }
}