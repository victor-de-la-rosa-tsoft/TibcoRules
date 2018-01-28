package com.tsoft.tibco.parser;


import com.als.core.ast.BaseNode;
import com.als.core.parser.AbstractParser;
import com.als.core.parser.AbstractReaderParser;
import com.als.core.parser.ParseException;
import com.als.core.parser.Parser;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;

/**
 * Parser compuesto por los distintos elementos tibco.
 */
public class TibcoCompositeParser extends AbstractReaderParser<ASTTibcoNode> {

    private static Logger log = Logger.getLogger(TibcoCompositeParser.class);

    /**
     * lista de parsers
     */
    private ArrayList<AbstractReaderParser<ASTTibcoNode>> parserList = new ArrayList<AbstractReaderParser<ASTTibcoNode>>();


    /**
     * Devuelve null si ningun parser puede analizar el tipo de archivo
     *
     * @param source
     * @param reader
     * @return
     * @throws ParseException
     */
    @Override
    public ASTTibcoNode parse(String source, Reader reader) throws ParseException {
        for (AbstractReaderParser<ASTTibcoNode> parser : parserList) {

            try {
                ASTTibcoNode node = parser.parse(source, reader);
                if (node != null) {
                    return node;
                }
            } catch (IOException e) {
                throw new ParseException("Error analizando archivo", e);
            }
        }

        log.debug("No existe parser para archivo");
        return null;
    }

    public void addParser(AbstractReaderParser<ASTTibcoNode> parser) {
        this.parserList.add(parser);
    }

}
