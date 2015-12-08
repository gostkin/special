package scalan.util

import scalan._
import scalan.common.Default
import scala.reflect.runtime.universe.{WeakTypeTag, weakTypeTag}
import scalan.meta.ScalanAst._

package impl {
// Abs -----------------------------------
trait ExceptionsAbs extends scalan.Scalan with Exceptions {
  self: ExceptionsDsl =>

  // single proxy for each type family
  implicit def proxySThrowable(p: Rep[SThrowable]): SThrowable = {
    proxyOps[SThrowable](p)(scala.reflect.classTag[SThrowable])
  }

  // TypeWrapper proxy
  //implicit def proxyThrowable(p: Rep[Throwable]): SThrowable =
  //  proxyOps[SThrowable](p.asRep[SThrowable])

  implicit def unwrapValueOfSThrowable(w: Rep[SThrowable]): Rep[Throwable] = w.wrappedValue

  implicit lazy val throwableElement: Elem[Throwable] =
    element[SThrowable].asInstanceOf[WrapperElem[_, _]].baseElem.asInstanceOf[Elem[Throwable]]

  // familyElem
  class SThrowableElem[To <: SThrowable]
    extends WrapperElem[Throwable, To] {
    lazy val parent: Option[Elem[_]] = None
    lazy val tyArgSubst: Map[String, TypeDesc] = {
      Map()
    }
    override def isEntityType = true
    override lazy val tag = {
      weakTypeTag[SThrowable].asInstanceOf[WeakTypeTag[To]]
    }
    override def convert(x: Rep[Def[_]]) = {
      implicit val eTo: Elem[To] = this
      val conv = fun {x: Rep[SThrowable] => convertSThrowable(x) }
      tryConvert(element[SThrowable], this, x, conv)
    }

    def convertSThrowable(x: Rep[SThrowable]): Rep[To] = {
      x.selfType1 match {
        case _: SThrowableElem[_] => x.asRep[To]
        case e => !!!(s"Expected $x to have SThrowableElem[_], but got $e", x)
      }
    }
    lazy val baseElem = {
      new BaseTypeElem[Throwable, SThrowable](this.asInstanceOf[Elem[SThrowable]])(weakTypeTag[Throwable], DefaultOfThrowable)
    }
    lazy val eTo: Elem[_] = new SThrowableImplElem(isoSThrowableImpl)
    override def getDefaultRep: Rep[To] = ???
  }

  implicit def sThrowableElement: Elem[SThrowable] =
    elemCache.getOrElseUpdate(
      (classOf[SThrowableElem[SThrowable]], Nil),
      new SThrowableElem[SThrowable]).asInstanceOf[Elem[SThrowable]]

  implicit case object SThrowableCompanionElem extends CompanionElem[SThrowableCompanionAbs] {
    lazy val tag = weakTypeTag[SThrowableCompanionAbs]
    protected def getDefaultRep = SThrowable
  }

  abstract class SThrowableCompanionAbs extends CompanionDef[SThrowableCompanionAbs] with SThrowableCompanion {
    def selfType = SThrowableCompanionElem
    override def toString = "SThrowable"
  }
  def SThrowable: Rep[SThrowableCompanionAbs]
  implicit def proxySThrowableCompanionAbs(p: Rep[SThrowableCompanionAbs]): SThrowableCompanionAbs =
    proxyOps[SThrowableCompanionAbs](p)

  // default wrapper implementation
  abstract class SThrowableImpl(val wrappedValue: Rep[Throwable]) extends SThrowable with Def[SThrowableImpl] {
    lazy val selfType = element[SThrowableImpl]

    def getMessage: Rep[String] =
      methodCallEx[String](self,
        this.getClass.getMethod("getMessage"),
        List())

    def initCause(cause: Rep[SThrowable]): Rep[SThrowable] =
      methodCallEx[SThrowable](self,
        this.getClass.getMethod("initCause", classOf[AnyRef]),
        List(cause.asInstanceOf[AnyRef]))
  }
  trait SThrowableImplCompanion
  // elem for concrete class
  class SThrowableImplElem(val iso: Iso[SThrowableImplData, SThrowableImpl])
    extends SThrowableElem[SThrowableImpl]
    with ConcreteElem[SThrowableImplData, SThrowableImpl] {
    override lazy val parent: Option[Elem[_]] = Some(sThrowableElement)
    override lazy val tyArgSubst: Map[String, TypeDesc] = {
      Map()
    }
    override lazy val eTo: Elem[_] = this
    override def convertSThrowable(x: Rep[SThrowable]) = // Converter is not generated by meta
!!!("Cannot convert from SThrowable to SThrowableImpl: missing fields List(wrappedValue)")
    override def getDefaultRep = SThrowableImpl(DefaultOfThrowable.value)
    override lazy val tag = {
      weakTypeTag[SThrowableImpl]
    }
  }

  // state representation type
  type SThrowableImplData = Throwable

  // 3) Iso for concrete class
  class SThrowableImplIso
    extends EntityIso[SThrowableImplData, SThrowableImpl] with Def[SThrowableImplIso] {
    override def from(p: Rep[SThrowableImpl]) =
      p.wrappedValue
    override def to(p: Rep[Throwable]) = {
      val wrappedValue = p
      SThrowableImpl(wrappedValue)
    }
    lazy val eFrom = element[Throwable]
    lazy val eTo = new SThrowableImplElem(self)
    lazy val selfType = new SThrowableImplIsoElem
    def productArity = 0
    def productElement(n: Int) = ???
  }
  case class SThrowableImplIsoElem() extends Elem[SThrowableImplIso] {
    def isEntityType = true
    def getDefaultRep = reifyObject(new SThrowableImplIso())
    lazy val tag = {
      weakTypeTag[SThrowableImplIso]
    }
  }
  // 4) constructor and deconstructor
  class SThrowableImplCompanionAbs extends CompanionDef[SThrowableImplCompanionAbs] {
    def selfType = SThrowableImplCompanionElem
    override def toString = "SThrowableImpl"

    def apply(wrappedValue: Rep[Throwable]): Rep[SThrowableImpl] =
      mkSThrowableImpl(wrappedValue)
  }
  object SThrowableImplMatcher {
    def unapply(p: Rep[SThrowable]) = unmkSThrowableImpl(p)
  }
  lazy val SThrowableImpl: Rep[SThrowableImplCompanionAbs] = new SThrowableImplCompanionAbs
  implicit def proxySThrowableImplCompanion(p: Rep[SThrowableImplCompanionAbs]): SThrowableImplCompanionAbs = {
    proxyOps[SThrowableImplCompanionAbs](p)
  }

  implicit case object SThrowableImplCompanionElem extends CompanionElem[SThrowableImplCompanionAbs] {
    lazy val tag = weakTypeTag[SThrowableImplCompanionAbs]
    protected def getDefaultRep = SThrowableImpl
  }

  implicit def proxySThrowableImpl(p: Rep[SThrowableImpl]): SThrowableImpl =
    proxyOps[SThrowableImpl](p)

  implicit class ExtendedSThrowableImpl(p: Rep[SThrowableImpl]) {
    def toData: Rep[SThrowableImplData] = isoSThrowableImpl.from(p)
  }

  // 5) implicit resolution of Iso
  implicit def isoSThrowableImpl: Iso[SThrowableImplData, SThrowableImpl] =
    reifyObject(new SThrowableImplIso())

  // 6) smart constructor and deconstructor
  def mkSThrowableImpl(wrappedValue: Rep[Throwable]): Rep[SThrowableImpl]
  def unmkSThrowableImpl(p: Rep[SThrowable]): Option[(Rep[Throwable])]

  registerModule(Exceptions_Module)
}

// Seq -----------------------------------
trait ExceptionsSeq extends scalan.ScalanSeq with ExceptionsDsl {
  self: ExceptionsDslSeq =>
  lazy val SThrowable: Rep[SThrowableCompanionAbs] = new SThrowableCompanionAbs {
    override def apply(msg: Rep[String]): Rep[SThrowable] =
      SThrowableImpl(new Throwable(msg))
  }

  // override proxy if we deal with TypeWrapper
  //override def proxyThrowable(p: Rep[Throwable]): SThrowable =
  //  proxyOpsEx[Throwable, SThrowable, SeqSThrowableImpl](p, bt => SeqSThrowableImpl(bt))

  case class SeqSThrowableImpl
      (override val wrappedValue: Rep[Throwable])
    extends SThrowableImpl(wrappedValue) with SeqSThrowable {
    override def getMessage: Rep[String] =
      wrappedValue.getMessage

    override def initCause(cause: Rep[SThrowable]): Rep[SThrowable] =
      SThrowableImpl(wrappedValue.initCause(cause))
  }

  def mkSThrowableImpl
    (wrappedValue: Rep[Throwable]): Rep[SThrowableImpl] =
    new SeqSThrowableImpl(wrappedValue)
  def unmkSThrowableImpl(p: Rep[SThrowable]) = p match {
    case p: SThrowableImpl @unchecked =>
      Some((p.wrappedValue))
    case _ => None
  }

  implicit def wrapThrowableToSThrowable(v: Throwable): SThrowable = SThrowableImpl(v)
}

// Exp -----------------------------------
trait ExceptionsExp extends scalan.ScalanExp with ExceptionsDsl {
  self: ExceptionsDslExp =>
  lazy val SThrowable: Rep[SThrowableCompanionAbs] = new SThrowableCompanionAbs {
    def apply(msg: Rep[String]): Rep[SThrowable] =
      newObjEx(classOf[SThrowable], List(msg.asRep[Any]))
  }

  case class ExpSThrowableImpl
      (override val wrappedValue: Rep[Throwable])
    extends SThrowableImpl(wrappedValue)

  object SThrowableImplMethods {
  }

  def mkSThrowableImpl
    (wrappedValue: Rep[Throwable]): Rep[SThrowableImpl] =
    new ExpSThrowableImpl(wrappedValue)
  def unmkSThrowableImpl(p: Rep[SThrowable]) = p.elem.asInstanceOf[Elem[_]] match {
    case _: SThrowableImplElem @unchecked =>
      Some((p.asRep[SThrowableImpl].wrappedValue))
    case _ =>
      None
  }

  object SThrowableMethods {
    object getMessage {
      def unapply(d: Def[_]): Option[Rep[SThrowable]] = d match {
        case MethodCall(receiver, method, _, _) if receiver.elem.isInstanceOf[SThrowableElem[_]] && method.getName == "getMessage" =>
          Some(receiver).asInstanceOf[Option[Rep[SThrowable]]]
        case _ => None
      }
      def unapply(exp: Exp[_]): Option[Rep[SThrowable]] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }

    object initCause {
      def unapply(d: Def[_]): Option[(Rep[SThrowable], Rep[SThrowable])] = d match {
        case MethodCall(receiver, method, Seq(cause, _*), _) if receiver.elem.isInstanceOf[SThrowableElem[_]] && method.getName == "initCause" =>
          Some((receiver, cause)).asInstanceOf[Option[(Rep[SThrowable], Rep[SThrowable])]]
        case _ => None
      }
      def unapply(exp: Exp[_]): Option[(Rep[SThrowable], Rep[SThrowable])] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }
  }

  object SThrowableCompanionMethods {
    object apply {
      def unapply(d: Def[_]): Option[Rep[String]] = d match {
        case MethodCall(receiver, method, Seq(msg, _*), _) if receiver.elem == SThrowableCompanionElem && method.getName == "apply" =>
          Some(msg).asInstanceOf[Option[Rep[String]]]
        case _ => None
      }
      def unapply(exp: Exp[_]): Option[Rep[String]] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }
  }
}

object Exceptions_Module extends scalan.ModuleInfo {
  val dump = "H4sIAAAAAAAAALVVO2wcRRj+7+z4fA/ixFEk4ibGuvASuXPSpHCBrPMFIV1sK2sFdESgub3xecLszHhnztlLkSJFCugQLVIi0SC5QVR0NEiIggohJGqqAEIpSBXEP7OPW1usEgq2GM3jn//xfd8/e/g7nNAhvKx9woloBdSQlufm69o0va4wzEyuyeGY0w26+6L85sGlL5a+LsNCH+b2iN7QvA/VeNKNVDb36H4PqkT4VBsZagMv9VyEti85p75hUrRZEIwNGXDa7jFt1nowO5DDyT7chVIPTvlS+CE11OtwojXVyf48tRmxbF1168mWmsYQbVtFO1fFTkiYwfQxxqnY/jpV3kRIMQkMnExS21I2LbSpsEDJ0KQhKuhuTw7T5awguAGLvVvkgLQxxKjtmZCJEd6sK+J/SEZ0E02s+SwmrCnf3Zkot57pQU3TfQTo7UBxtxMpAEAGLrskWlN8Whk+LYtP06MhI5zdIfZwO5TRBOKvNAMQKXTxxjNcpB5oVwybH93033vi1YOyvRzZVCquwjl0dL5ADY4KxPG765/ox289vFKGWh9qTK8PtAmJb/KUJ2jViRDSuJwzAEk4QrZWithyUdbR5pgkqr4MFBHoKYGygTxx5jNjje1eI2GnAPqKUTQ1LUWqlNW7XFCv002HcL796NzFC7913y1D+WiIKrr0UPhh6tRAzdvZC+Vti7pD1Q7VBODiUFnRrzz6Y/jtKtwsZ1Alnp+PHXRxQv/8U/3H194sw3zfafkqJ6M+oqW7nAZbYUcK04d5eUDD+KRyQLid/StblSHdJWNuEgzzxc9g8QaWC7tOUYvMmlN4KQWgHot0UwravLrd/Mv7/tNDq8EQGvFJ3IZ/sytPfzm5a5w8DTRuh0QpOrxB+Dju/QUDM9jFCSrJTrUI+oQAOyw540W3xuafspW25FLu7jNBT5+WL+/fP/vn5x+ccS0xP2AmIKq5+h8aItXv/yh4OApW3Vq+43CNs5uzw/n0uFjH1dxzsZidWXJrMYWeDOjplcfs/YcfG6fjUnT0wdwa3MIXas35Oef8NI8l1+hGnbT61fyRHZafLyFHJornzLSSTh7RmGhlx9PHibfjhaObqJZaN/Kp0yc+YPVEEmPDuAt9ERFYKdCJlzCDUrn75LPN13/46lf3mNQsx9gLIvvdTAmNjqn7hWl0/IPkkjUwa5nPkni1MIl9K3IaYDinwHtw43Dj8oPAkbRAo1hd13L/u8jBU/kHnJbXHycIAAA="
}
}

trait ExceptionsDsl extends impl.ExceptionsAbs {self: ExceptionsDsl =>}
trait ExceptionsDslExp extends impl.ExceptionsExp {self: ExceptionsDslExp =>}
