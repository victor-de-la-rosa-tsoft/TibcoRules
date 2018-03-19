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

/**
 * Identifica el tipo de elemento y aplica patrones de diseño de targetnamespaces segun documento de buenas practicas
 */
public class TibcoTargetNamespace extends AbstractRule {

    List<String> patternProcess = null;
    List<String> nodeToSearch = new ArrayList<String>();
    String subRule = null;

    //namespace process
    //http://itg.isban.cl/[País]/[Aplicativo]/[Tecnologoa][Invocacion][Transaccion]/Process/[DescripcionFuncionalidad]

    //namespace atomico
    //http://itg.isban.cl/[NombreProyecto]/[PathProcess]/[Nombre-Servicio-atomico]

    //namespace service
    //http://itg.isban.cl/[País]/[Aplicativo]/[Tecnología][Invocación][Transacción]/[DescripciónFuncionalidad]

    //namespace schema
    //http:\/\/itg\.isban.cl\/[\w]{5,20}\/Resources\/Schemas\/[\w]+\.xsd


    @Override
    public void visit(BaseNode basenode, final RuleContext ctx) {

        setPatternToSearch(ctx);
        if (basenode instanceof NullNode || patternProcess == null)
            return;
        else
            System.out.println("pattern seteado a " + patternProcess.toString());

        //itera en el documento actual
        TreeNode.on(basenode).accept(new NodeVisitor() {
            public void visit(BaseNode node) {

                //busca un nodo explicito
                String type = node.getTypeName();
                if (!nodeToSearch.contains(type))
                    return;

                if (subRule.equals("wsdl") || subRule.equals("xsd")) {
                    validWsdl(node, ctx);
                } else if (subRule.equals("process")) {
                    validProcess(node, ctx);
                }

            }
        });

    }


    protected void validXsd(BaseNode node, RuleContext ctx) {


    }

    protected void validProcess(BaseNode node, RuleContext ctx) {

        String value = node.getChild(0).getImage();

        if (!validate(value, patternProcess.get(0))) {
            RuleViolation violation = createRuleViolation(ctx, node.getBeginLine(), "Violacion en el nodo " + node.getTypeName());
            ctx.getReport().addRuleViolation(violation);
        }

    }


    @SuppressWarnings("unchecked")
    protected void validWsdl(BaseNode node, RuleContext ctx) {

        XmlNode xmlNode = (XmlNode) node;
        Map<String, String> attributes = xmlNode.getAttributesMap();
        Iterator<String> it = attributes.keySet().iterator();
        while (it.hasNext()) {
            String name = (String) it.next();
            if ("targetNamespace".equals(name)) {
                String patt = node.getTypeName() == "xs:schema" ? patternProcess.get(0) : patternProcess.get(1);
                if (!validate((String) attributes.get(name), patt)) {
                    RuleViolation violation = createRuleViolation(ctx, node.getBeginLine(), "Violacion en el nodo " + node.getTypeName() + " atributo targetNamespace : " + (String) attributes.get(name));
                    ctx.getReport().addRuleViolation(violation);
                }
            }
        }

    }


    private void setPatternToSearch(final RuleContext ctx) {

        File file = ctx.getSourceCodeFilename();
        subRule = getExtension(file.getName());//extension
        System.out.println("initialize validatePath " + file.getName() + " extension " + subRule);

        if("process".equalsIgnoreCase(subRule)) {
            String serviceName = TibcoLanguaje.getServiceName(file);
            if(serviceName == null) {
                //process no
                patternProcess = null;
                nodeToSearch = null;
            }
            else if(TibcoLanguaje.isAtomicoService(serviceName)) {
                patternProcess = Arrays.asList("http:\\/\\/itg\\.isban\\.cl\\/[\\w]{2,3}\\/[\\w]{2,3}\\/[\\w]{5,10}\\/Process\\/[\\w]+");
                nodeToSearch = Arrays.asList("pd:targetNamespace");
            }
            else {
                patternProcess = Arrays.asList("http:\\/\\/itg\\.isban\\.cl\\/[\\w]{2,3}\\/[\\w]{2,3}\\/[\\w]{5,10}\\/Process\\/[\\w]+");
                nodeToSearch = Arrays.asList("pd:targetNamespace");
            }
        }
        else if("wsdl".equalsIgnoreCase(subRule)) {
            patternProcess = Arrays.asList("http:\\/\\/itg\\.isban.cl\\/[\\w]{5,20}\\/Resources\\/Schemas\\/[\\w]+\\.xsd", "http:\\/\\/itg\\.isban\\.cl\\/[\\w]{2,3}\\/[\\w]{2,3}\\/[\\w]{5,15}\\/(WSDL\\/)?[\\w]+");
            nodeToSearch = Arrays.asList("wsdl:definitions", "xs:schema");
        }
        else if("schema".equalsIgnoreCase(subRule)){
            patternProcess = Arrays.asList("http:\\/\\/itg\\.isban.cl\\/[\\w]{5,20}\\/Resources\\/Schemas\\/[\\w]+\\.xsd");
            nodeToSearch = Arrays.asList("xs:schema");
        }
    }

    /**
     * Post process
     */
    @Override
    public void postProcess(RuleContext ctx) {

        super.postProcess(ctx);
    }


    private static String getExtension(String str) {

        str = reverse(str);
        str = str.substring(0, str.indexOf('.'));
        str = reverse(str);
        return String.valueOf(str);
    }


    private static String reverse(String str) {

        if (str == null)
            return str;

        char[] array = str.toCharArray();

        for (int i = 0, j = array.length - 1; i < j; i++, j--) {
            // Swap values of left and right
            char temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }

        return String.valueOf(array);
    }


    private boolean validate(String value, String patt) {

        boolean result = false;
        final Pattern pattern = Pattern.compile(patt);
        if (pattern.matcher(value).find())
            result = true;
        System.out.println("a validar " + value + " result " + result);
        return result;
    }


}
