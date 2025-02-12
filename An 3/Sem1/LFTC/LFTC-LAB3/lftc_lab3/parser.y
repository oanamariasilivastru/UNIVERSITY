%{
#include <stdio.h>
#include <stdlib.h>
#include "BST.h"


extern int yylex(void);
extern FILE *yyin;


int currentLine = 1;
struct TreeNode *ts = NULL;


void yyerror(const char *s);
void printFIP();
%}

%union {
    int intval;
    char *strval;
}

%token ID CONST
%token LEFT_PAREN RIGHT_PAREN COMMA
%token INT FLOAT STRING
%token LEFT_BRACE RIGHT_BRACE SEMICOLON
%token ASSIGN_LEFT WRITE READ
%token PLUS MINUS MULTIPLY DIVIDE MODULO
%token WHILE NOT_EQUAL EQUAL_EQUAL LESS_THAN GREATER_THAN
%token LESS_THAN_OR_EQUAL GREATER_THAN_OR_EQUAL IF COLON QUOTE MAIN EQUAL
%token CATTIMP EXECUTA SFCATTIMP

/* Specificarea priorității și asociativității operatorilor */
%left COLON
%left EQUAL_EQUAL NOT_EQUAL LESS_THAN GREATER_THAN LESS_THAN_OR_EQUAL GREATER_THAN_OR_EQUAL
%left PLUS MINUS
%left MULTIPLY DIVIDE MODULO
%right ASSIGN_LEFT
%nonassoc IF

%type <strval> ID CONST

%start program

%%


program
    : main_function
    ;

main_function
    : type MAIN LEFT_PAREN RIGHT_PAREN LEFT_BRACE statements RIGHT_BRACE
    ;

statements
    : /* empty */
    | statements statement
    ;

statement
    : declaration
    | assignment
    | write_statement
    | read_statement
    | if_statement
    | while_statement
    | execute_statement
    | CATTIMP SEMICOLON
    | SFCATTIMP SEMICOLON
    ;

declaration
    : type ID SEMICOLON
    | type ID ASSIGN_LEFT expression SEMICOLON
    ;

type
    : INT
    | FLOAT
    | STRING
    ;

assignment
    : ID ASSIGN_LEFT expression SEMICOLON
    ;

write_statement
    : WRITE LEFT_PAREN expression RIGHT_PAREN SEMICOLON
    ;

read_statement
    : READ LEFT_PAREN ID RIGHT_PAREN SEMICOLON
    ;

if_statement
    : IF LEFT_PAREN expression RIGHT_PAREN LEFT_BRACE statements RIGHT_BRACE
    ;

while_statement
    : WHILE LEFT_PAREN expression RIGHT_PAREN LEFT_BRACE statements RIGHT_BRACE
    ;

execute_statement
    : EXECUTA LEFT_PAREN RIGHT_PAREN SEMICOLON
    ;

expression
    : expression PLUS expression
    | expression MINUS expression
    | expression MULTIPLY expression
    | expression DIVIDE expression
    | expression MODULO expression
    | expression EQUAL_EQUAL expression
    | expression NOT_EQUAL expression
    | expression LESS_THAN expression
    | expression GREATER_THAN expression
    | expression LESS_THAN_OR_EQUAL expression
    | expression GREATER_THAN_OR_EQUAL expression
    | LEFT_PAREN expression RIGHT_PAREN
    | ID
    | CONST
    ;

%%

void yyerror(const char *s) {
    fprintf(stderr, "Syntax error on line %d: %s\n", currentLine, s);
    exit(1);
}

int main(int argc, char **argv) {
    if (argc < 2) {
        printf("Usage: %s <input file>\n", argv[0]);
        return 1;
    }

    FILE* file = fopen(argv[1], "r");
    if (!file) {
        printf("Error: Cannot open file %s\n", argv[1]);
        return 1;
    }

    yyin = file;

    if (yyparse() == 0) {
        printf("Parsing completed successfully.\n");
    }

    // Afișarea tabelei de simboluri și a FIP-ului
    printf("\nTABELA DE SIMBOLURI:\n");
    inorderRecursivePrint(ts);

    printf("\nFORMA INTERNA A PROGRAMULUI:\n");
    printFIP();

    fclose(file);
    return 0;
}
