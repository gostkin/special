package special.collection {
  import scalan._

  trait CostedOptions extends Base { self: Library =>
    import CCostedBuilder._;
    import CostedBuilder._;
    import CostedOption._;
    import Size._;
    import SizeOption._;
    import WOption._;
    abstract class CCostedOption[T](val value: Rep[WOption[T]], val costOpt: Rep[WOption[Int]], val sizeOpt: Rep[WOption[Size[T]]], val accumulatedCost: Rep[Int]) extends CostedOption[T] {
      def builder: Rep[CostedBuilder] = RCCostedBuilder();
      @NeverInline def cost: Rep[Int] = delayInvoke;
      def size: Rep[Size[WOption[T]]] = CCostedOption.this.builder.mkSizeOption[T](CCostedOption.this.sizeOpt)
    };
    trait CCostedOptionCompanion
  }
}