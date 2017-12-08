package com.tsoft.tibco.rules.general;

import static junit.framework.Assert.assertEquals;

import java.io.File;
import java.util.Properties;

import org.junit.BeforeClass;

import com.als.core.AbstractRule;
import com.als.core.Rule;
import com.als.core.RuleContext;
import com.als.core.RuleViolation;
import com.als.core.io.TransformedContents;
import com.als.core.util.ClassUtil;
import com.optimyth.qaking.test.TestUtils;

public class AbstracRuleTest {


	@BeforeClass
	public static void setupParser() {

	}

	/**
	 * Metodo que prepara un contexto e invoca una ejecución de una regla sobre un código. 
	 * El contexto se informa con las violaciones encontradas por la regla
	 * y una ruta relativa del archivo al que corresponde el còdigo analizado. 
	 * 
	 * @param rule
	 * @param code
	 * @param relativaPathFilename
	 * @return
	 */
	public static RuleContext test(Rule rule, String code, String relativaPathFilename) {
		return testWithSrcFolder(rule, code, relativaPathFilename, "");
	}
	
	/**
	 * Metodo que prepara un contexto e invoca una ejecución de una regla sobre un código. 
	 * El contexto se informa con las violaciones encontradas por la regla, además de 
	 * la ruta a una carpeta raiz simulada sobre la que se está ejecutando el análisis, 
	 * y una ruta relativa del archivo al que corresponde el còdigo analizado. 
	 * 
	 * @param rule - regla validada
	 * @param code - contenido del archivo en analisis
	 * @param relativaPathFilename - path relativo del fuente en analisis
	 * @param sourcePath - simula una carpeta raiz donde se encuentran los fuentes
	 * @return
	 */
	public static RuleContext testWithSrcFolder(Rule rule, String code, String relativaPathFilename, String srcFolder) {

		RuleContext ctx = new RuleContext();
		
		File srcFolderFile = new File(srcFolder);
		File codeFile = new File(srcFolderFile, relativaPathFilename);
		
		ctx.setCurrentDirectory(srcFolderFile);
		ctx.setSourceCodeFilename(codeFile);
		
		ctx.setOriginalFileContents(TransformedContents.build(ctx.getSourceCodeFilename(), code));
		
		
		//no hay parser aun
		ctx.setAbstractSyntaxTree(null);

		rule.initialize(ctx);
		rule.apply(null, ctx);
		rule.postProcess(ctx);

		dump(ctx);

		return ctx;
	}

	public static void test(Class<? extends AbstractRule> rule,
	    int expectedViolations, Properties p) {
		String rulename = ClassUtil.className(rule.getName());
		RuleContext ctx = TestUtils.testRuleOnTestbed(rulename, "java", rule,
		    expectedViolations, p);
		dump(ctx);
	}

	/**
	 * Check that violations are emitted in the expected lines. Useful for tests
	 * with a single input
	 */
	public static void check(RuleContext ctx, String testname,
	    Integer... expectedViolationLines) {
		int expectedNum = expectedViolationLines.length;
		int seenNum = ctx.getReport().size();
		assertEquals(testname + " expected: " + expectedNum + ", seen: " + seenNum,
		    expectedNum, seenNum);

		int i = 0;
		for (RuleViolation rv : ctx.getReport()) {
			int seen = rv.getLine();
			int expected = expectedViolationLines[i++];
			assertEquals("Violation does not match - expected: " + expected
			    + ", seen: " + seen, expected, seen);
		}
	}

	

	public static void dump(RuleContext ctx) {
		for (RuleViolation rv : ctx.getReport()) {
			System.out.println(rv);
		}
	}
}
