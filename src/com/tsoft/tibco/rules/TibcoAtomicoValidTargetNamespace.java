package com.tsoft.tibco.rules;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.als.core.AbstractRule;
import com.als.core.RuleContext;
import com.als.core.RuleViolation;
import com.als.core.ast.BaseNode;
import com.als.core.ast.NodeVisitor;
import com.als.core.ast.NullNode;
import com.als.core.ast.TreeNode;
import com.optimyth.qaking.xml.XmlNode;

public class TibcoAtomicoValidTargetNamespace extends AbstractRule {
	
	static List<String> patternProcess = null;
	static List<String> nodeToSearch = new ArrayList<String>();
	static String subRule = null;
	
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
	        	  String type = node.getTypeName();
	              if ( !nodeToSearch.contains(type) )
	            	  return;
	              
	              if( subRule.equals("wsdl") || subRule.equals("xsd") ){
	            	  validWsdl( node, ctx);
	              }else if( subRule.equals("process") ){
	            	  validProcess(node, ctx);
	              }
	            	   
	            }
	          });
	      
	}
	
	
	protected void validXsd(BaseNode node, RuleContext ctx){
		
		
		
	}
	
	protected void validProcess(BaseNode node, RuleContext ctx) {
		
		String value = node.getChild(0).getImage();
        
        if( !validate( value, patternProcess.get(0)) ){
      	  RuleViolation violation = createRuleViolation(ctx, node.getBeginLine(),"Violacion en el nodo "+node.getTypeName());
            ctx.getReport().addRuleViolation(violation);
        }
		
	}


	@SuppressWarnings("unchecked")
	protected void validWsdl(BaseNode node, RuleContext ctx) {
		
		XmlNode xmlNode = (XmlNode) node;
		Map<String, String> attributes = xmlNode.getAttributesMap();
        Iterator<String> it = attributes.keySet().iterator();
        while(it.hasNext()){
          String name = (String)it.next();
          if( "targetNamespace".equals(name) ){
        	  String patt = node.getTypeName() == "xs:schema" ? patternProcess.get(0): patternProcess.get(1);
        	  if( !validate( (String)attributes.get(name), patt ) ){
            	  RuleViolation violation = createRuleViolation(ctx, node.getBeginLine(),"Violacion en el nodo "+node.getTypeName()+" atributo targetNamespace : "+(String)attributes.get(name));
                  ctx.getReport().addRuleViolation(violation);
              } 
          }
        }
        
	}


	private static void setPatternToSearch( final RuleContext ctx ){
		
		File file = ctx.getSourceCodeFilename();
		subRule = getExtension( file.getName() );//extension
		System.out.println("initialize validatePath "+file.getName()+" extension "+subRule);
		
		//solo si es un process, seteo el patron 
		
		switch( 0 ) {
		case 1:
			//System.out.println("seteando el patternprocess");
			patternProcess = Arrays.asList("http:\\/\\/itg\\.isban\\.cl\\/[\\w]{2,3}\\/[\\w]{2,3}\\/[\\w]{5,10}\\/Process\\/[\\w]+");
			nodeToSearch = Arrays.asList("pd:targetNamespace");
			break;
		case 2:
			patternProcess = Arrays.asList("http:\\/\\/itg\\.isban.cl\\/[\\w]{5,20}\\/Resources\\/Schemas\\/[\\w]+\\.xsd","http:\\/\\/itg\\.isban\\.cl\\/[\\w]{2,3}\\/[\\w]{2,3}\\/[\\w]{5,15}\\/(WSDL\\/)?[\\w]+");
			nodeToSearch = Arrays.asList("wsdl:definitions","xs:schema");
			break;
		case 3:
			patternProcess = Arrays.asList("http:\\/\\/itg\\.isban.cl\\/[\\w]{5,20}\\/Resources\\/Schemas\\/[\\w]+\\.xsd");
			nodeToSearch = Arrays.asList("xs:schema");
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
	
	
	  private boolean validate( String value, String patt){
		  
		  boolean result = false;
		  final Pattern pattern = Pattern.compile(patt);
		  if( pattern.matcher(value).find() )
			  result = true;
		  System.out.println("a validar "+value+ " result "+result);
		  return result;
	  }
	
	

}
