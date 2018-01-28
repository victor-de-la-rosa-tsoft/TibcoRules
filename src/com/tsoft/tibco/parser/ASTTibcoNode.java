package com.tsoft.tibco.parser;

import com.als.core.ast.BaseNode;
import com.als.core.ast.IToken;

public abstract class ASTTibcoNode implements BaseNode {

    private BaseNode parent;
    private String dialect;
    private int beginLine;

    @Override
    public BaseNode getParent() {
        return (this.parent);
    }

    @Override
    public String getDialect() {
        return this.dialect;
    }

    @Override
    public IToken getIToken() {
        return null;
    }

    @Override
    public int getBeginColumn() {
        return 0;
    }


    @Override
    public int getBeginLine() {
        return this.beginLine;
    }

    @Override
    public void setDialect(String dialect) {
        this.dialect = dialect;
    }

    @Override
    public int getNumChildren() {
        return 0;
    }

    @Override
    public int getEndLine() {
        return this.beginLine;
    }

    @Override
    public String getTechnology() {
        return "tibco";
    }

    @Override
    public int getEndColumn() {
        return 0;
    }

}
