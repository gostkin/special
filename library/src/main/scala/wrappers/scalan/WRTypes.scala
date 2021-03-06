package wrappers.scalan {
  import scalan._

  import impl._

  import scalan.RType

  import special.wrappers.WrappersModule

  import special.wrappers.RTypeWrapSpec

  trait WRTypes extends Base { self: WrappersModule =>
    import WRType._;
    @External("RType") @Liftable trait WRType[A] extends Def[WRType[A]] {
      implicit def eA: Elem[A];
      @External def name: Rep[String]
    };
    trait WRTypeCompanion
  }
}