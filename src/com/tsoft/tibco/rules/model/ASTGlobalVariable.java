package com.tsoft.tibco.rules.model;

import com.als.core.ast.BaseNode;
import com.als.core.ast.IToken;
import com.tsoft.tibco.parser.ASTTibcoNode;

import java.text.ParseException;
import java.util.ArrayList;

public class ASTGlobalVariable extends ASTTibcoNode {

    private String name;
    private String value;
    private String image;

    public ASTGlobalVariable(String name, String value) {
        this.name = name;
        this.value = value;
        this.image = String.format("<name>%s</name><value>%s</value>", name, value);
    }

    @Override
    public BaseNode getChild(int i) {
        return null;
    }

    @Override
    public boolean isTypeName(String type) {
        return "globalVariable".equals(type) ? true : false;
    }


    @Override
    public int getNumChildren() {
        return 0;
    }

    @Override
    public String getTypeName() {
        return "globalVariable";
    }

    @Override
    public String getImage() {
        return this.image;
    }

    public String getVarName() {
        return this.name;
    }

    public String getVarValue() {
        return this.value;
    }
}
