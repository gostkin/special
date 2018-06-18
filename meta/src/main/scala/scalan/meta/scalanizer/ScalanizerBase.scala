package scalan.meta.scalanizer

import java.io.File

import scala.tools.nsc.Global
import scalan.meta.ScalanAst._
import scalan.meta._
import scalan.util.FileUtil

trait ScalanizerBase[+G <: Global]
  extends ScalanParsers[G] with ScalanGens[G] {
  import global._
  import context._

  def snConfig: ScalanizerConfig

  def isNonWrapper(name: String): Boolean = {
    snConfig.nonWrappers.contains(name)
  }

  def isWrapper(name: String): Boolean = {
    val ok = !Set(
      isPrimitive _, isStandardType _,
      isEntity _, isEntityCompanion _,
      isClass _, isClassCompanion _,
      isModule _, isNonWrapper _
    ).exists(_ (name))
    ok
  }

  def getParents(externalType: Type) = {
    externalType.typeSymbol.typeSignature match {
      case PolyType(_, ClassInfoType(parents, _, _)) => parents
      case ClassInfoType(parents, _, _) => parents
      case _ => Nil
    }
  }

  def saveCodeToResources(module: SourceModuleConf, packageName: String, fileName: String, code: String): Unit = {
    saveCode(module.getResourcesRootDir, packageName, fileName, ModuleConf.ResourceFileExtension, code)
  }

  def saveWrapperCode(module: SourceModuleConf, packageName: String, fileName: String, code: String) = {
    saveCode(module.getWrappersRootDir, packageName, fileName, ModuleConf.ResourceFileExtension, code)
  }

  def saveImplCode(sourceRoot: String, packageName: String, unitName: String, ext: String, code: String): File = {
    saveCode(sourceRoot, packageName + ".impl", unitName + "Impl", ext, code)
  }

  def saveCode(sourceRoot: String, packageName: String, unitName: String, ext: String, code: String): File = {
    val packagePath = packageName.replace('.', '/')
    val file = FileUtil.file(sourceRoot, packagePath, unitName + ext)
    file.mkdirs()
    FileUtil.write(file, code)
    file
  }

//  def saveImplCode(file: File, implCode: String) = {
//    val fileName = file.getName.split('.')(0)
//    val folder = file.getParentFile
//    val implFile = FileUtil.file(folder, "impl", s"${fileName}Impl.scala")
//    implFile.mkdirs()
//    FileUtil.write(implFile, implCode)
//  }
}
