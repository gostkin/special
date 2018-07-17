package scala.util {
  import special.wrappers.WrappersModule

  import scalan._
  import impl._

  trait WEithers extends Base { self: WrappersModule =>
    @External("Either") trait WEither[A, B] extends Def[WEither[A, B]] {
      implicit def eA: Elem[A];
      implicit def eB: Elem[B];
      @External def fold[C](fa: Rep[scala.Function1[A, C]], fb: Rep[scala.Function1[B, C]]): Rep[C]
    };
    trait WEitherCompanion {
      @External def cond[A, B](test: Rep[Boolean], right: Rep[Thunk[B]], left: Rep[Thunk[A]]): Rep[WEither[A, B]]
    }
  }
}