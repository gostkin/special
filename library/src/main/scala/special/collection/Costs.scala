package special.collection {
  import scalan._

  trait Costs extends Base { self: Library =>
    import Col._;
    import Costed._;
    import CostedBuilder._;
    import CostedCol._;
    import CostedFunc._;
    import CostedNestedCol._;
    import CostedOption._;
    import CostedPair._;
    import CostedPairCol._;
    import CostedPrim._;
    import CostedSum._;
    import MonoidBuilder._;
    import WEither._;
    import WOption._;
    import WRType._;
    trait Costed[Val] extends Def[Costed[Val]] {
      implicit def eVal: Elem[Val];
      def builder: Rep[CostedBuilder];
      def value: Rep[Val];
      def cost: Rep[Int];
      def dataSize: Rep[Long]
    };
    trait CostedPrim[Val] extends Costed[Val] {
      implicit def eVal: Elem[Val];
      def value: Rep[Val];
      def cost: Rep[Int];
      def dataSize: Rep[Long]
    };
    trait CostedPair[L, R] extends Costed[scala.Tuple2[L, R]] {
      implicit def eL: Elem[L];
      implicit def eR: Elem[R];
      def l: Rep[Costed[L]];
      def r: Rep[Costed[R]]
    };
    trait CostedSum[L, R] extends Costed[WEither[L, R]] {
      implicit def eL: Elem[L];
      implicit def eR: Elem[R];
      def value: Rep[WEither[L, R]];
      def left: Rep[Costed[Unit]];
      def right: Rep[Costed[Unit]]
    };
    trait CostedFunc[Env, Arg, Res] extends Costed[scala.Function1[Arg, Res]] {
      implicit def eEnv: Elem[Env];
      implicit def eArg: Elem[Arg];
      implicit def eRes: Elem[Res];
      def envCosted: Rep[Costed[Env]];
      def func: Rep[scala.Function1[Costed[Arg], Costed[Res]]];
      def cost: Rep[Int];
      def dataSize: Rep[Long]
    };
    trait CostedCol[Item] extends Costed[Col[Item]] {
      implicit def eItem: Elem[Item];
      def values: Rep[Col[Item]];
      def costs: Rep[Col[Int]];
      def sizes: Rep[Col[Long]];
      def valuesCost: Rep[Int];
      def mapCosted[Res](f: Rep[scala.Function1[Costed[Item], Costed[Res]]]): Rep[CostedCol[Res]];
      def filterCosted(f: Rep[scala.Function1[Costed[Item], Costed[Boolean]]]): Rep[CostedCol[Item]];
      def foldCosted[B](zero: Rep[Costed[B]], op: Rep[scala.Function1[Costed[scala.Tuple2[B, Item]], Costed[B]]]): Rep[Costed[B]]
    };
    trait CostedPairCol[L, R] extends Costed[Col[scala.Tuple2[L, R]]] {
      implicit def eL: Elem[L];
      implicit def eR: Elem[R];
      def ls: Rep[Costed[Col[L]]];
      def rs: Rep[Costed[Col[R]]]
    };
    trait CostedNestedCol[Item] extends Costed[Col[Col[Item]]] {
      implicit def eItem: Elem[Item];
      def rows: Rep[Col[Costed[Col[Item]]]]
    };
    trait CostedOption[T] extends Costed[WOption[T]] {
      implicit def eT: Elem[T];
      def get: Rep[Costed[T]];
      def getOrElse(default: Rep[Costed[T]]): Rep[Costed[T]];
      def fold[B](ifEmpty: Rep[Costed[B]], f: Rep[Costed[scala.Function1[T, B]]]): Rep[Costed[B]];
      def isEmpty: Rep[Costed[Boolean]];
      def isDefined: Rep[Costed[Boolean]];
      def filter(p: Rep[Costed[scala.Function1[T, Boolean]]]): Rep[Costed[WOption[T]]];
      def flatMap[B](f: Rep[Costed[scala.Function1[T, WOption[B]]]]): Rep[Costed[WOption[B]]];
      def map[B](f: Rep[Costed[scala.Function1[T, B]]]): Rep[Costed[WOption[B]]]
    };
    trait CostedBuilder extends Def[CostedBuilder] {
      def ConstructTupleCost: Rep[Int] = toRep(1.asInstanceOf[Int]);
      def ConstructSumCost: Rep[Int] = toRep(1.asInstanceOf[Int]);
      def SelectFieldCost: Rep[Int] = toRep(1.asInstanceOf[Int]);
      def SumTagSize: Rep[Long] = toRep(1L.asInstanceOf[Long]);
      def costedValue[T](x: Rep[T], optCost: Rep[WOption[Int]]): Rep[Costed[T]];
      def defaultValue[T](valueType: Rep[WRType[T]]): Rep[T];
      def monoidBuilder: Rep[MonoidBuilder];
      def mkCostedPrim[T](value: Rep[T], cost: Rep[Int], size: Rep[Long]): Rep[CostedPrim[T]];
      def mkCostedPair[L, R](first: Rep[Costed[L]], second: Rep[Costed[R]]): Rep[CostedPair[L, R]];
      def mkCostedSum[L, R](value: Rep[WEither[L, R]], left: Rep[Costed[Unit]], right: Rep[Costed[Unit]]): Rep[CostedSum[L, R]];
      def mkCostedFunc[Env, Arg, Res](envCosted: Rep[Costed[Env]], func: Rep[scala.Function1[Costed[Arg], Costed[Res]]], cost: Rep[Int], dataSize: Rep[Long]): Rep[CostedFunc[Env, Arg, Res]];
      def mkCostedCol[T](values: Rep[Col[T]], costs: Rep[Col[Int]], sizes: Rep[Col[Long]], valuesCost: Rep[Int]): Rep[CostedCol[T]];
      def mkCostedPairCol[L, R](ls: Rep[Costed[Col[L]]], rs: Rep[Costed[Col[R]]]): Rep[CostedPairCol[L, R]];
      def mkCostedNestedCol[Item](rows: Rep[Col[Costed[Col[Item]]]]): Rep[CostedNestedCol[Item]];
      def mkCostedSome[T](costedValue: Rep[Costed[T]]): Rep[CostedOption[T]];
      def mkCostedNone[T](cost: Rep[Int])(implicit eT: Elem[T]): Rep[CostedOption[T]];
      def mkCostedOption[T](value: Rep[WOption[T]], none: Rep[Costed[Unit]], some: Rep[Costed[Unit]]): Rep[CostedOption[T]]
    };
    trait CostedCompanion;
    trait CostedPrimCompanion;
    trait CostedPairCompanion;
    trait CostedSumCompanion;
    trait CostedFuncCompanion;
    trait CostedColCompanion;
    trait CostedPairColCompanion;
    trait CostedNestedColCompanion;
    trait CostedOptionCompanion;
    trait CostedBuilderCompanion
  }
}