package com.tsoft.tibco.rules;

import java.util.Arrays;
import java.util.Map;

import com.als.core.AbstractRule;
import com.als.core.RuleContext;
import com.als.core.RuleViolation;
import com.als.core.ast.BaseNode;
import com.als.core.ast.NodeVisitor;
import com.als.core.ast.NullNode;
import com.als.core.ast.TreeNode;
import com.optimyth.qaking.xml.XmlNode;

public class TibcoConfiguracion extends AbstractRule{
	
	
	
	
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
		
		if( basenode instanceof NullNode && !"wsdl".equals( getExtension( ctx.getSourceCodeFilename().getName() ) ) )
		      return;
		else
			System.out.println("nodo valido");
		
		//itera en el documento actual
	      TreeNode.on(basenode).accept( new NodeVisitor() {       
	          public void visit(BaseNode node) {
	        	  
	              //busca un nodo explicito
	        	  String type = node.getTypeName();
	              if( !Arrays.asList("wsdl:message","wsdl:portype").contains(type) )
	            	  return;
	              
	              if( "wsdl:message".equals( type ) ){
	            	  //validar que el name sea input o output
	            	  validateMessage(node, ctx);
	              }else if( "wsdl:portType".equals( type ) ){
	            	  //validar name sea ...
	            	  validatePortType(node, ctx);
	              }else
	            	  System.out.println("not def node");
	            	   
	            }
	          });
	      
	}
	
	
	protected void validatePortType( BaseNode node, RuleContext ctx ){
		
		XmlNode xmlNode = (XmlNode) node;
		Map<String, String> attributes = null;
		
		if( node.getChild(0) != null ){
			if( "wsdl:operation".equals( node.getChild(0).getTypeName() ) ){
				attributes = xmlNode.getAttributesMap();
				if( attributes.get("name").matches("[\\w_\\d]+") ){
					attributes = ( (XmlNode)node.getChild(0) ).getAttributesMap();
					for(int i = 0; i < node.getChild(0).getNumChildren(); ++i){
						if( Arrays.asList("wsdl:input","wsdl:output").contains( node.getChild(0).getChild(i).getTypeName() ) ){
							if( "wsdl:input".equals( node.getChild(0).getChild(i).getTypeName() ) ){
								attributes = ( (XmlNode)node.getChild(0).getChild(i) ).getAttributesMap();
								if( !attributes.get("message").matches("tns[\\d]?:INPUT") ){
									//violacion
									reportViolation(ctx , node.getChild(0).getChild(i), "name of message should be INPUT");
								}
							}else{
								attributes = ( (XmlNode)node.getChild(0).getChild(i) ).getAttributesMap();
								if( !attributes.get("message").matches("tns[\\d]?:OUTPUT") )
									//violacion
									reportViolation(ctx , node.getChild(0).getChild(i), "name of message should be OUTPUT");
							}
						}else{
							reportViolation(ctx , node.getChild(0).getChild(i), "nodo no contiene entradas o salidas");
							break;
						}
					}
					
				}else{
					reportViolation(ctx , node.getChild(0), "nombre de la operacion no cumple la nomenclatura");
				}
			}else{
				reportViolation(ctx , node.getChild(0), "debe tener un solo nodo llamado wsdl:operation");
			}
		}else{
			reportViolation(ctx , node.getChild(0), "portype no posee nodos");
		}
}
	
protected void reportViolation(RuleContext ctx, BaseNode node, String msg){
	
	RuleViolation violation = createRuleViolation(ctx, node.getBeginLine(),"Violacion en el nodo "+node.getTypeName()+" "+msg);
    ctx.getReport().addRuleViolation(violation);
	
}
	
	
	protected void validateMessage( BaseNode node, RuleContext ctx ) {
		
		XmlNode xmlNode = (XmlNode) node;
		Map<String, String> attributes = xmlNode.getAttributesMap();
		
		if( Arrays.asList("INPUT","OUTPUT").contains( attributes.get("name") ) ){
			if( "INPUT".equals( attributes.get("name") ) ){
				validateIO((XmlNode)node, ctx, "INPUT");
			}else{
				validateIO((XmlNode)node, ctx, "OUTPUT");
			}
			
		}else{
			System.out.println("el nodo message tiene el atributo nombre = "+attributes.get("name"));
		}
		
		
	}
	
	private void validateIO(XmlNode node, RuleContext ctx, String in) {
		
		String io = in.equals("INPUT") ? "INPUT" : "[_\\w\\d]+";
		Map<String, String> attributes = null;
		XmlNode child = (XmlNode) node.getFirstChild();
		if( child != null && "wsdl:part".equals( child.getTypeName() )  ){
			attributes = child.getAttributesMap();
			if( attributes.get("name") != "parameters" || attributes.get("name").matches("(?i)ns[\\d]?:"+io) ){
				//no cumple el formato de parametros de entrada
				RuleViolation violation = createRuleViolation(ctx, node.getBeginLine(),"Violacion en el nodo "+node.getTypeName());
	            ctx.getReport().addRuleViolation(violation);
			}else{
				//validar en un map que las tres condiciones esten correctas?
				System.out.println("correcto en "+in);
				}
		}else{
			//no tiene parametros de entrada
			RuleViolation violation = createRuleViolation(ctx, node.getBeginLine(),"Violacion en el nodo "+node.getTypeName());
            ctx.getReport().addRuleViolation(violation);
		}
		
	}



	/**
	 * Post process
	 */
	@Override
	public void postProcess(RuleContext ctx) {

		super.postProcess(ctx);
	}
	
}

