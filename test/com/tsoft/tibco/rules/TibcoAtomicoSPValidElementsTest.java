package com.tsoft.tibco.rules;

import com.als.core.RuleContext;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;

public class TibcoAtomicoSPValidElementsTest extends AbstracRuleTest{

	@Test
	public void testBadCode(){
		TibcoAtomicoSPValidElements rule = new TibcoAtomicoSPValidElements();
		rule.setMessage("Valid spValidElementTest.");
		
		//valida webservices
		String testfilename = "/resources/Process/CL1CO-CONDatosCliente.process";
		String srcFolder = "c:/srcFolder";
		String CODE = readTestFile(testfilename);

		RuleContext rc = testWithXmlParserFolder(rule, CODE, testfilename, srcFolder);
		//lineas con violaciones      
		check(rc, "test process");
	}

	
	
	private static String readTestFile(String path) {
		StringBuffer content = new StringBuffer();
		try {
			BufferedReader br = new BufferedReader(new FileReader(
					TibcoAtomicoSPValidElements.class.getResource(path).getFile()));
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
