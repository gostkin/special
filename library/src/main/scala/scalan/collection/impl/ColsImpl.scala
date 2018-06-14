package scalan.collection

import scalan._
import scala.reflect.runtime.universe._
import scala.reflect._

package impl {
// Abs -----------------------------------
trait ColsDefs extends scalan.Scalan with Cols {
  self: Library =>

  // entityProxy: single proxy for each type family
  implicit def proxyCol[A](p: Rep[Col[A]]): Col[A] = {
    proxyOps[Col[A]](p)(scala.reflect.classTag[Col[A]])
  }

  // familyElem
  class ColElem[A, To <: Col[A]](implicit _eA: Elem[A])
    extends EntityElem[To] {
    def eA = _eA
    lazy val parent: Option[Elem[_]] = None
    override def buildTypeArgs = super.buildTypeArgs ++ TypeArgs("A" -> (eA -> scalan.util.Invariant))
    override lazy val tag = {
      implicit val tagA = eA.tag
      weakTypeTag[Col[A]].asInstanceOf[WeakTypeTag[To]]
    }
    override def convert(x: Rep[Def[_]]) = {
      val conv = fun {x: Rep[Col[A]] => convertCol(x) }
      tryConvert(element[Col[A]], this, x, conv)
    }

    def convertCol(x: Rep[Col[A]]): Rep[To] = {
      x.elem match {
        case _: ColElem[_, _] => x.asRep[To]
        case e => !!!(s"Expected $x to have ColElem[_, _], but got $e", x)
      }
    }
    override def getDefaultRep: Rep[To] = ???
  }

  implicit def colElement[A](implicit eA: Elem[A]): Elem[Col[A]] =
    cachedElem[ColElem[A, Col[A]]](eA)

  implicit case object ColCompanionElem extends CompanionElem[ColCompanionCtor] {
    lazy val tag = weakTypeTag[ColCompanionCtor]
    protected def getDefaultRep = Col
  }

  abstract class ColCompanionCtor extends CompanionDef[ColCompanionCtor] with ColCompanion {
    def selfType = ColCompanionElem
    override def toString = "Col"
  }
  implicit def proxyColCompanionCtor(p: Rep[ColCompanionCtor]): ColCompanionCtor =
    proxyOps[ColCompanionCtor](p)

  lazy val Col: Rep[ColCompanionCtor] = new ColCompanionCtor {
  }

  object ColMethods {
    object builder {
      def unapply(d: Def[_]): Option[Rep[Col[A]] forSome {type A}] = d match {
        case MethodCall(receiver, method, _, _) if receiver.elem.isInstanceOf[ColElem[_, _]] && method.getName == "builder" =>
          Some(receiver).asInstanceOf[Option[Rep[Col[A]] forSome {type A}]]
        case _ => None
      }
      def unapply(exp: Sym): Option[Rep[Col[A]] forSome {type A}] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }

    object arr {
      def unapply(d: Def[_]): Option[Rep[Col[A]] forSome {type A}] = d match {
        case MethodCall(receiver, method, _, _) if receiver.elem.isInstanceOf[ColElem[_, _]] && method.getName == "arr" =>
          Some(receiver).asInstanceOf[Option[Rep[Col[A]] forSome {type A}]]
        case _ => None
      }
      def unapply(exp: Sym): Option[Rep[Col[A]] forSome {type A}] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }

    object length {
      def unapply(d: Def[_]): Option[Rep[Col[A]] forSome {type A}] = d match {
        case MethodCall(receiver, method, _, _) if receiver.elem.isInstanceOf[ColElem[_, _]] && method.getName == "length" =>
          Some(receiver).asInstanceOf[Option[Rep[Col[A]] forSome {type A}]]
        case _ => None
      }
      def unapply(exp: Sym): Option[Rep[Col[A]] forSome {type A}] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }

    object apply {
      def unapply(d: Def[_]): Option[(Rep[Col[A]], Rep[Int]) forSome {type A}] = d match {
        case MethodCall(receiver, method, Seq(i, _*), _) if receiver.elem.isInstanceOf[ColElem[_, _]] && method.getName == "apply" =>
          Some((receiver, i)).asInstanceOf[Option[(Rep[Col[A]], Rep[Int]) forSome {type A}]]
        case _ => None
      }
      def unapply(exp: Sym): Option[(Rep[Col[A]], Rep[Int]) forSome {type A}] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }

    object map {
      def unapply(d: Def[_]): Option[(Rep[Col[A]], Rep[A => B]) forSome {type A; type B}] = d match {
        case MethodCall(receiver, method, Seq(f, _*), _) if receiver.elem.isInstanceOf[ColElem[_, _]] && method.getName == "map" =>
          Some((receiver, f)).asInstanceOf[Option[(Rep[Col[A]], Rep[A => B]) forSome {type A; type B}]]
        case _ => None
      }
      def unapply(exp: Sym): Option[(Rep[Col[A]], Rep[A => B]) forSome {type A; type B}] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }

    object zip {
      def unapply(d: Def[_]): Option[(Rep[Col[A]], Rep[Col[B]]) forSome {type A; type B}] = d match {
        case MethodCall(receiver, method, Seq(ys, _*), _) if receiver.elem.isInstanceOf[ColElem[_, _]] && method.getName == "zip" =>
          Some((receiver, ys)).asInstanceOf[Option[(Rep[Col[A]], Rep[Col[B]]) forSome {type A; type B}]]
        case _ => None
      }
      def unapply(exp: Sym): Option[(Rep[Col[A]], Rep[Col[B]]) forSome {type A; type B}] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }

    object foreach {
      def unapply(d: Def[_]): Option[(Rep[Col[A]], Rep[A => Unit]) forSome {type A}] = d match {
        case MethodCall(receiver, method, Seq(f, _*), _) if receiver.elem.isInstanceOf[ColElem[_, _]] && method.getName == "foreach" =>
          Some((receiver, f)).asInstanceOf[Option[(Rep[Col[A]], Rep[A => Unit]) forSome {type A}]]
        case _ => None
      }
      def unapply(exp: Sym): Option[(Rep[Col[A]], Rep[A => Unit]) forSome {type A}] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }

    object exists {
      def unapply(d: Def[_]): Option[(Rep[Col[A]], Rep[A => Boolean]) forSome {type A}] = d match {
        case MethodCall(receiver, method, Seq(p, _*), _) if receiver.elem.isInstanceOf[ColElem[_, _]] && method.getName == "exists" =>
          Some((receiver, p)).asInstanceOf[Option[(Rep[Col[A]], Rep[A => Boolean]) forSome {type A}]]
        case _ => None
      }
      def unapply(exp: Sym): Option[(Rep[Col[A]], Rep[A => Boolean]) forSome {type A}] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }

    object forall {
      def unapply(d: Def[_]): Option[(Rep[Col[A]], Rep[A => Boolean]) forSome {type A}] = d match {
        case MethodCall(receiver, method, Seq(p, _*), _) if receiver.elem.isInstanceOf[ColElem[_, _]] && method.getName == "forall" =>
          Some((receiver, p)).asInstanceOf[Option[(Rep[Col[A]], Rep[A => Boolean]) forSome {type A}]]
        case _ => None
      }
      def unapply(exp: Sym): Option[(Rep[Col[A]], Rep[A => Boolean]) forSome {type A}] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }

    object filter {
      def unapply(d: Def[_]): Option[(Rep[Col[A]], Rep[A => Boolean]) forSome {type A}] = d match {
        case MethodCall(receiver, method, Seq(p, _*), _) if receiver.elem.isInstanceOf[ColElem[_, _]] && method.getName == "filter" =>
          Some((receiver, p)).asInstanceOf[Option[(Rep[Col[A]], Rep[A => Boolean]) forSome {type A}]]
        case _ => None
      }
      def unapply(exp: Sym): Option[(Rep[Col[A]], Rep[A => Boolean]) forSome {type A}] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }

    object fold {
      def unapply(d: Def[_]): Option[(Rep[Col[A]], Rep[B], Rep[((B, A)) => B]) forSome {type A; type B}] = d match {
        case MethodCall(receiver, method, Seq(zero, op, _*), _) if receiver.elem.isInstanceOf[ColElem[_, _]] && method.getName == "fold" =>
          Some((receiver, zero, op)).asInstanceOf[Option[(Rep[Col[A]], Rep[B], Rep[((B, A)) => B]) forSome {type A; type B}]]
        case _ => None
      }
      def unapply(exp: Sym): Option[(Rep[Col[A]], Rep[B], Rep[((B, A)) => B]) forSome {type A; type B}] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }

    object slice {
      def unapply(d: Def[_]): Option[(Rep[Col[A]], Rep[Int], Rep[Int]) forSome {type A}] = d match {
        case MethodCall(receiver, method, Seq(from, until, _*), _) if receiver.elem.isInstanceOf[ColElem[_, _]] && method.getName == "slice" =>
          Some((receiver, from, until)).asInstanceOf[Option[(Rep[Col[A]], Rep[Int], Rep[Int]) forSome {type A}]]
        case _ => None
      }
      def unapply(exp: Sym): Option[(Rep[Col[A]], Rep[Int], Rep[Int]) forSome {type A}] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }
  }

  object ColCompanionMethods {
  }

  // entityProxy: single proxy for each type family
  implicit def proxyPairCol[L, R](p: Rep[PairCol[L, R]]): PairCol[L, R] = {
    proxyOps[PairCol[L, R]](p)(scala.reflect.classTag[PairCol[L, R]])
  }

  // familyElem
  class PairColElem[L, R, To <: PairCol[L, R]](implicit _eL: Elem[L], _eR: Elem[R])
    extends ColElem[(L, R), To] {
    def eL = _eL
    def eR = _eR
    override lazy val parent: Option[Elem[_]] = Some(colElement(pairElement(element[L],element[R])))
    override def buildTypeArgs = super.buildTypeArgs ++ TypeArgs("L" -> (eL -> scalan.util.Invariant), "R" -> (eR -> scalan.util.Invariant))
    override lazy val tag = {
      implicit val tagL = eL.tag
      implicit val tagR = eR.tag
      weakTypeTag[PairCol[L, R]].asInstanceOf[WeakTypeTag[To]]
    }
    override def convert(x: Rep[Def[_]]) = {
      val conv = fun {x: Rep[PairCol[L, R]] => convertPairCol(x) }
      tryConvert(element[PairCol[L, R]], this, x, conv)
    }

    def convertPairCol(x: Rep[PairCol[L, R]]): Rep[To] = {
      x.elem match {
        case _: PairColElem[_, _, _] => x.asRep[To]
        case e => !!!(s"Expected $x to have PairColElem[_, _, _], but got $e", x)
      }
    }
    override def getDefaultRep: Rep[To] = ???
  }

  implicit def pairColElement[L, R](implicit eL: Elem[L], eR: Elem[R]): Elem[PairCol[L, R]] =
    cachedElem[PairColElem[L, R, PairCol[L, R]]](eL, eR)

  implicit case object PairColCompanionElem extends CompanionElem[PairColCompanionCtor] {
    lazy val tag = weakTypeTag[PairColCompanionCtor]
    protected def getDefaultRep = PairCol
  }

  abstract class PairColCompanionCtor extends CompanionDef[PairColCompanionCtor] with PairColCompanion {
    def selfType = PairColCompanionElem
    override def toString = "PairCol"
  }
  implicit def proxyPairColCompanionCtor(p: Rep[PairColCompanionCtor]): PairColCompanionCtor =
    proxyOps[PairColCompanionCtor](p)

  lazy val PairCol: Rep[PairColCompanionCtor] = new PairColCompanionCtor {
  }

  object PairColMethods {
    object ls {
      def unapply(d: Def[_]): Option[Rep[PairCol[L, R]] forSome {type L; type R}] = d match {
        case MethodCall(receiver, method, _, _) if receiver.elem.isInstanceOf[PairColElem[_, _, _]] && method.getName == "ls" =>
          Some(receiver).asInstanceOf[Option[Rep[PairCol[L, R]] forSome {type L; type R}]]
        case _ => None
      }
      def unapply(exp: Sym): Option[Rep[PairCol[L, R]] forSome {type L; type R}] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }

    object rs {
      def unapply(d: Def[_]): Option[Rep[PairCol[L, R]] forSome {type L; type R}] = d match {
        case MethodCall(receiver, method, _, _) if receiver.elem.isInstanceOf[PairColElem[_, _, _]] && method.getName == "rs" =>
          Some(receiver).asInstanceOf[Option[Rep[PairCol[L, R]] forSome {type L; type R}]]
        case _ => None
      }
      def unapply(exp: Sym): Option[Rep[PairCol[L, R]] forSome {type L; type R}] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }
  }

  object PairColCompanionMethods {
  }

  // entityProxy: single proxy for each type family
  implicit def proxyColBuilder(p: Rep[ColBuilder]): ColBuilder = {
    proxyOps[ColBuilder](p)(scala.reflect.classTag[ColBuilder])
  }

  // familyElem
  class ColBuilderElem[To <: ColBuilder]
    extends EntityElem[To] {
    lazy val parent: Option[Elem[_]] = None
    override def buildTypeArgs = super.buildTypeArgs ++ TypeArgs()
    override lazy val tag = {
      weakTypeTag[ColBuilder].asInstanceOf[WeakTypeTag[To]]
    }
    override def convert(x: Rep[Def[_]]) = {
      val conv = fun {x: Rep[ColBuilder] => convertColBuilder(x) }
      tryConvert(element[ColBuilder], this, x, conv)
    }

    def convertColBuilder(x: Rep[ColBuilder]): Rep[To] = {
      x.elem match {
        case _: ColBuilderElem[_] => x.asRep[To]
        case e => !!!(s"Expected $x to have ColBuilderElem[_], but got $e", x)
      }
    }
    override def getDefaultRep: Rep[To] = ???
  }

  implicit def colBuilderElement: Elem[ColBuilder] =
    cachedElem[ColBuilderElem[ColBuilder]]()

  implicit case object ColBuilderCompanionElem extends CompanionElem[ColBuilderCompanionCtor] {
    lazy val tag = weakTypeTag[ColBuilderCompanionCtor]
    protected def getDefaultRep = ColBuilder
  }

  abstract class ColBuilderCompanionCtor extends CompanionDef[ColBuilderCompanionCtor] with ColBuilderCompanion {
    def selfType = ColBuilderCompanionElem
    override def toString = "ColBuilder"
  }
  implicit def proxyColBuilderCompanionCtor(p: Rep[ColBuilderCompanionCtor]): ColBuilderCompanionCtor =
    proxyOps[ColBuilderCompanionCtor](p)

  lazy val ColBuilder: Rep[ColBuilderCompanionCtor] = new ColBuilderCompanionCtor {
  }

  object ColBuilderMethods {
    object apply_apply {
      def unapply(d: Def[_]): Option[(Rep[ColBuilder], Rep[Col[A]], Rep[Col[B]]) forSome {type A; type B}] = d match {
        case MethodCall(receiver, method, Seq(as, bs, _*), _) if receiver.elem.isInstanceOf[ColBuilderElem[_]] && method.getName == "apply" && { val ann = method.getAnnotation(classOf[scalan.OverloadId]); ann != null && ann.value == "apply" } =>
          Some((receiver, as, bs)).asInstanceOf[Option[(Rep[ColBuilder], Rep[Col[A]], Rep[Col[B]]) forSome {type A; type B}]]
        case _ => None
      }
      def unapply(exp: Sym): Option[(Rep[ColBuilder], Rep[Col[A]], Rep[Col[B]]) forSome {type A; type B}] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }

    // WARNING: Cannot generate matcher for method `apply`: Method has repeated argument items

    object fromItemsTest {
      def unapply(d: Def[_]): Option[Rep[ColBuilder]] = d match {
        case MethodCall(receiver, method, _, _) if receiver.elem.isInstanceOf[ColBuilderElem[_]] && method.getName == "fromItemsTest" =>
          Some(receiver).asInstanceOf[Option[Rep[ColBuilder]]]
        case _ => None
      }
      def unapply(exp: Sym): Option[Rep[ColBuilder]] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }

    object fromArray {
      def unapply(d: Def[_]): Option[(Rep[ColBuilder], Rep[WArray[T]]) forSome {type T}] = d match {
        case MethodCall(receiver, method, Seq(arr, _*), _) if receiver.elem.isInstanceOf[ColBuilderElem[_]] && method.getName == "fromArray" =>
          Some((receiver, arr)).asInstanceOf[Option[(Rep[ColBuilder], Rep[WArray[T]]) forSome {type T}]]
        case _ => None
      }
      def unapply(exp: Sym): Option[(Rep[ColBuilder], Rep[WArray[T]]) forSome {type T}] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }

    object replicate {
      def unapply(d: Def[_]): Option[(Rep[ColBuilder], Rep[Int], Rep[T]) forSome {type T}] = d match {
        case MethodCall(receiver, method, Seq(n, v, _*), _) if receiver.elem.isInstanceOf[ColBuilderElem[_]] && method.getName == "replicate" =>
          Some((receiver, n, v)).asInstanceOf[Option[(Rep[ColBuilder], Rep[Int], Rep[T]) forSome {type T}]]
        case _ => None
      }
      def unapply(exp: Sym): Option[(Rep[ColBuilder], Rep[Int], Rep[T]) forSome {type T}] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }

    object dot {
      def unapply(d: Def[_]): Option[(Rep[ColBuilder], Rep[Col[T]], Rep[Col[T]]) forSome {type T}] = d match {
        case MethodCall(receiver, method, Seq(xs, ys, _*), _) if receiver.elem.isInstanceOf[ColBuilderElem[_]] && method.getName == "dot" =>
          Some((receiver, xs, ys)).asInstanceOf[Option[(Rep[ColBuilder], Rep[Col[T]], Rep[Col[T]]) forSome {type T}]]
        case _ => None
      }
      def unapply(exp: Sym): Option[(Rep[ColBuilder], Rep[Col[T]], Rep[Col[T]]) forSome {type T}] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }

    object ddmvm {
      def unapply(d: Def[_]): Option[(Rep[ColBuilder], Rep[WArray[Double]])] = d match {
        case MethodCall(receiver, method, Seq(v, _*), _) if receiver.elem.isInstanceOf[ColBuilderElem[_]] && method.getName == "ddmvm" =>
          Some((receiver, v)).asInstanceOf[Option[(Rep[ColBuilder], Rep[WArray[Double]])]]
        case _ => None
      }
      def unapply(exp: Sym): Option[(Rep[ColBuilder], Rep[WArray[Double]])] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }

    object functorArg {
      def unapply(d: Def[_]): Option[(Rep[ColBuilder], Rep[WArray[Double]], Functor[WArray])] = d match {
        case MethodCall(receiver, method, Seq(arr, evF, _*), _) if receiver.elem.isInstanceOf[ColBuilderElem[_]] && method.getName == "functorArg" =>
          Some((receiver, arr, evF)).asInstanceOf[Option[(Rep[ColBuilder], Rep[WArray[Double]], Functor[WArray])]]
        case _ => None
      }
      def unapply(exp: Sym): Option[(Rep[ColBuilder], Rep[WArray[Double]], Functor[WArray])] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }
  }

  object ColBuilderCompanionMethods {
  }

  // entityProxy: single proxy for each type family
  implicit def proxyFunctor[F[_]](p: Rep[Functor[F]]): Functor[F] = {
    proxyOps[Functor[F]](p)(scala.reflect.classTag[Functor[F]])
  }

  // familyElem
  class FunctorElem[F[_], To <: Functor[F]](implicit _cF: Cont[F])
    extends EntityElem[To] {
    def cF = _cF
    lazy val parent: Option[Elem[_]] = None
    override def buildTypeArgs = super.buildTypeArgs ++ TypeArgs("F" -> (cF -> scalan.util.Invariant))
    override lazy val tag = {
      weakTypeTag[Functor[F]].asInstanceOf[WeakTypeTag[To]]
    }
    override def convert(x: Rep[Def[_]]) = {
      val conv = fun {x: Rep[Functor[F]] => convertFunctor(x) }
      tryConvert(element[Functor[F]], this, x, conv)
    }

    def convertFunctor(x: Rep[Functor[F]]): Rep[To] = {
      x.elem.asInstanceOf[Elem[_]] match {
        case _: FunctorElem[_, _] => x.asRep[To]
        case e => !!!(s"Expected $x to have FunctorElem[_, _], but got $e", x)
      }
    }
    override def getDefaultRep: Rep[To] = ???
  }

  implicit def functorElement[F[_]](implicit cF: Cont[F]): Elem[Functor[F]] =
    cachedElem[FunctorElem[F, Functor[F]]](cF)

  implicit case object FunctorCompanionElem extends CompanionElem[FunctorCompanionCtor] {
    lazy val tag = weakTypeTag[FunctorCompanionCtor]
    protected def getDefaultRep = Functor
  }

  abstract class FunctorCompanionCtor extends CompanionDef[FunctorCompanionCtor] with FunctorCompanion {
    def selfType = FunctorCompanionElem
    override def toString = "Functor"
  }
  implicit def proxyFunctorCompanionCtor(p: Rep[FunctorCompanionCtor]): FunctorCompanionCtor =
    proxyOps[FunctorCompanionCtor](p)

  lazy val Functor: Rep[FunctorCompanionCtor] = new FunctorCompanionCtor {
  }

  object FunctorMethods {
    object map {
      def unapply(d: Def[_]): Option[(Rep[Functor[F]], Rep[F[A]], Rep[A => B]) forSome {type F[_]; type A; type B}] = d match {
        case MethodCall(receiver, method, Seq(fa, f, _*), _) if (receiver.elem.asInstanceOf[Elem[_]] match { case _: FunctorElem[_, _] => true; case _ => false }) && method.getName == "map" =>
          Some((receiver, fa, f)).asInstanceOf[Option[(Rep[Functor[F]], Rep[F[A]], Rep[A => B]) forSome {type F[_]; type A; type B}]]
        case _ => None
      }
      def unapply(exp: Sym): Option[(Rep[Functor[F]], Rep[F[A]], Rep[A => B]) forSome {type F[_]; type A; type B}] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }
  }

  object FunctorCompanionMethods {
  }

  // entityProxy: single proxy for each type family
  implicit def proxyEnum(p: Rep[Enum]): Enum = {
    proxyOps[Enum](p)(scala.reflect.classTag[Enum])
  }

  // familyElem
  class EnumElem[To <: Enum]
    extends EntityElem[To] {
    lazy val parent: Option[Elem[_]] = None
    override def buildTypeArgs = super.buildTypeArgs ++ TypeArgs()
    override lazy val tag = {
      weakTypeTag[Enum].asInstanceOf[WeakTypeTag[To]]
    }
    override def convert(x: Rep[Def[_]]) = {
      val conv = fun {x: Rep[Enum] => convertEnum(x) }
      tryConvert(element[Enum], this, x, conv)
    }

    def convertEnum(x: Rep[Enum]): Rep[To] = {
      x.elem match {
        case _: EnumElem[_] => x.asRep[To]
        case e => !!!(s"Expected $x to have EnumElem[_], but got $e", x)
      }
    }
    override def getDefaultRep: Rep[To] = ???
  }

  implicit def enumElement: Elem[Enum] =
    cachedElem[EnumElem[Enum]]()

  implicit case object EnumCompanionElem extends CompanionElem[EnumCompanionCtor] {
    lazy val tag = weakTypeTag[EnumCompanionCtor]
    protected def getDefaultRep = Enum
  }

  abstract class EnumCompanionCtor extends CompanionDef[EnumCompanionCtor] with EnumCompanion {
    def selfType = EnumCompanionElem
    override def toString = "Enum"
  }
  implicit def proxyEnumCompanionCtor(p: Rep[EnumCompanionCtor]): EnumCompanionCtor =
    proxyOps[EnumCompanionCtor](p)

  lazy val Enum: Rep[EnumCompanionCtor] = new EnumCompanionCtor {
  }

  object EnumMethods {
    object value {
      def unapply(d: Def[_]): Option[Rep[Enum]] = d match {
        case MethodCall(receiver, method, _, _) if receiver.elem.isInstanceOf[EnumElem[_]] && method.getName == "value" =>
          Some(receiver).asInstanceOf[Option[Rep[Enum]]]
        case _ => None
      }
      def unapply(exp: Sym): Option[Rep[Enum]] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }
  }

  object EnumCompanionMethods {
  }

  registerModule(ColsModule)
}

object ColsModule extends scalan.ModuleInfo("scalan.collection", "Cols")
}

trait ColsModule extends scalan.collection.impl.ColsDefs {self: Library =>}
