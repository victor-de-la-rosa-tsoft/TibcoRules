package com.tsoft.tibco.rules.general;

import com.als.core.Rule;
import com.als.core.RuleContext;
import com.als.core.RuleViolation;
import com.als.core.io.TransformedContents;
import com.tsoft.tibco.rules.ExampleTibcoRule;
import com.tsoft.tibco.rules.TibcoAppProjectValidStructureRule;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static junit.framework.Assert.assertEquals;

public class TibcoAppProjectValidStructureRuleTest  {

	TibcoAppProjectValidStructureRule rule;

	TemporaryFolder tempFolder = new TemporaryFolder();

	@Before
	public void setup() {
		rule = new TibcoAppProjectValidStructureRule();

	}

	@Test
	public void correctStructureShouldNotFail() {

        File srcFolder = null;

        try {
            tempFolder.create();
            srcFolder = tempFolder.newFolder("test1");

            /** datos de prueba */
            String[] testFiles = {
                    "\\App3Click\\AESchemas\\ae.aeschema",
                    "\\App3Click\\AESchemas\\ae\\baseDocument.aeschema",
                    "\\App3Click\\Process\\Main\\wsCL1WS-CONGeneraRecuperaDocumento.process",
                    "/app/Process/SUBPROCESS/a.process",
                    "Process/SUBPROCESS/ATOMICO/a.process",
                    "Process/SUBPROCESS/UTILITIES/a.process",
                    "vcrepo.dat"
            };

            for(String testFile: testFiles) {
                (new File(srcFolder, testFile)).getParentFile().mkdirs();
                (new File(srcFolder, testFile)).createNewFile();
            }

        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

		RuleContext rc = testWithSrcFolder(rule, "anycode", "anyfile", srcFolder.getPath());
		
		//lineas con violaciones
		check(rc, " test 1 ");
	}


    @Test
    public void unexpectedFileStructureShouldFail() {

        File srcFolder = null;

        try {
            tempFolder.create();
            srcFolder = tempFolder.newFolder("test1");

            /** datos de prueba */
            String[] testFiles = {
                    "Process/MAIN/valid.process",
                    "Process/MAIN/BADFOLDER/a.process",
                    "Process/MAIN/SUBPROCESS/a.notvalid"
            };

            for(String testFile: testFiles) {
                (new File(srcFolder, testFile)).getParentFile().mkdirs();
                (new File(srcFolder, testFile)).createNewFile();
            }

        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        RuleContext rc = testWithSrcFolder(rule, "anycode", "anyfile", srcFolder.getPath());

        //lineas con violaciones
        check(rc, " test 1 ", 1, 1);
    }

    /**
     * Check that violations are emitted in the expected lines. Useful for tests
     * with a single input
     */
    public static void check(RuleContext ctx, String testname,
                             Integer... expectedViolationLines) {
        int expectedNum = expectedViolationLines.length;
        int seenNum = ctx.getReport().size();

        assertEquals(testname + " expected: " + expectedNum + ", seen: "
                + seenNum, expectedNum, seenNum);

        int i = 0;
        for (RuleViolation rv : ctx.getReport()) {
            int seen = rv.getLine();
            int expected = expectedViolationLines[i++];
            assertEquals("Violation does not match - expected: " + expected
                    + ", seen: " + seen, expected, seen);
        }
    }

    /**
     * Prepara contexto y simula ejecución de Regla
     * @param rule
     * @param code
     * @param relativaPathFilename
     * @param srcFolder
     * @return
     */
    RuleContext testWithSrcFolder(Rule rule, String code,
                                  String relativaPathFilename, String srcFolder) {

        RuleContext ctx = new RuleContext();

        File srcFolderFile = new File(srcFolder);
        File codeFile = new File(srcFolderFile, relativaPathFilename);

        ctx.setCurrentDirectory(srcFolderFile);
        ctx.setSourceCodeFilename(codeFile);

        ctx.setOriginalFileContents(TransformedContents.build(
                ctx.getSourceCodeFilename(), code));


        rule.initialize(ctx);
        rule.apply(null, ctx); //no se trabaja con arbol AST
        rule.postProcess(ctx);

        //dump violations
        System.out.println("Violaciones encontradas:");
        for (RuleViolation rv : ctx.getReport()) {
            System.out.println(rv);
        }

        return ctx;
    }

	

	
}
