package scalan.meta

import scalan.meta.ScalanAstExtensions._
import scalan.meta.ScalanAst.{STpeDef, SUnitDef, SValDef, SEntityAnnotation, SClassDef, SObjectDef, STraitDef, SMethodDef, SImportStat, SBodyItem, SEntityDef}
import scalan.util.CollectionUtil.{OptionOps, TraversableOps}

class SUnitMerger(uTo: SUnitDef)(implicit ctx: AstContext) {

  def checkEquals[T](x: T, y: T)(msg: => String) = if (x != y) sys.error(msg)

  def mergeImports(to: SImportStat, from: SImportStat) = {
    checkEquals(to, from)(s"Cannot merge imports because they are different: $to and $from")
    to
  }

  def mergeTypes(to: STpeDef, from: STpeDef): STpeDef = {
    checkEquals(to.tpe, from.tpe)(
        s"""
          |Cannot merge type definition $from into types of unit ${uTo.packageAndName }
          |because they have different definitions:
          |existing $to, to be merged: $from""".stripMargin)
    to
  }

  def mergeVals(to: SValDef, from: SValDef) = {
    to
  }

  def mergeBodyItems(to: SBodyItem, from: SBodyItem): SBodyItem = {
    (to, from) match {
      case (to: SMethodDef, from: SMethodDef) => mergeMethods(to, from)
      case (to: SEntityDef, from: SEntityDef) => mergeEntities(to, from)
      case (to: SImportStat, from: SImportStat) => mergeImports(to, from)
      case (to: STpeDef, from: STpeDef) => mergeTypes(to, from)
      case (to: SValDef, from: SValDef) => mergeVals(to, from)
      case _ =>
        sys.error(s"Cannot merge body items of different types: $to and $from")
    }
  }

  def mergeEntities(to: SEntityDef, from: SEntityDef): SEntityDef = (to, from) match {
    case (to: STraitDef, from: STraitDef) => mergeTraits(to, from)
    case (to: SClassDef, from: SClassDef) => mergeClasses(to, from)
    case (to: SObjectDef, from: SObjectDef) => mergeObjects(to, from)
    case _ =>
      sys.error(s"Cannot merge entities of different types: $to and $from")
  }

  def mergeEntityAnnotations(to: SEntityAnnotation, from: SEntityAnnotation) = {
    checkEquals(to, from)(s"Cannot merge annotations because they are different: $to and $from")
    to
  }

  def mergeTraits(to: STraitDef, from: STraitDef): STraitDef = {
    checkEquals(to.tpeArgs, from.tpeArgs)(s"Cannot merge traits with different type args: $to and $from")
    checkEquals(to.ancestors, from.ancestors)(s"Cannot merge traits with different ancestors $to and $from")
    checkEquals(to.selfType, from.selfType)(s"Cannot merge traits with different self types $to and $from")
    val newBody = to.body.mergeWith(from.body, _.signature, mergeBodyItems)
    val newComp = to.companion.mergeWith(from.companion, mergeEntities)
    val newAnnotations = to.annotations.mergeWith(from.annotations, _.annotationClass, mergeEntityAnnotations)
    to.copy(body = newBody, selfType = to.selfType, companion = newComp, annotations = newAnnotations)
  }

  def mergeClasses(to: SClassDef, from: SClassDef) = {
    to
  }

  def mergeObjects(to: SObjectDef, from: SObjectDef) = {
    to
  }

  def mergeMethods(to: SMethodDef, from: SMethodDef): SMethodDef = {
    checkEquals(to, from)(s"Cannot merge methods because they are different: $to and $from")
    to
  }

  def merge(uFrom: SUnitDef): SUnitDef = {
    val newImports = (uTo.imports ++ uFrom.imports).distinct
    val newTypes = uTo.typeDefs.mergeWith(uFrom.typeDefs, _.name, mergeTypes)
    val newTraits = uTo.traits.mergeWith(uFrom.traits, _.name, mergeTraits)
    val newClasses = uTo.classes.mergeWith(uFrom.classes, _.name, mergeClasses)
    val newMethods = uTo.methods.mergeWith(uFrom.methods, _.signature, mergeMethods)
    SUnitDef(
      uTo.packageName, newImports, uTo.name,
      newTypes, newTraits, newClasses, newMethods, uTo.selfType, uTo.ancestors,
      uTo.origModuleTrait, uTo.isVirtualized, uTo.okEmitOrigModuleTrait
    )
  }
}

