package com.tsoft.tibco.rules;

import com.als.core.RuleContext;
import com.als.core.io.IOUtils;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class TibcoGlobalVariablesRuleTest extends AbstracRuleTest {


    TemporaryFolder tempFolder = new TemporaryFolder();



    @Test
    public void correctVariablesShouldNotFail() {

        File testFolder = new File("./test/resources/testglobalvariable/validFolder");
        String sourceFile = "INFO/defaultVars.substvar";
        try {
            File testSource = new File(testFolder, sourceFile);

            TibcoGlobalVariablesRule rule = new TibcoGlobalVariablesRule();


            RuleContext rc = testWithXmlParserFolder(rule, IOUtils.slurp(testSource),
                    sourceFile, testFolder.getPath());

            //test sin violaciones
            check(rc, " test correct variables ");
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void incorrectGlobalVariableFilesShouldFail() {

        File testFolder = new File("./test/resources/testglobalvariable/badName");
        String sourceFile = "HTTP/badDefaultVars.substvar";
        try {
            File testSource = new File(testFolder, sourceFile);

            TibcoGlobalVariablesRule rule = new TibcoGlobalVariablesRule();


            RuleContext rc = testWithXmlParserFolder(rule, IOUtils.slurp(testSource),
                    sourceFile, testFolder.getPath());

            //test sin violaciones
            check(rc, " test correct variables ",  1, 1, 4, 20, 28);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void incorrectGlobalVariablePathShouldFail() {

        File testFolder = new File("./test/resources/testglobalvariable/badFolder");
        String sourceFile = "http/defaultVars.substvar";
        try {
            File testSource = new File(testFolder, sourceFile);

            TibcoGlobalVariablesRule rule = new TibcoGlobalVariablesRule();


            RuleContext rc = testWithXmlParserFolder(rule, IOUtils.slurp(testSource),
                    sourceFile, testFolder.getPath());

            //test sin violaciones
            check(rc, " test correct variables ", 1, 1, 1);
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }


    @Test
    public void camelCaseValidationShouldNotGenerateStackOverflow() {
        File testFolder = new File("./test/resources/testglobalvariable/camelcase");
        String sourceFile = "DEFAULT/defaultVars.substvar";
        try {
            File testSource = new File(testFolder, sourceFile);

            TibcoGlobalVariablesRule rule = new TibcoGlobalVariablesRule();


            RuleContext rc = testWithXmlParserFolder(rule, IOUtils.slurp(testSource),
                    sourceFile, testFolder.getPath());

            //test sin violaciones
            check(rc, " test correct variables ", 1, 1);
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }


}