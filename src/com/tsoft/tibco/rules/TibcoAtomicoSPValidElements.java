package com.tsoft.tibco.rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;

import com.als.core.AbstractRule;
import com.als.core.RuleContext;
import com.als.core.RuleViolation;
import com.als.core.ast.BaseNode;
import com.als.core.ast.NodeVisitor;
import com.als.core.ast.NullNode;
import com.als.core.ast.TreeNode;
import com.optimyth.qaking.xml.XmlNode;

public class TibcoAtomicoSPValidElements extends AbstractRule{
	
	static List<HashMap<String, ArrayList<String>>> struct =  new ArrayList<HashMap<String,ArrayList<String> > >();
	
	//crear la estructura a validar
	boolean setStructure(Object type){
		
		boolean result = false;
		
		if( "process".equals((String)type) ){
			setProcessStruct();
			result = true;
		}
		
		
		return result;
	}
	
	//esto debe ser definido de forma externa
	@SuppressWarnings("serial")
	private void setProcessStruct() {
		HashMap<String, ArrayList<String>> e = new HashMap<String, ArrayList<String> >() {{ put("name", new ArrayList<String>() {{ add("Start"); }} );
		   																				  put("transitions", new ArrayList<String>() {{ add("Render JSON"); }});}};
		struct.add(e);        
		struct.add( new HashMap<String, ArrayList<String> >() {{ put("name", new ArrayList<String>() {{ add("Render JSON"); }});put("transitions", new ArrayList<String>() {{ add("FormatoJsonToMT"); }} ); }} );
		struct.add( new HashMap<String, ArrayList<String> >() {{ put("name", new ArrayList<String>() {{ add("FormatoJsonToMT"); }});put("transitions", new ArrayList<String>() {{ add("Log"); }} ); }} );
		struct.add( new HashMap<String, ArrayList<String> >() {{ put("name", new ArrayList<String>() {{ add("Log"); }});put("transitions", new ArrayList<String>() {{ add("RequestReplyMQ"); }} ); }} );
		struct.add( new HashMap<String, ArrayList<String> >() {{ put("name", new ArrayList<String>() {{ add("RequestReplyMQ"); }});put("transitions", new ArrayList<String>() {{ add("End"); add("Log-1"); }} ); }} );
		struct.add( new HashMap<String, ArrayList<String> >() {{ put("name", new ArrayList<String>() {{ add("Log-1"); }});put("transitions", new ArrayList<String>() {{ add("FormatoMTToJson");}} ); }} );
		struct.add( new HashMap<String, ArrayList<String> >() {{ put("name", new ArrayList<String>() {{ add("FormatoMTToJson"); }});put("transitions", new ArrayList<String>() {{ add("Log-2");}} ); }} );
		struct.add( new HashMap<String, ArrayList<String> >() {{ put("name", new ArrayList<String>() {{ add("Log-2"); }});put("transitions", new ArrayList<String>() {{ add("RespJsonOK");}} ); }} );
		struct.add( new HashMap<String, ArrayList<String> >() {{ put("name", new ArrayList<String>() {{ add("RespJsonOK"); }});put("transitions", new ArrayList<String>() {{ add("End"); add("Log-OK");}} );}} ); 
		struct.add( new HashMap<String, ArrayList<String> >() {{ put("name", new ArrayList<String>() {{ add("Log-2"); }});put("transitions", new ArrayList<String>() {{ add("RespJsonOK");}} ); }} );
		struct.add( new HashMap<String, ArrayList<String> >() {{ put("name", new ArrayList<String>() {{ add("Catch"); }});put("transitions", new ArrayList<String>() {{ add("LogError");}} ); }} );
		struct.add( new HashMap<String, ArrayList<String> >() {{ put("name", new ArrayList<String>() {{ add("LogError"); }});put("transitions", new ArrayList<String>() {{ add("End");}} ); }} );

		
	}

	

	@Override
	public void visit(BaseNode basenode, final RuleContext ctx) {

		if( basenode instanceof NullNode )
		   return;
		setStructure("process");
		HashMap< String, List<String> > localStruct = new HashMap< String, List<String> > ();
		
		//itera en el documento actual
	    TreeNode.on(basenode).accept( new NodeVisitor() {       
	        public void visit(BaseNode node){
	              //busca un nodo explicito
	        	      String type = node.getTypeName();
	              if ( "pd:transition".equals(type) ){//if is a transition node
	            	  	String from = null, to = null;
	            	  	for(int i = 0; i < node.getNumChildren(); ++i){//traverse childs of "pd:transition node"
	            	  		if( "pd:from".equals( node.getChild(i).getTypeName() )){
	            	  			from = ((XmlNode)node.getChild(i) ).getTextContent();
	            	  		}else if( "pd:to".equals( node.getChild(i).getTypeName() )){
	            	  			to = ((XmlNode)node.getChild(i) ).getTextContent();
	            	  		}
	            	  		if( from != null && to != null ) {//aet vertex and break the loop
	            	  			addTransition(localStruct, from, to);
	            	  			//System.out.println("transition : "+from+" -> "+to);
	            	  			break;
	            	  		}
	            	  	}
	              }
	            }
	     });
	    
	    validateStructure(localStruct, ctx);
	      
	}
	
	private void validateStructure( HashMap< String, List<String> > localStruct, final RuleContext ctx ) {
		
		
		for( HashMap<String, ArrayList<String>> obj : struct) {//iterate on template
			if( localStruct.containsKey(obj.get("name").get(0)) ){//if localStruct contain node of template
				//System.out.println("contiene "+obj.get("name").get(0) );
				for( String tr : obj.get("transitions")  ) {//iterate on list of transitions
					List<String> localTrans = localStruct.get( obj.get("name").get(0) );
					if( !localTrans.contains(tr) ) {
						RuleViolation violation = createRuleViolation(ctx, 0,"Violacion de structura, la actividad <"+obj.get("name").get(0)+"> debe contener las siguientes transiciones : <"+
								tr+">, transicion/es observada/s -> "+Arrays.toString( localTrans.toArray(new String[localTrans.size()]) ));
				        ctx.getReport().addRuleViolation(violation);
					}
				}
			}else {
				RuleViolation violation = createRuleViolation(ctx, 0,"Violacion de structura, estructura local no contiene la siguiente activity: "+obj.get("name").get(0));
		        ctx.getReport().addRuleViolation(violation);
			}
		}
		
		
		
	}

	protected void addTransition(HashMap< String, List<String> > localStruct, String from, String to){
		
		if( !localStruct.containsKey(from) ){
			localStruct.put(from, new ArrayList<String>( Arrays.asList(to) ));
		}else {
			//localStruct.get(from).add(to);
			ArrayList<String> tmp = (ArrayList<String>) localStruct.get(from);
			tmp.add(to);
			localStruct.replace(from, tmp);
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
