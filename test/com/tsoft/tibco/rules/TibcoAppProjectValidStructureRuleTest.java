package com.tsoft.tibco.rules;

import com.als.core.Rule;
import com.als.core.RuleContext;
import com.als.core.RuleViolation;
import com.als.core.io.TransformedContents;
import com.tsoft.tibco.rules.ExampleTibcoRule;
import com.tsoft.tibco.rules.TibcoAppProjectValidStructureRule;
import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;



public class TibcoAppProjectValidStructureRuleTest extends AbstracRuleTest  {

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
                    "src/Process/Main/jsonRest/CL1CO-CONEstadoTarjeta.process",
                    "src/Resources/Xslt/formatoMontoMVHistoricoxFecha.xslt",
                    "vcrepo.dat"
            };

            for(String testFile: testFiles) {
                (new File(srcFolder, testFile)).getParentFile().mkdirs();
                (new File(srcFolder, testFile)).createNewFile();
            }

        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

		RuleContext rc = testWithoutParserFolder(rule, "anycode", "anyfile", srcFolder.getPath());

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
                    "Process/MAIN/BADFOLDER-5/a.process",
                    "Process/MAIN/SUBPROCESS2/un espacio.notvalid"
            };

            for(String testFile: testFiles) {
                (new File(srcFolder, testFile)).getParentFile().mkdirs();
                (new File(srcFolder, testFile)).createNewFile();
            }

        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        RuleContext rc = testWithoutParserFolder(rule, "anycode", "anyfile", srcFolder.getPath());

        //lineas con violaciones
        check(rc, " test 1 ", 1, 1);
    }





	

	
}
