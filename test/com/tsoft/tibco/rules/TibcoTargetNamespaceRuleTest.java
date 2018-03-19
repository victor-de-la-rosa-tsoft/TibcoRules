package com.tsoft.tibco.rules;

import com.als.core.RuleContext;
import com.als.core.io.IOUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.fail;

public class TibcoTargetNamespaceRuleTest extends AbstracRuleTest {

    @Test
    public void validNamespaceForProcess() {

        File testFolder = new File("./test/resources/testTargetnamespace");
        String sourceFile = "valid.process";
        try {
            File testSource = new File(testFolder, sourceFile);

            TibcoTargetNamespace rule = new TibcoTargetNamespace();


            RuleContext rc = testWithXmlParserFolder(rule, IOUtils.slurp(testSource),
                    sourceFile, testFolder.getPath());

            //test sin violaciones
            check(rc, " valid targetnamespace for process ");
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void invalidNamespaceForProcess() {

        File testFolder = new File("./test/resources/testTargetnamespace");
        String sourceFile = "invalid.process";
        try {
            File testSource = new File(testFolder, sourceFile);

            TibcoTargetNamespace rule = new TibcoTargetNamespace();


            RuleContext rc = testWithXmlParserFolder(rule, IOUtils.slurp(testSource),
                    sourceFile, testFolder.getPath());

            //test sin violaciones
            check(rc, " valid targetnamespace for process ", 44);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void validNamespaceForWebService() {

        File testFolder = new File("./test/resources/testTargetnamespace");
        String sourceFile = "valid.wsdl";
        try {
            File testSource = new File(testFolder, sourceFile);

            TibcoTargetNamespace rule = new TibcoTargetNamespace();


            RuleContext rc = testWithXmlParserFolder(rule, IOUtils.slurp(testSource),
                    sourceFile, testFolder.getPath());

            //test sin violaciones
            check(rc, " valid targetnamespace for process ");
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void invalidNamespaceForWebService() {

        File testFolder = new File("./test/resources/testTargetnamespace");
        String sourceFile = "invalid.wsdl";
        try {
            File testSource = new File(testFolder, sourceFile);

            TibcoTargetNamespace rule = new TibcoTargetNamespace();


            RuleContext rc = testWithXmlParserFolder(rule, IOUtils.slurp(testSource),
                    sourceFile, testFolder.getPath());

            //test sin violaciones
            check(rc, " valid targetnamespace for process ",3, 5, 15);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void validNamespaceForAtomico() {

        File testFolder = new File("./test/resources/testTargetnamespace");
        String sourceFile = "validAtomico.process";
        try {
            File testSource = new File(testFolder, sourceFile);

            TibcoTargetNamespace rule = new TibcoTargetNamespace();


            RuleContext rc = testWithXmlParserFolder(rule, IOUtils.slurp(testSource),
                    sourceFile, testFolder.getPath());

            //test sin violaciones
            check(rc, " valid targetnamespace for process ",3, 5, 15);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }
}
