package com.tsoft.tibco.rules;

import com.als.core.AbstractRule;
import com.als.core.RuleContext;
import com.als.core.RuleViolation;
import com.als.core.ast.BaseNode;
import com.tsoft.tibco.rules.model.ASTGlobalVariable;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Regla que valida la estructura de contenedores y variables globales
 */
public class TibcoGlobalVariablesRule extends AbstractRule {

    private static Logger log = Logger.getLogger(TibcoGlobalVariablesRule.class);

    private ArrayList<Pattern> validStructures = new ArrayList<Pattern>();


    public void initialize(RuleContext a) {

        validStructures.add(Pattern.compile(".*\\.folder"));

    }

    @Override
    protected void visit(BaseNode node, RuleContext ctx) {

        String currentDirectory = ctx.getCurrentDirectory().getPath();
        String currentFilepath = ctx.getSourceCodeFilename().getPath()
                .substring(
                        ctx.getCurrentDirectory().getPath().length());

        if (node.isTypeName("globalVariable")) {
            validGlobalVariable(currentDirectory, currentFilepath, ((ASTGlobalVariable) node).getVarName(), ctx);
        }

    }


    private void validGlobalVariable(String rootDirectory, String filepath, String variableName, RuleContext ctx) {
        boolean pathIsValid = false;
        for (Pattern validPath : validStructures) {

            if (validPath.matcher(filepath.replaceAll("\\\\", "/")).matches()) {
                pathIsValid = true;
                log.debug(String.format("path %s matches %s", filepath, validPath));

            }
        }

        if (!pathIsValid) {
            ctx.getReport().addRuleViolation(
                    new RuleViolation(this, 1,
                            String.format("carpeta o archivo no cumple con normativa: %s", filepath),
                            new File(rootDirectory + filepath)));
        }
    }
}
