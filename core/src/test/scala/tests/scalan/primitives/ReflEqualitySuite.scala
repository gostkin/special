package tests.scalan.primitives

import java.io.File
import java.lang.reflect.Method

import scalan.compilation.GraphVizExport
import scalan.{BaseShouldTests, ScalanCtxExp}

import scalan.compilation.{GraphVizConfig, GraphVizExport}
import scalan.{BaseShouldTests, ScalanCtxExp}

class ReflEqualitySuite extends BaseShouldTests {

  class Ctx extends ScalanCtxExp with GraphVizExport {
    override def isInvokeEnabled(d: Def[_], m: Method) = true
    lazy val testLemma = postulate[Int, Int, Int, Int]((x, y, z) => x * y + x * z <=> x * (y + z))
    lazy val rule = rewriteRuleFromEqLemma(testLemma)
    lazy val patGraph = rule.patternGraph 

    lazy val test = {(x: IntRep) => x * 10 + x * 20}
    lazy val testFunc = fun(test)
  }

  def getCtx = new Ctx

  "ScalanStaged" should "created Lemma" in {
    val ctx = getCtx
    import ctx._
    ctx.emitDepGraph(ctx.testLemma, new File(prefix, "testLemma.dot"))(GraphVizConfig.default)
  }

  it should "create LemmaRule" in {
    val ctx = getCtx
    import ctx._
    ctx.emitDepGraph(Seq[Exp[_]](testLemma, rule.pattern, rule.rhs), new File(prefix, "testRule.dot"))(GraphVizConfig.default)
  }

  it should "create ProjectionTree in pattern" in {
    val ctx = getCtx
    import ctx._
    val tree = rule.pattern.argsTree
    ctx.emitDepGraph(Seq[Exp[_]](rule.pattern, rule.rhs), new File(prefix, "testPatternAndRhs.dot"))(GraphVizConfig.default)
  }

  "LemmaRule" should "build PatternGraph" in {
    val ctx = getCtx
    import ctx._
    import ctx.graphs.Graph
    ctx.emitDepGraph(ctx.rule.pattern, new File(prefix, "testPattern.dot"))(GraphVizConfig.default)
    ctx.emitDot(Graph.asDot(patGraph), new File(prefix, "patternGraph.dot"))(GraphVizConfig.default)
  }

  it should "recognize pattern" in {
    val ctx = getCtx
    import ctx._
    import ctx.graphs._
    val lam = testFunc.getLambda
    ctx.emitDepGraph(List(rule.pattern, testFunc), new File(prefix, "LemmaRule/patternAndTestFunc.dot"))(GraphVizConfig.default)
    rule.matchWith(lam.y) match {
      case Some((res, subst)) => 
        res should be(SimilarityEmbeded)
        subst should not be(Map.empty)
        
      case _ => 
        fail("should recognize pattern")
    }
    
  }

  it should "apply pattern" in {
    val ctx = getCtx
    import ctx._
    import ctx.graphs._
    val lam = testFunc.getLambda
    val rewritten = rule(lam.y)
    rewritten match {
      case Some(res) => 
        ctx.emitDepGraph(List(Pair(lam.y, res)), new File(prefix, "LemmaRule/originalAndRewritten.dot"))(GraphVizConfig.default)
      case _ => 
        fail("should apply pattern")
    }
  }

  it should "rewrite when registered" in {
    val ctx = getCtx
    import ctx._
    import ctx.graphs._
    val withoutRule = testFunc
    addRewriteRules(rule)
    val withRule = fun(test)
    removeRewriteRules(rule)
    ctx.emitDepGraph(List(withoutRule, withRule), new File(prefix, "LemmaRule/ruleRewriting.dot"))(GraphVizConfig.default)
    
    val expected = fun[Int,Int] {x => x * 30}
    alphaEqual(withRule, expected) should be(true)
    alphaEqual(withoutRule, expected) should be(false)

    val afterRemoval = fun(test)
    ctx.emitDepGraph(List(withoutRule, withRule, afterRemoval), new File(prefix, "LemmaRule/ruleRewriting.dot"))(GraphVizConfig.default)
    alphaEqual(afterRemoval, withoutRule) should be(true)
  }
}
