package com.tsoft.tibco.rules;

import java.io.File;

import com.als.core.AbstractRule;
import com.als.core.RuleContext;
import com.als.core.ast.BaseNode;

public class ExampleTibcoRule extends AbstractRule {

	/**
	 * Valida estructura archivo
	 * 
	 * @param basenode
	 *            - valor nulo (por ahora no hay parser)
	 * @param ctx
	 *            - contexto e ejecución de la regla
	 */
	@Override
	public void visit(BaseNode basenode, final RuleContext ctx) {

		/** archivo analizado */
		File file = ctx.getSourceCodeFilename();
		String relativeFilepath = file.getPath().substring(ctx.getCurrentDirectory().getPath().length());

		/** incluye violacion */
		ctx.getReport().addRuleViolation(
				createRuleViolation(ctx, 1, "Ejemplo violacion archivo " + relativeFilepath));

		ctx.getReport().addRuleViolation(
				createRuleViolation(ctx, 5, "Ejemplo violacion2 archivo " + relativeFilepath));
	}

	/**
	 * Post process
	 */
	@Override
	public void postProcess(RuleContext ctx) {

		super.postProcess(ctx);
	}

}
