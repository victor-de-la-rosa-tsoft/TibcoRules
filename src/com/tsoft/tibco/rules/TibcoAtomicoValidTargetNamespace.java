package com.tsoft.tibco.rules;

import java.io.File;
import java.util.regex.Pattern;

import com.als.core.AbstractRule;
import com.als.core.RuleContext;
import com.als.core.RuleViolation;
import com.als.core.ast.BaseNode;
import com.als.core.ast.NodeVisitor;
import com.als.core.ast.NullNode;
import com.als.core.ast.TreeNode;

public class TibcoAtomicoValidTargetNamespace extends AbstractRule {
	
	String patternProcess = null;
	
	public void initialize(RuleContext ctx) {
		
		//solo si es un process, seteo el patron 
		File file = ctx.getSourceCodeFilename();
		if( file.getName().contains(".process") ){
			patternProcess = "http:\\/\\/itg\\.isban\\.cl\\/[\\w]{2,3}\\/[\\w]{2,3}\\/[\\w]{5,10}\\/Process\\/[\\w]+";
		}
		
	};
	
	@Override
	public void visit(BaseNode basenode, final RuleContext ctx) {

		if( basenode instanceof NullNode || patternProcess == null )
		      return;
		
		//itera en el documento actual
	      TreeNode.on(basenode).accept( new NodeVisitor() {       
	          public void visit(BaseNode node) {
	        	  
	              //busca un nodo explicito
	              if (!node.isTypeName("pd:targetNamespace"))
	            	  return;
	                
	              String value = node.getChild(0).getImage();
	              
	              if( !validate( value ) ){
	            	  RuleViolation violation = createRuleViolation(ctx, node.getBeginLine(),"Violacion en el nodo "+node.getTypeName());
	                  ctx.getReport().addRuleViolation(violation);
	              }
	            	   
	            }
	          });
	      
	}

	/**
	 * Post process
	 */
	@Override
	public void postProcess(RuleContext ctx) {

		super.postProcess(ctx);
	}
	
	
	  private boolean validate( String value ){
		  
		  boolean result = false;
		  final Pattern pattern = Pattern.compile(patternProcess);
		  if( pattern.matcher(value).find() )
			  result = true;
		  
		  return result;
	  }
	
	

}
