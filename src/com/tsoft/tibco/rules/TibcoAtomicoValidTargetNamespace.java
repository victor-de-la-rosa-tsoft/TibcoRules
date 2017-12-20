package com.tsoft.tibco.rules;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.jaxen.BaseXPath;
import org.jaxen.JaxenException;

import com.als.core.AbstractRule;
import com.als.core.RuleContext;
import com.als.core.RuleViolation;
import com.als.core.ast.BaseNode;
import com.als.core.ast.NodeVisitor;
import com.als.core.ast.NullNode;
import com.als.core.ast.TreeNode;
import com.als.core.xpath.XPath;
import com.optimyth.qaking.xml.XmlNode;

public class TibcoAtomicoValidTargetNamespace extends AbstractRule {
	
	static String patternProcess = null;
	static String nodeToSearch = null;
	
public void initialize(RuleContext ctx) {
		//aca validar estructura de directorios solamente
		
		
		/*if( file.getName().contains(".process") && file.getAbsolutePath().contains("/Process/") ){
			patternProcess = "http:\\/\\/itg\\.isban\\.cl\\/[\\w]{2,3}\\/[\\w]{2,3}\\/[\\w]{5,10}\\/Process\\/[\\w]+";
		}*/
		
	}


private static String getExtension( String str ){
	
	str = reverse(str);
	str = str.substring(0, str.indexOf('.'));
	str = reverse(str);
	return String.valueOf(str);
}


private static String reverse( String str ){
	
	if( str == null )
		return str;
	
	char[] array = str.toCharArray();

    for (int i = 0, j = array.length-1 ; i < j ; i++, j--){
        // Swap values of left and right
        char temp = array[i];
        array[i] = array[j];
        array[j]=temp;
    }
	
	return String.valueOf(array);
}
	
	@Override
	public void visit(BaseNode basenode, final RuleContext ctx) {

		System.out.println("basenode "+basenode);
		setPatternToSearch( ctx );
		if( basenode instanceof NullNode || patternProcess == null)
		      return;
		else
			System.out.println("pattern seteado a "+patternProcess.toString());
		
		//itera en el documento actual
	      TreeNode.on(basenode).accept( new NodeVisitor() {       
	          public void visit(BaseNode node) {
	        	  
	              //busca un nodo explicito
	              if (!node.isTypeName(nodeToSearch))
	            	  return;
	              
	              if( nodeToSearch.equals("wsdl:definitions") ){
	            	  validWsdl( node, ctx);
	              }else if( nodeToSearch.equals("pd:targetNamespace") ){
	            	  validProcess(node, ctx);
	              }
	            	   
	            }
	          });
	      
	}
	
	
	protected void validProcess(BaseNode node, RuleContext ctx) {
		
		String value = node.getChild(0).getImage();
        
        if( !validate( value ) ){
      	  RuleViolation violation = createRuleViolation(ctx, node.getBeginLine(),"Violacion en el nodo "+node.getTypeName());
            ctx.getReport().addRuleViolation(violation);
        }
		
	}


	@SuppressWarnings("unchecked")
	protected void validWsdl(BaseNode node, RuleContext ctx) {
		
		XmlNode xmlNode = (XmlNode) node;
		Map attributes = xmlNode.getAttributesMap();
        Iterator it = attributes.keySet().iterator();
        while(it.hasNext()){
          String name = (String)it.next();
          if( "targetNamespace".equals(name) ){
        	  if( !validate( (String)attributes.get(name) ) ){
            	  RuleViolation violation = createRuleViolation(ctx, node.getBeginLine(),"Violacion en el nodo "+node.getTypeName()+" atributo targetNamespace : "+(String)attributes.get(name));
                  ctx.getReport().addRuleViolation(violation);
              } 
          }
        }
        
        /*TreeNode.on(node).accept( new NodeVisitor() {       
	          public void visit(BaseNode node) {
	        	  
	        	  if (!node.isTypeName("xs:types") && node.)
	            	  return;
	          }
	          
        });*/
        
        
        XPath xpath = null;
		try {
			xpath = new XPath("//types/schema", ctx);
		} catch (JaxenException e) {
			// TODO Auto-generated catch block
			System.out.println("en xpath se cayo");
			e.printStackTrace();
		}
        @SuppressWarnings("unchecked")
		List<BaseXPath> list = null;
		try {
			//list = (List<BaseNode>) xpath.evaluate(node);
			list = xpath.selectNodes(node); 
			System.out.println("size "+list.size());
		} catch (JaxenException e) {
			// TODO Auto-generated catch block
			System.out.println("en select node se cayo");
			e.printStackTrace();
		}
        
        if( list != null ){
        	for (BaseXPath object : list) {
        		System.out.println("value "+((BaseNode)object).getImage());
        		validWsdl((BaseNode)object, ctx);
			}
        	
        }else
        	return;
        
		
	}


	private static void setPatternToSearch( final RuleContext ctx ){
		
		File file = ctx.getSourceCodeFilename();
		String ext = getExtension( file.getName() );
		System.out.println("initialize validatePath "+file.getName()+" extension "+ext);
		//solo si es un process, seteo el patron 
		
		switch( ext ) {
		case "process":
			//System.out.println("seteando el patternprocess");
			patternProcess = "http:\\/\\/itg\\.isban\\.cl\\/[\\w]{2,3}\\/[\\w]{2,3}\\/[\\w]{5,10}\\/Process\\/[\\w]+";
			nodeToSearch = "pd:targetNamespace";
			break;
		case "wsdl":
			patternProcess = "http:\\/\\/itg\\.isban\\.cl\\/[\\w]{2,3}\\/[\\w]{2,3}\\/[\\w]{5,15}\\/(WSDL\\/)?[\\w]+";
			nodeToSearch = "wsdl:definitions";
			break;
		case "xsd":
			patternProcess = "http:\\/\\/itg\\.isban\\.cl\\/[\\w]{2,3}\\/[\\w]{2,3}\\/[\\w]{5,10}\\/Process\\/[\\w]+\\.xsd";
			nodeToSearch = "pd:targetNamespace";
			break;
		default:
			System.out.println("Extension no valida");
			patternProcess = null;
			break;
		}
		
		
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
		  System.out.println("a validar "+value+ " result "+result);
		  return result;
	  }
	
	

}
