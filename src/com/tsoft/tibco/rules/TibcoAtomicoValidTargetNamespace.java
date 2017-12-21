package com.tsoft.tibco.rules.general;

import java.io.BufferedReader;
import java.io.FileReader;

import org.junit.Test;

import com.als.core.RuleContext;
import com.tsoft.tibco.rules.TibcoAtomicoValidTargetNamespace;

public class TibcoValidNamespaceRuleTest extends AbstracRuleTest {


	@Test
	public void testBadCode() {
		TibcoAtomicoValidTargetNamespace rule = new TibcoAtomicoValidTargetNamespace();
		rule.setMessage("Valid Namespace.");
		
		String testfilename = "/resource/WebServices/wsCL1MDCLVSP001-CONSolicitaDesafio.wsdl";
		//String testfilename = "/resources/Process/tibcoAtomicoValidTargetNamespace1.process";
		String srcFolder = "c:/srcFolder"; 
		String BAD_CODE = readTestFile(testfilename);

		RuleContext rc = TibcoValidNamespaceRuleTest.testWithSrcFolder(rule, BAD_CODE, testfilename, srcFolder);
		
		//lineas con violaciones
		check(rc, " test 1 ");
		//check(rc, " test 1 ",3);
	}

	
	
	private static String readTestFile(String path) {
		StringBuffer content = new StringBuffer();
		try {
			BufferedReader br = new BufferedReader(new FileReader(
					TibcoValidNamespaceRuleTest.class.getResource(path).getFile()));
			String line;
			while ((line = br.readLine()) != null) {
				content.append(line).append(System.getProperty("line.separator"));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return content.toString();
	}

	
}
