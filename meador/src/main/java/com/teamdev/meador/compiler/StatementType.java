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

    DATA_STRUCTURE_DECLARATION,
    DATA_STRUCTURE_INSTANCE,
    FIELD_ASSIGNMENT,

    OPERAND,
    NUMBER,
    BRACKETS,
    FUNCTION,
    MEMORY_VALUE,
}
