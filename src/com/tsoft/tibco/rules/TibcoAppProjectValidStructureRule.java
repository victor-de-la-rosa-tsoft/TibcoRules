package com.tsoft.tibco.rules;

import com.als.core.AbstractRule;
import com.als.core.Rule;
import com.als.core.RuleContext;
import com.als.core.RuleViolation;
import com.als.core.io.TransformedContents;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Regla que revisa estructura de carpetas de un proyeco tibco BW 5.x
 */
public class TibcoAppProjectValidStructureRule extends AbstractRule {

    private static Logger log = Logger.getLogger(TibcoAppProjectValidStructureRule.class);

    private ArrayList<Pattern> validStructure = new ArrayList<Pattern>();


    public void initialize(RuleContext a) {

        validStructure.add(Pattern.compile(".*\\.folder"));
        validStructure.add(Pattern.compile(".*vcrepo.dat"));
        validStructure.add(Pattern.compile(".*/AESchemas/.*\\.aeschema"));
        validStructure.add(Pattern.compile(".*/defaultVars/.*defaultVars\\.substvar"));
        validStructure.add(Pattern.compile(".*/EAR(/.*)?"));
        validStructure.add(Pattern.compile(".*/Deployment(/.*)?"));
        validStructure.add(Pattern.compile(".*/Process/(MAIN|Main)/([^\\/]*)\\.process"));
        validStructure.add(Pattern.compile(".*/Process/(MAIN|Main)/STARTPROCESS/([^\\/]*)\\.process"));
        validStructure.add(Pattern.compile(".*/Process/(SUBPROCESS|SubProcess)/([^\\/]*)\\.process"));
        validStructure.add(Pattern.compile(".*/Process/(SUBPROCESS|SubProcess)/ATOMICO/([^\\/]*)\\.process"));
        validStructure.add(Pattern.compile(".*/Process/(SUBPROCESS|SubProcess)/UTILITIES/([^\\/]*)\\.process"));
        validStructure.add(Pattern.compile(".*/Process/RESTJSON/([^\\/]*)\\.process"));
        validStructure.add(Pattern.compile(".*/Resources/Policies/.*"));
        validStructure.add(Pattern.compile(".*/Resources/Library/.*"));
        validStructure.add(Pattern.compile(".*/Resources/Schemas/([^\\/]*)\\.xsd"));
        validStructure.add(Pattern.compile(".*/Resources/Connections/([^\\/]*)\\.sharedhttp"));
        validStructure.add(Pattern.compile(".*/Resources/Connections/([^\\/]*)\\.sharedjdbc"));
        validStructure.add(Pattern.compile(".*/Resources/Connections/([^\\/]*)\\.sharedjms"));
        validStructure.add(Pattern.compile(".*/Services/.*\\.wsdl"));
        validStructure.add(Pattern.compile(".*/Services/.*.serviceagent"));

    }


    public void postProcess(RuleContext ctx) {

        //recorre carpetas
        processFolder(ctx.getCurrentDirectory(), ctx.getCurrentDirectory().getPath(), ctx);

    }

    private void processFolder(File currentDirectory, String rootDirectory, RuleContext ctx) {

        if (currentDirectory.isDirectory()) {
            for (File file : currentDirectory.listFiles()) {
                processFolder(file, rootDirectory, ctx);
            }
        } else {
            validFile(currentDirectory.getPath().substring(rootDirectory.length()), rootDirectory, ctx);
        }
    }

    private void validFile(String filepath, String rootDirectory, RuleContext ctx) {
        boolean pathIsValid = false;
        for (Pattern validPath : validStructure) {

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
