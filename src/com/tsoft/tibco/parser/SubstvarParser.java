package com.tsoft.tibco.parser;

import com.als.core.parser.AbstractReaderParser;
import com.als.core.parser.ParseException;
import com.tsoft.tibco.rules.model.ASTGlobalVariable;
import com.tsoft.tibco.rules.model.ASTGlobalVariables;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.LocationSAXReader;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

/**
 * Parser que identifica
 */
public class SubstvarParser extends AbstractReaderParser<ASTTibcoNode> {

    private static Logger log = Logger.getLogger(SubstvarParser.class);

    @Override
    public ASTTibcoNode parse(String source, Reader reader) throws IOException, ParseException {

        ASTTibcoNode basenode = null;

        Document document = null;

        try {

            //TODO move saxreader to parent parser
            document = (new LocationSAXReader(null)).read(reader);

            Element rootElement = document.getRootElement();
            rootElement.addNamespace("repo",
                    "http://www.tibco.com/xmlns/repo/types/2002");

            // valid substvar repository
            if ("repository".equals(rootElement.getName())) {

                basenode = parseSubstvars(rootElement);

            } else {
                //unknown tibco file
                log.debug("Unknown tibco file, file discard, rootnode: " + rootElement.getName());
            }


        } catch (DocumentException e) {
            e.printStackTrace();
            log.error("ERROR reading tibco file. ", e);
        }

        return basenode;
    }

    private ASTTibcoNode parseSubstvars(Element rootElement) {

        ASTGlobalVariables globalVariablesNode = new ASTGlobalVariables();

        List<Element> globalVariablesElements = rootElement
                .selectNodes("repo:globalVariables/repo:globalVariable");

        for (Element variableElement : globalVariablesElements) {

            globalVariablesNode.addGlobalVariable(
                    new ASTGlobalVariable(
                            variableElement.element("name").getText(),
                            variableElement.element("value").getText()));
        }

        return globalVariablesNode;
    }
}
