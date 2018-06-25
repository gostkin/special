package scalan.meta.scalanizer

import scala.tools.nsc.Global
import scalan.meta.{SourceModuleConf, UnitConfig}

/** Scalanizer is a component which can be used from different contexts to generated
  * boilerplate code such as wrappers, Impl files etc.
  * Scalanizer object is created for a set of Scalan modules of scalan-meta (SModuleDef) */
trait Scalanizer[+G <: Global]
    extends ScalanizerBase[G]
        with Backend[G]
        //  with HotSpots[G]
{
  /** Project module for which this instance of Scalanizer is running. */
  def moduleName: String

  def getSourceModule = snConfig.sourceModules.get(moduleName).getOrElse {
    global.abort(
      s"""Source module $moduleName is not found in config instance of ${snConfig.getClass.getName}.
        |Declared source modules ${snConfig.sourceModules.keySet}.
       """.stripMargin
    )
  }

  def getTargetModule = snConfig.targetModules.get(moduleName).getOrElse {
    global.abort(
      s"""Target module $moduleName is not found in config instance of ${snConfig.getClass.getName}.
        |Declared target modules ${snConfig.targetModules.keySet}.
       """.stripMargin
    )
  }

  def findUnitModule(unitName: String): Option[(SourceModuleConf, UnitConfig)] = {
    for {
      sourceModule <- snConfig.sourceModules.find(mc => mc.hasUnit(unitName))
      conf <- sourceModule.units.get(unitName)
    }
    yield (sourceModule, conf)
  }

  def isModuleUnit(unitName: String) = findUnitModule(unitName).isDefined

  def getUnitPackage(unit: global.CompilationUnit) = {
    val packageName = unit.body match {
      case pd: global.PackageDef =>
        val packageName = pd.pid.toString
        packageName
    }
    packageName
  }

  def withUnitModule(unit: global.CompilationUnit)(action: (SourceModuleConf, UnitConfig) => Unit) = {
    val unitName = unit.source.file.name
    findUnitModule(unitName) match {
      case Some((sm, conf)) => try {
        action(sm, conf)
      } catch {
        case e: Exception =>
          print(s"Error: failed to parse $unitName from ${unit.source.file} due to " + e.printStackTrace())
      }
      case None =>
    }
  }
}
