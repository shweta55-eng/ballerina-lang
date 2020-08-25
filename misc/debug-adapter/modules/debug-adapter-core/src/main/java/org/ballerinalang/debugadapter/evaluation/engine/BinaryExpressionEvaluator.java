/*
 * Copyright (c) 2020, WSO2 Inc. (http://wso2.com) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ballerinalang.debugadapter.evaluation.engine;

import com.sun.jdi.Value;
import io.ballerinalang.compiler.syntax.tree.BinaryExpressionNode;
import io.ballerinalang.compiler.syntax.tree.SyntaxKind;
import org.ballerinalang.debugadapter.SuspendedContext;
import org.ballerinalang.debugadapter.evaluation.BExpressionValue;
import org.ballerinalang.debugadapter.evaluation.EvaluationException;
import org.ballerinalang.debugadapter.evaluation.EvaluationExceptionKind;
import org.ballerinalang.debugadapter.evaluation.EvaluationUtils;
import org.ballerinalang.debugadapter.variable.BVariable;
import org.ballerinalang.debugadapter.variable.BVariableType;
import org.ballerinalang.debugadapter.variable.VariableFactory;

/**
 * Binary expression evaluator implementation.
 *
 * @since 2.0.0
 */
public class BinaryExpressionEvaluator extends Evaluator {

    private final BinaryExpressionNode syntaxNode;
    private final Evaluator lhsEvaluator;
    private final Evaluator rhsEvaluator;

    public BinaryExpressionEvaluator(SuspendedContext context, BinaryExpressionNode node, Evaluator lhsEvaluator,
                                     Evaluator rhsEvaluator) {
        super(context);
        this.syntaxNode = node;
        this.lhsEvaluator = lhsEvaluator;
        this.rhsEvaluator = rhsEvaluator;
    }

    @Override
    public BExpressionValue evaluate() throws EvaluationException {
        try {
            // Evaluates the lhs and rhs expressions.
            BExpressionValue lhsResult = lhsEvaluator.evaluate();
            BExpressionValue rhsResult = rhsEvaluator.evaluate();
            return performOperation(lhsResult, rhsResult);
        } catch (EvaluationException e) {
            throw e;
        } catch (Exception e) {
            throw new EvaluationException(String.format(EvaluationExceptionKind.CUSTOM_ERROR.getString(), "An " +
                    "internal error is occurred while evaluating the binary expression: " + syntaxNode.toSourceCode()));
        }
    }

    /**
     * Performs the binary operation and returns the evaluation result.
     *
     * @param lhs LHS evaluation result
     * @param rhs RHS evaluation result
     * @return returns the evaluation result of the binary operation.
     */
    private BExpressionValue performOperation(BExpressionValue lhs, BExpressionValue rhs) throws EvaluationException {
        Value lValue = lhs.getJdiValue();
        Value rValue = rhs.getJdiValue();
        BVariable lVar = VariableFactory.getVariable(context, lValue, lValue.type().name(), "lVar");
        BVariable rVar = VariableFactory.getVariable(context, rValue, rValue.type().name(), "rVar");
        SyntaxKind operatorType = syntaxNode.operator().kind();

        switch (operatorType) {
            case PLUS_TOKEN:
                return add(lVar, rVar);
            case MINUS_TOKEN:
                return sub(lVar, rVar);
            case ASTERISK_TOKEN:
                return mul(lVar, rVar);
            case SLASH_TOKEN:
                return div(lVar, rVar);
            case PERCENT_TOKEN:
                return mod(lVar, rVar);
            default:
                throw createUnsupportedOperationException(lVar, rVar, operatorType);
        }
    }

    /**
     * Performs addition/concatenation operation on the given ballerina variable values and returns the result.
     */
    private BExpressionValue add(BVariable lVar, BVariable rVar) throws EvaluationException {
        if (lVar.getBType() == BVariableType.INT && rVar.getBType() == BVariableType.INT) {
            // int + int
            long result = Long.parseLong(lVar.computeValue()) + Long.parseLong(rVar.computeValue());
            return EvaluationUtils.make(context, result);
        } else if (lVar.getBType() == BVariableType.FLOAT && rVar.getBType() == BVariableType.FLOAT) {
            // float + float
            double result = Double.parseDouble(lVar.computeValue()) + Double.parseDouble(rVar.computeValue());
            return EvaluationUtils.make(context, result);
        } else if ((lVar.getBType() == BVariableType.INT && rVar.getBType() == BVariableType.FLOAT)
                || (lVar.getBType() == BVariableType.FLOAT && rVar.getBType() == BVariableType.INT)) {
            // int + float , float + int
            double result = Double.parseDouble(lVar.computeValue()) + Double.parseDouble(rVar.computeValue());
            return EvaluationUtils.make(context, result);
        } else if (lVar.getBType() == BVariableType.DECIMAL || rVar.getBType() == BVariableType.DECIMAL) {
            // Todo - Add support for
            // decimal + decimal
            // decimal + int , int + decimal
            // decimal + float , float + decimal
            throw createUnsupportedOperationException(lVar, rVar, SyntaxKind.PLUS_TOKEN);
        } else if (lVar.getBType() == BVariableType.STRING && rVar.getBType() == BVariableType.STRING) {
            // string + string
            String result = lVar.computeValue() + rVar.computeValue();
            return EvaluationUtils.make(context, result);
        } else if (lVar.getBType() == BVariableType.XML && rVar.getBType() == BVariableType.XML) {
            // xml + xml
            // Todo - Add support
            throw createUnsupportedOperationException(lVar, rVar, SyntaxKind.PLUS_TOKEN);
        } else {
            throw createUnsupportedOperationException(lVar, rVar, SyntaxKind.PLUS_TOKEN);
        }
    }

    /**
     * Performs subtraction operation on the given ballerina variable values and returns the result.
     */
    private BExpressionValue sub(BVariable lVar, BVariable rVar) throws EvaluationException {
        if (lVar.getBType() == BVariableType.INT && rVar.getBType() == BVariableType.INT) {
            // int - int
            long result = Long.parseLong(lVar.computeValue()) - Long.parseLong(rVar.computeValue());
            return EvaluationUtils.make(context, result);
        } else if (lVar.getBType() == BVariableType.FLOAT && rVar.getBType() == BVariableType.FLOAT) {
            // float - float
            double result = Double.parseDouble(lVar.computeValue()) - Double.parseDouble(rVar.computeValue());
            return EvaluationUtils.make(context, result);
        } else if ((lVar.getBType() == BVariableType.INT && rVar.getBType() == BVariableType.FLOAT)
                || (lVar.getBType() == BVariableType.FLOAT && rVar.getBType() == BVariableType.INT)) {
            // int - float , float - int
            double result = Double.parseDouble(lVar.computeValue()) - Double.parseDouble(rVar.computeValue());
            return EvaluationUtils.make(context, result);
        } else if (lVar.getBType() == BVariableType.DECIMAL || rVar.getBType() == BVariableType.DECIMAL) {
            // Todo - Add support for
            // decimal - decimal
            // decimal - int , int - decimal
            // decimal - float , float - decimal
            throw createUnsupportedOperationException(lVar, rVar, SyntaxKind.MINUS_TOKEN);
        } else {
            throw createUnsupportedOperationException(lVar, rVar, SyntaxKind.MINUS_TOKEN);
        }
    }

    /**
     * Performs multiplication operation on the given ballerina variable values and returns the result.
     */
    private BExpressionValue mul(BVariable lVar, BVariable rVar) throws EvaluationException {
        if (lVar.getBType() == BVariableType.INT && rVar.getBType() == BVariableType.INT) {
            // int * int
            long result = Long.parseLong(lVar.computeValue()) * Long.parseLong(rVar.computeValue());
            return EvaluationUtils.make(context, result);
        } else if (lVar.getBType() == BVariableType.FLOAT && rVar.getBType() == BVariableType.FLOAT) {
            // float * float
            double result = Double.parseDouble(lVar.computeValue()) * Double.parseDouble(rVar.computeValue());
            return EvaluationUtils.make(context, result);
        } else if ((lVar.getBType() == BVariableType.INT && rVar.getBType() == BVariableType.FLOAT)
                || (lVar.getBType() == BVariableType.FLOAT && rVar.getBType() == BVariableType.INT)) {
            // int * float , float * int
            double result = Double.parseDouble(lVar.computeValue()) * Double.parseDouble(rVar.computeValue());
            return EvaluationUtils.make(context, result);
        } else if (lVar.getBType() == BVariableType.DECIMAL || rVar.getBType() == BVariableType.DECIMAL) {
            // Todo - Add support for
            // decimal * decimal
            // decimal * int , int * decimal
            // decimal * float , float * decimal
            throw createUnsupportedOperationException(lVar, rVar, SyntaxKind.ASTERISK_TOKEN);
        } else {
            throw createUnsupportedOperationException(lVar, rVar, SyntaxKind.ASTERISK_TOKEN);
        }
    }

    /**
     * Performs division operation on the given ballerina variable values and returns the result.
     */
    private BExpressionValue div(BVariable lVar, BVariable rVar) throws EvaluationException {
        if (lVar.getBType() == BVariableType.INT && rVar.getBType() == BVariableType.INT) {
            // int / int
            long result = Long.parseLong(lVar.computeValue()) / Long.parseLong(rVar.computeValue());
            return EvaluationUtils.make(context, result);
        } else if (lVar.getBType() == BVariableType.FLOAT && rVar.getBType() == BVariableType.FLOAT) {
            // float / float
            double result = Double.parseDouble(lVar.computeValue()) / Double.parseDouble(rVar.computeValue());
            return EvaluationUtils.make(context, result);
        } else if ((lVar.getBType() == BVariableType.INT && rVar.getBType() == BVariableType.FLOAT)
                || (lVar.getBType() == BVariableType.FLOAT && rVar.getBType() == BVariableType.INT)) {
            // int / float , float / int
            double result = Double.parseDouble(lVar.computeValue()) / Double.parseDouble(rVar.computeValue());
            return EvaluationUtils.make(context, result);
        } else if (lVar.getBType() == BVariableType.DECIMAL || rVar.getBType() == BVariableType.DECIMAL) {
            // Todo - Add support for
            // decimal / decimal
            // decimal / int , int / decimal
            // decimal / float , float / decimal
            throw createUnsupportedOperationException(lVar, rVar, SyntaxKind.SLASH_TOKEN);
        } else {
            throw createUnsupportedOperationException(lVar, rVar, SyntaxKind.SLASH_TOKEN);
        }
    }

    /**
     * Performs modulus operation on the given ballerina variable values and returns the result.
     */
    private BExpressionValue mod(BVariable lVar, BVariable rVar) throws EvaluationException {
        if (lVar.getBType() == BVariableType.INT && rVar.getBType() == BVariableType.INT) {
            // int % int
            long result = Long.parseLong(lVar.computeValue()) % Long.parseLong(rVar.computeValue());
            return EvaluationUtils.make(context, result);
        } else if (lVar.getBType() == BVariableType.FLOAT && rVar.getBType() == BVariableType.FLOAT) {
            // float % float
            double result = Double.parseDouble(lVar.computeValue()) % Double.parseDouble(rVar.computeValue());
            return EvaluationUtils.make(context, result);
        } else if ((lVar.getBType() == BVariableType.INT && rVar.getBType() == BVariableType.FLOAT)
                || (lVar.getBType() == BVariableType.FLOAT && rVar.getBType() == BVariableType.INT)) {
            // int % float , float % int
            double result = Double.parseDouble(lVar.computeValue()) % Double.parseDouble(rVar.computeValue());
            return EvaluationUtils.make(context, result);
        } else if (lVar.getBType() == BVariableType.DECIMAL || rVar.getBType() == BVariableType.DECIMAL) {
            // Todo - Add support for
            // decimal % decimal
            // decimal % int , int % decimal
            // decimal % float , float % decimal
            throw createUnsupportedOperationException(lVar, rVar, SyntaxKind.PERCENT_TOKEN);
        } else {
            throw createUnsupportedOperationException(lVar, rVar, SyntaxKind.PERCENT_TOKEN);
        }
    }

    private EvaluationException createUnsupportedOperationException(BVariable lVar, BVariable rVar,
                                                                    SyntaxKind operator) {
        String reason = String.format(EvaluationExceptionKind.UNSUPPORTED_OPERATION.getReason(), operator.stringValue(),
                lVar.getBType().getString(), rVar.getBType().getString());
        return new EvaluationException(
                String.format(EvaluationExceptionKind.UNSUPPORTED_EXPRESSION.getString(), reason));
    }
}
