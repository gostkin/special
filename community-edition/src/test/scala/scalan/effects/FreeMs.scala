package scalan.effects

import scalan._
import scala.reflect.runtime.universe._
import scalan.monads.{MonadsDslExp, MonadsDslSeq, Monads, MonadsDsl}

trait FreeMs extends Base { self: MonadsDsl =>

  type RFreeM[F[_],A] = Rep[FreeM[F,A]]
  sealed trait FreeM[F[_], A] extends Reifiable[FreeM[F,A]] {
    implicit def eA: Elem[A]
    implicit def cF: Cont[F]

    def flatMap[B:Elem](f: Rep[A] => Rep[FreeM[F,B]]): Rep[FreeM[F,B]] = flatMapBy(fun(f))
    def flatMapBy[B:Elem](f: Rep[A => FreeM[F,B]]): Rep[FreeM[F,B]] = FlatMap(self, f)

    def mapBy[B:Elem](f: Rep[A => B]): Rep[FreeM[F,B]] =
      flatMap(a => Done(f(a)))

    def map[B:Elem](f: Rep[A] => Rep[B]): Rep[FreeM[F,B]] = mapBy(fun(f))

    def resume(implicit F: Functor[F]): Rep[F[FreeM[F,A]] | A]
//    =
//      self.selfType1.asInstanceOf[FreeMElem[F,_,_]] match {
//          case r: ReturnElem[F,A] => self.asRep[Return[F,A]].a.asRight[F[A]]
//          case s: SuspendElem[F,A] => self.asRep[Suspend[F,A]].a.asLeft[A]
//          case b: BindElem[F,s,A] =>
//            val b = seld.asRep[Bind[F,s,A]]
//
//      }
  }
  trait FreeMCompanion

  abstract class Done[F[_], A]
        (val a: Rep[A])
        (implicit val eA: Elem[A], val cF: Cont[F]) extends FreeM[F,A]
  {
    override def flatMap[B:Elem](f: Rep[A] => Rep[FreeM[F,B]]): Rep[FreeM[F,B]] = f(a)
    override def flatMapBy[B:Elem](f: Rep[A => FreeM[F,B]]): Rep[FreeM[F,B]] = f(a)
    def resume(implicit F: Functor[F]): Rep[F[FreeM[F,A]] | A] = a.asRight[F[FreeM[F,A]]]
  }
  trait DoneCompanion

  abstract class More[F[_],A]
        (val k: Rep[F[FreeM[F,A]]])
        (implicit val eA: Elem[A], val cF: Cont[F]) extends FreeM[F, A] {

    def resume(implicit F: Functor[F]): Rep[F[FreeM[F,A]] | A] = k.asLeft[A]
  }
  trait MoreCompanion

  abstract class FlatMap[F[_],S,B]
        (val a: Rep[FreeM[F, S]], val f: Rep[S => FreeM[F,B]])
        (implicit val eS: Elem[S], val eA: Elem[B], val cF: Cont[F]) extends FreeM[F,B] {

    override def flatMap[R:Elem](f1: Rep[B] => Rep[FreeM[F,R]]): Rep[FreeM[F,R]] =
      a.flatMap((s: Rep[S]) => f(s).flatMap(f1))
    override def flatMapBy[R:Elem](f1: Rep[B => FreeM[F,R]]): Rep[FreeM[F,R]] =
      a.flatMap((s: Rep[S]) => f(s).flatMapBy(f1))

    def resume(implicit F: Functor[F]): Rep[F[FreeM[F,B]] | B] = a.selfType1.asInstanceOf[FreeMElem[F, _, _]] match {
      case r: DoneElem[F,S] => f(a.asRep[Done[F,S]].a).resume
      case s: MoreElem[F,S] => F.map(a.asRep[More[F,S]].k)(fs => fs.flatMapBy(f)).asLeft[B]
      case fm: FlatMapElem[F,s,S] =>
        val fm = a.asRep[FlatMap[F,s,S]]
        fm.a.flatMap((x: Rep[s]) => fm.f(x) flatMapBy f).resume
    }
  }
  trait FlatMapCompanion

}

trait FreeMsDsl extends ScalanDsl with impl.FreeMsAbs with FreeMs with Monads { self: MonadsDsl =>

  implicit def freeMCont[F[_]:Cont]: Cont[({type f[x] = FreeM[F,x]})#f] = new Container[({type f[x] = FreeM[F,x]})#f] {
    def tag[T](implicit tT: WeakTypeTag[T]) = weakTypeTag[FreeM[F,T]]
    def lift[T](implicit eT: Elem[T]) = element[FreeM[F,T]]
  }

  def freeMMonad[F[_]:Cont]: Monad[({type f[a] = FreeM[F,a]})#f] =
    new Monad[({type f[a] = FreeM[F,a]})#f] {
      // suppress implicit resolution of this method (as it leads to stackoverflow)
      override def toMonadic[A: Elem](a: Rep[FreeM[F, A]]) = super.toMonadic(a)

      def cF = freeMCont
      def unit[A:Elem](a: Rep[A]) = Done(a)
      override def flatMap[A:Elem,B:Elem](fa: Rep[FreeM[F, A]])(f: Rep[A] => Rep[FreeM[F, B]]) = fa flatMap f
    }

}


trait FreeMsDslSeq extends FreeMsDsl with impl.FreeMsSeq with ScalanCtxSeq  { self: MonadsDslSeq =>

}

trait FreeMsDslExp extends FreeMsDsl with impl.FreeMsExp with ScalanExp { self: MonadsDslExp =>


}