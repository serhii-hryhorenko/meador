package com.teamdev.meador.compiler;

public enum ProgramElement {
    LIST_OF_STATEMENTS,

    CODE_BLOCK,

    CONDITIONAL_OPERATOR,
    FOR_LOOP,
    SWITCH_OPERATOR,
    PROCEDURE,
    STRUCTURE_FIELD_ASSIGNMENT,
    VARIABLE_ASSIGNMENT,

    EXPRESSION,

    NUMERIC_OPERAND,
    NUMERIC_BRACKETS,
    NUMBER,
    FUNCTION,
    NUMERIC_EXPRESSION,

    BOOLEAN_EXPRESSION,
    BOOLEAN_OPERAND,
    BOOLEAN_LITERAL,
    BOOLEAN_BRACKETS,
    RELATIONAL_EXPRESSION,

    STRING_EXPRESSION,
    STRING_LITERAL,

    READ_VARIABLE,
    UNARY_PREFIX_EXPRESSION,
    UNARY_POSTFIX_EXPRESSION,
    VARIABLE_VALUE,
    STRUCTURE_FIELD_VALUE, DATA_STRUCTURE_DECLARATION, DATA_STRUCTURE_INSTANCE,
}
