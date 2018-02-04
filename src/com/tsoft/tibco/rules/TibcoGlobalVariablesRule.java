package com.tsoft.tibco.rules;

import com.als.core.AbstractRule;
import com.als.core.RuleContext;
import com.als.core.RuleViolation;
import com.als.core.ast.BaseNode;
import com.optimyth.qaking.parsers.xpath.SimpleNode;
import com.optimyth.qaking.xml.XmlNode;
import com.tsoft.tibco.rules.model.ASTGlobalVariable;
import org.apache.log4j.Logger;

import java.awt.image.AreaAveragingScaleFilter;
import java.io.File;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Regla que valida la estructura de contenedores y variables globales
 */
public class TibcoGlobalVariablesRule extends AbstractRule {

    private static Logger log = Logger.getLogger(TibcoGlobalVariablesRule.class);

    /**
     * current tibco types namespace URI for substvar files
     */
    private String TIBCOTYPESURI = "http://www.tibco.com/xmlns/repo/types/2002";

    /**
     * valid camel case pattern
     */
    private Pattern camelCasePattern = Pattern.compile("(([A-Z]+[a-z0-9]*)+)");

    /**
     * valid variable path estructure files
     */
    private ArrayList<Pattern> validStructures = new ArrayList<Pattern>();

    /**
     * mandatory variable paths
     */
    private ArrayList<String> mandatoryVariablePaths;

    /**
     * list of found variables paths
     */
    private ArrayList<TibcoVariable> foundVariablesList;

    /**
     * first file found to emit structure violations
     */
    File firstFile;

    public void initialize(RuleContext ctx) {

        validStructures.add(Pattern.compile(".*/[A-Z]+"));

        mandatoryVariablePaths = new ArrayList<String>();
        mandatoryVariablePaths.add("/INFO/CodigoAviso");
        mandatoryVariablePaths.add("/INFO/CodigoError");

        foundVariablesList = new ArrayList<TibcoVariable>();

    }

    @Override
    protected void visit(BaseNode node, RuleContext ctx) {

        //filepath is relative to root folder and normalized using /
        String currentFilepath = ctx.getSourceCodeFilename().getPath()
                .substring(
                        ctx.getCurrentDirectory().getPath().length())
                .replaceAll("\\\\","/");

        if (!ctx.getSourceCodeFilename().getName().endsWith("substvar")) {
            return;
        }

        //save first file
        if (firstFile == null) {
            firstFile = ctx.getSourceCodeFilename();
        }

        log.info("Analysing substvar file " + currentFilepath);
        log.info("Analysing substvar file node " + node);

        if (node == null
                || node.getChild(0) == null) {
            return;
        }

        log.info("Analysing substvar file node0 " + node.getChild(0));

        //nodo raiz "#document/repository"
        XmlNode repositorynode = ((XmlNode) node.getChild(0));

        log.info("Analysing substvar file repositorynode " + repositorynode.getTypeName());

        //valida nombres de variables buscando /repository/globalVariables/globalVariable/name
        if (repositorynode.isTypeName("repository")
                && TIBCOTYPESURI.equals(repositorynode.getNamespaceURI())) {

            log.info("Analysing global variables " + currentFilepath);

            for (int v = 0; v < node.getNumChildren(); v++) {
                processGlobalVariablesNode(repositorynode.getChild(v), currentFilepath, ctx.getSourceCodeFilename(), ctx);
            }
        }
    }

    /**
     * Verifica variables y estructura
     *
     * @param ctx
     */
    @Override
    public void postProcess(RuleContext ctx) {

        ArrayList<String> foundVariablePaths = new ArrayList<String>();

        HashMap<String,TibcoVariable> foundPaths = new HashMap<String,TibcoVariable>();

        for (TibcoVariable tibcoVariable : foundVariablesList) {

            if (!camelCasePattern.matcher(tibcoVariable.getName()).matches()) {
                ctx.getReport().addRuleViolation(

                        //camel case violation
                        new RuleViolation(this, tibcoVariable.line,
                                String.format("nombre de variable no cumple notacion CamalCase: %s", tibcoVariable.name),
                                tibcoVariable.getCurrentFile()));
            }

            foundVariablePaths.add(tibcoVariable.getFilepath() + "/" + tibcoVariable.getName());

            foundPaths.put(tibcoVariable.getFilepath(), tibcoVariable);
        }

        log.info(" substvar file paths founds " + foundPaths);
        System.out.println("p " + foundVariablePaths);
        System.out.println("p " + foundPaths);


        for(String path : foundPaths.keySet()) {
            boolean validStructure = false;

            for(Pattern pattern : validStructures) {

                if(pattern.matcher(path).matches()) {
                    validStructure = true;
                }
            }

            if(validStructure == false) {
                ctx.getReport().addRuleViolation(

                        // estructure violation
                        new RuleViolation(this, 1,
                                String.format("carpeta de variables no cumple con normativa: %s", path),
                                foundPaths.get(path).getCurrentFile()));
            }

            //TODO validar variables tipo password

        }


        for (String mandatoryVariable : mandatoryVariablePaths) {
            if (!foundVariablePaths.contains(mandatoryVariable)) {
                ctx.getReport().addRuleViolation(

                        //variable path violation
                        new RuleViolation(this, 1,
                                String.format("variable obligatoria '%s'  no encontrada:", mandatoryVariable),
                                firstFile));
            }
        }

    }


    private void processGlobalVariablesNode(BaseNode node, String filepath, File currentFile, RuleContext ctx) {
        XmlNode xmlnode = ((XmlNode) node);

        if (node.isTypeName("globalVariables")
                && TIBCOTYPESURI.equals(xmlnode.getNamespaceURI())) {
            for (int v = 0; v < node.getNumChildren(); v++) {
                processGlobalVariableNode(node.getChild(v), filepath, currentFile, ctx);
            }
        }
    }


    private void processGlobalVariableNode(BaseNode node, String filepath, File currentFile, RuleContext ctx) {

        XmlNode xmlnode = ((XmlNode) node);

        if (node.isTypeName("globalVariable")
                && TIBCOTYPESURI.equals(xmlnode.getNamespaceURI())) {

            String variableName = getVariableName(xmlnode);
            if (variableName != null) {
                foundVariablesList.add(new TibcoVariable(variableName, node.getBeginLine(), filepath, currentFile));
            }

        }
    }

    private String getVariableName(BaseNode node) {
        String name = null;

        for (int v = 0; v < node.getNumChildren(); v++) {
            XmlNode xmlChildNode = (XmlNode) node.getChild(v);

            if ("name".equals(xmlChildNode.getNodeName())) {
                name = xmlChildNode.getTextContent();
            }
        }

        return name;
    }

    class TibcoVariable {
        private final File currentFile;
        private final String name;
        private final String filepath;
        private final int line;

        public TibcoVariable(String name, int line, String filepath, File currentFile) {
            this.name = name;
            this.line = line;
            this.filepath = filepath.substring(0, filepath.length() - currentFile.getName().length() - 1);
            this.currentFile = currentFile;
        }

        public String getName() {
            return name;
        }

        public File getCurrentFile() {
            return currentFile;
        }

        public String getFilepath() {
            return filepath;
        }

        @Override
        public String toString() {
            return String.format("%s/%s %d", this.filepath, this.name, this.line);
        }

        public int getLine() {
            return line;
        }
    }
}
