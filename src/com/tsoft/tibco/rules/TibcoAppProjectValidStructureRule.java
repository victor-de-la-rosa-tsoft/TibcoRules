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
 *
 * Emplea un juego de expresiones regulares para validar carpetas y tipos de archivos admitidos.
 *
 * TODO Analizar rendimiento y Optimizar expresiones regulares
 */
public class TibcoAppProjectValidStructureRule extends AbstractRule {

    private static Logger log = Logger.getLogger(TibcoAppProjectValidStructureRule.class);

    private ArrayList<Pattern> validStructure = new ArrayList<Pattern>();


    public void initialize(RuleContext a) {

        //como nombres de carpeta se aceptan solo A-Z a-z 0-9 y _, como nombre de archivo: A-Z a-z 0-9 _ y -
        validStructure.add(Pattern.compile(".*\\.folder"));
        validStructure.add(Pattern.compile(".*vcrepo.dat"));
        validStructure.add(Pattern.compile(".*/AESchemas(/[A-Za-z0-9_]*)*/[A-Za-z0-9_-]*\\.aeschema"));
        validStructure.add(Pattern.compile(".*/defaultVars(/[A-Za-z0-9_]*)*/[A-Za-z0-9_-]*\\.substvar"));
        validStructure.add(Pattern.compile(".*/EAR(/.*)?"));
        validStructure.add(Pattern.compile(".*/Deployment(/.*)?"));
        validStructure.add(Pattern.compile(".*/Process/(MAIN|Main)(/[A-Za-z0-9_]*)*/[A-Za-z0-9_-]*\\.process"));
        validStructure.add(Pattern.compile(".*/Process/(SUBPROCESS|SubProcess)(/[A-Za-z0-9_]*)*/[A-Za-z0-9_-]*\\.process"));
        validStructure.add(Pattern.compile(".*/Process/(RESTJSON|RestJson)(/[A-Za-z0-9_]*)*/[A-Za-z0-9_]-*\\.process"));

        validStructure.add(Pattern.compile(".*/Resources/Connections(/[A-Za-z0-9_]*)*/[A-Za-z0-9_-]*\\.[a-z]*shared[a-z]+]"));
        validStructure.add(Pattern.compile(".*/Resources/Policies/.*"));
        validStructure.add(Pattern.compile(".*/Resources/Library/.*"));
        validStructure.add(Pattern.compile(".*/Resources/Schemas(/[A-Za-z0-9_]*)*/[A-Za-z0-9_-]*\\.xsd"));
        validStructure.add(Pattern.compile(".*/Resources/Xml(/[A-Za-z0-9_]*)*/[A-Za-z0-9_-]*\\.xml"));
        validStructure.add(Pattern.compile(".*/Resources/Xslt(/[A-Za-z0-9_]*)*/[A-Za-z0-9_-]*\\.xslt"));
        validStructure.add(Pattern.compile(".*/Resources/Parse(/[A-Za-z0-9_]*)*/[A-Za-z0-9_-]*\\.[a-z]*parse"));

        validStructure.add(Pattern.compile(".*/Resources/Wsdl(/[A-Za-z0-9_]*)*/[A-Za-z0-9_-]*\\.wsdl"));
        validStructure.add(Pattern.compile(".*/Services(/[A-Za-z0-9_]*)*/[A-Za-z0-9_-]*\\.(serviceagent|wsdl)"));



    }


    public void postProcess(RuleContext ctx) {

        //recorre carpetas
        processFolder(ctx.getCurrentDirectory(), ctx.getCurrentDirectory().getPath(), ctx);

    }

    /**
     * Analiza de forma recursiva la estructura de carpetas y archivos analizados.
     *
     * @param currentDirectory
     * @param rootDirectory
     * @param ctx
     */
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

            //si cumple alguno de los patrones, el path es correcto
            if (validPath.matcher(filepath.replaceAll("\\\\", "/")).matches()) {
                pathIsValid = true;
                log.debug(String.format("path correcto %s matches %s", filepath, validPath));

            }
        }

        if (!pathIsValid) {
            ctx.getReport().addRuleViolation(
                    new RuleViolation(this, 1,
                            String.format("carpeta o archivo no cumple con norma de diseño: %s", filepath),
                            new File(rootDirectory + filepath)));
        }
    }
}
