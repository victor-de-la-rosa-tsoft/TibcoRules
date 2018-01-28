package com.tsoft.tibco.rules.model;

import com.als.core.ast.BaseNode;
import com.tsoft.tibco.parser.ASTTibcoNode;

import java.util.ArrayList;

public class ASTGlobalVariables extends ASTTibcoNode {

    private ArrayList<ASTGlobalVariable> globalVariableList = new ArrayList<ASTGlobalVariable>();

    public void addGlobalVariable(ASTGlobalVariable globalVariable) {
        this.globalVariableList.add(globalVariable);
    }

    @Override
    public BaseNode getChild(int i) {
        return this.globalVariableList.get(i);
    }

    @Override
    public boolean isTypeName(String type) {
        return "globalVariables".equals(type);
    }

    @Override
    public String getTypeName() {
        return "globalVariables";
    }

    @Override
    public String getImage() {
        return "<globalVariables>";
    }
}
