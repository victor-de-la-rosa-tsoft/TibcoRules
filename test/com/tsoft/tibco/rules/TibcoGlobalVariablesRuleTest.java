package com.tsoft.tibco.rules;

import com.als.core.RuleContext;
import com.als.core.io.IOUtils;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class TibcoGlobalVariablesRuleTest extends AbstracRuleTest{


    TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void correctVariablesShouldNotFail() {


        try {
            File testFolder = new File("./test/resources");
            File testSource = new File(testFolder, "globalVariable/defaultVars.substvar");


            TibcoGlobalVariablesRule rule = new TibcoGlobalVariablesRule();


            RuleContext rc = testWithSrcFolder(rule, IOUtils.slurp(testSource),
                    "globalVariable/defaultVars.substvar", testFolder.getPath());

            //test sin violaciones
            check(rc, " test correct variables ");
        }
        catch(IOException e) {
            e.printStackTrace();
            fail();
        }
    }
}