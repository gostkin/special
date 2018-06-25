package scalan.plugin

import scalan.{FunctorType, ContainerType}
import scalan.meta._
import scalan.meta.ScalanAst.{WrapperConf, NonWrapper}
import scalan.meta.scalanizer.ScalanizerConfig

abstract class ScalanizerPluginConfig extends ScalanizerConfig {

  def getModule(moduleName: String): ModuleConf = {
    val source = sourceModules.get(moduleName)
    val target = targetModules.get(moduleName)
    if (source.isDefined && target.isDefined)
        sys.error(s"ScalanPlugin configuration error: Module $moduleName found in both source modules and target modules")
    else if (source.isEmpty && target.isEmpty)
      sys.error(s"ScalanPlugin configuration error: Module $moduleName not found in both source and target lists")
    else
      source.getOrElse(target.get)
  }

  /** The flag indicates that the plugin has to generate additional information and to store it
    * the debug folder and outputs to the console. */
  var debug: Boolean = true

  def withDebug(d: Boolean): ScalanizerConfig = {debug = d; this }

  lazy val unitConfigs: List[UnitConfig] = (for {
    (_, mc) <- sourceModules.table
    (_, u) <- mc.units.table
  } yield u).toList

  def getUnitConfig(unitName: String) = unitConfigs.find(_.name == unitName).getOrElse {
    sys.error(s"Cannot fing UnitConfig for '$unitName'")
  }

  def wrapperConfigs: Map[String, WrapperConf] = {
    val wlist = for {
      m <- sourceModules.values
      u <- m.units.values
      w <- u.wrappers } yield w
    val gs = wlist.groupBy(_._1)
    val duplicates = gs.filter(_._2.size > 2)
    if (duplicates.nonEmpty)
      sys.error(s"Duplicated definition of wrappers ${duplicates.keySet}")
    gs.mapValues(xs => xs.head._2)
  }

  val nonWrappers: Map[String, NonWrapper] = List[NonWrapper](
    NonWrapper(name = "Predef"),
    NonWrapper(name = "<byname>"),
    NonWrapper(name = "ArrayOps"),
    NonWrapper(name = "WrappedArray"),
    NonWrapper(name = "CanBuildFrom"),
    NonWrapper(name = "Class"),
    NonWrapper(name = "ScalaRunTime"),
    NonWrapper(name = "DummyImplicit"),
    NonWrapper(name = "ArraySeq"),
    NonWrapper(name = "ClassTag")
  ).map(w => (w.name, w)).toMap

  val pluginConfigs: Seq[PluginConfig] = Nil
}

