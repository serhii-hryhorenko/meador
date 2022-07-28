package com.teamdev.meador.compiler;

public enum StatementType {
    PROGRAM,

    CODE_BLOCK,

    VARIABLE_DECLARATION,
    PROCEDURE,
    SWITCH,

    EXPRESSION,
    NUMERIC_EXPRESSION,
    RELATIONAL_EXPRESSION,
    BOOLEAN_EXPRESSION,

    NUMERIC_OPERAND,
    BOOLEAN_OPERAND,
    NUMBER,
    NUMERIC_BRACKETS,
    BOOLEAN_BRACKETS,
    FUNCTION,
    VARIABLE_VALUE,
    BOOLEAN_LITERAL, STRING_LITERAL, STRING_EXPRESSION,
}
