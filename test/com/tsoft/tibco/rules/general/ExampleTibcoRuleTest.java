package com.tsoft.tibco.rules.general;

import java.io.BufferedReader;
import java.io.FileReader;

import org.junit.Test;

import com.als.core.RuleContext;
import com.tsoft.tibco.rules.ExampleTibcoRule;

public class ExampleTibcoRuleTest extends AbstracRuleTest {


	@Test
	public void testBadCode() {
		ExampleTibcoRule rule = new ExampleTibcoRule();
		rule.setMessage("Uso de variables de entorno.");
		
		String testfilename = "/resources/exampletibcoruletest1.process";
		String BAD_CODE = readTestFile(testfilename);

		RuleContext rc = ExampleTibcoRuleTest.test(rule, BAD_CODE, testfilename);
		
		//lineas con violaciones
		check(rc, " test 1 ", 1,2);
	}

	
	
	private static String readTestFile(String path) {
		StringBuffer content = new StringBuffer();
		try {
			BufferedReader br = new BufferedReader(new FileReader(
					ExampleTibcoRuleTest.class.getResource(path).getFile()));
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
