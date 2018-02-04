package com.tsoft.tibco.rules.general;


import java.io.BufferedReader;
import java.io.FileReader;

import com.tsoft.tibco.rules.AbstracRuleTest;
import org.junit.Test;


import com.als.core.RuleContext;
import com.tsoft.tibco.rules.TibcoAtomicoValidTargetNamespace;

public class TibcoConfiguracionTest extends AbstracRuleTest {

	@Test
	public void testBadCode(){
		TibcoAtomicoValidTargetNamespace rule = new TibcoAtomicoValidTargetNamespace();
		rule.setMessage("Valid Config.");
		
		//valida webservices
		String testfilename = "/resources/WebServices/wsCL1MDTCFE-CONFechaEsHabil.wsdl";
		String srcFolder = "c:/srcFolder"; 
		String BAD_CODE = readTestFile(testfilename);

		RuleContext rc = testWithXmlParserFolder(rule, BAD_CODE, testfilename, srcFolder);
		
		//lineas con violaciones
		check(rc, " test webservices ", 52);
		
		
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
