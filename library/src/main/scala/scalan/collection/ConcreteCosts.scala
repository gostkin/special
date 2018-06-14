package scalan.collection {
  import scala.reflect.ClassTag
  import scalan._

  trait ConcreteCosts extends Base { self: Library =>
    abstract class CostedPrim[T](val value: Rep[T], val cost: Rep[Long]) extends Costed[T] {
      def builder: Rep[ConcreteCostedBuilder] = ConcreteCostedBuilder()
    };
    abstract class CostedPair[L, R](val l: Rep[L], val r: Rep[R], val cost: Rep[Long]) extends Costed[scala.Tuple2[L, R]] {
      def builder: Rep[ConcreteCostedBuilder] = ConcreteCostedBuilder();
      def value: Rep[scala.Tuple2[L, R]] = Pair(CostedPair.this.l, CostedPair.this.r)
    };
    abstract class CostedArray[T](val arr: Rep[Col[Costed[T]]]) extends Costed[WArray[T]] {
      implicit def _eT: Elem[T] = eT.eItem
      def builder: Rep[ConcreteCostedBuilder] = ConcreteCostedBuilder();
      def value: Rep[WArray[T]] = CostedArray.this.arr.map[T](fun(((c: Rep[Costed[T]]) => c.value))).arr;
      def cost: Rep[Long] = CostedArray.this.arr.map[Long](fun(((c: Rep[Costed[T]]) => c.cost))).fold[Long](toRep(0L.asInstanceOf[Long]))(fun(((in: Rep[scala.Tuple2[Long, Long]]) => {
        val x: Rep[Long] = in._1;
        val y: Rep[Long] = in._2;
        x.+(y)
      })))
    };
    abstract class ConcreteCostedBuilder extends CostedBuilder;
    trait CostedPrimCompanion;
    trait CostedPairCompanion;
    trait CostedArrayCompanion;
    trait ConcreteCostedBuilderCompanion
  }
}