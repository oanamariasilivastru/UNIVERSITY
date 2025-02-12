%{
#include <stdlib.h>
#include <string.h>
#include "BST.h"
#include "parser.tab.h" // Include fișierul generat de Bison

extern int currentLine;
extern struct TreeNode *ts;

// Definirea structurii FIP și a funcțiilor asociate
typedef struct {
    char atom[100];
    int codAtom;
    int codAtomTS;
} FIP;

typedef struct {
    char atom[100];
    int codAtomTS;
} TS;

int lenFIP = 0;
FIP fip[300];

// Funcții pentru gestionarea FIP
int isInFIP(char atom[], int codAtom, int codAtomTS) {
    for (int i = 0; i < lenFIP; i++) {
        if (strcmp(fip[i].atom, atom) == 0 && fip[i].codAtom == codAtom && fip[i].codAtomTS == codAtomTS) {
            return 1;
        }
    }
    return 0;
}

void addToFIP(char atom[], int codAtom, int codAtomTS) {
    if (!isInFIP(atom, codAtom, codAtomTS)) {
        strcpy(fip[lenFIP].atom, atom);
        fip[lenFIP].codAtom = codAtom;
        fip[lenFIP].codAtomTS = codAtomTS;
        lenFIP++;
    }
}

void printFIP(); // Declarare externă

extern YYSTYPE yylval;

// Declararea funcției printFIP pentru a fi apelată din parser.y
extern void printFIP();
%}

%option noyywrap

/* Definirea expresiilor regulate pentru diferite tipuri de tokenuri */
DIGIT           [0-9]
LETTER          [a-zA-Z_]
ID              {LETTER}({LETTER}|{DIGIT})*
CONST           {DIGIT}+

KEYWORD         "if"|"while"|"int"|"float"|"string"|"main"|"scrie"|"citeste"|"executa"|"sfcattimp"|"cattimp"
OPERATOR        "=="|"!="|"<="|">="|"<"|">"|"="|"+"|"-"|"*"|"/"|"%"
SEPARATOR       ";"|"{"|"}"|"("|")"|","|":"
SPECIAL         "<-"

%%


"<-"            { addToFIP("<-", ASSIGN_LEFT, -1); return ASSIGN_LEFT; }

"cattimp"      { addToFIP("cattimp", CATTIMP, -1); return CATTIMP; }
"sfcattimp"    { addToFIP("sfcattimp", SFCATTIMP, -1); return SFCATTIMP; }

{KEYWORD} {
    if (strcmp(yytext, "if") == 0) {
        addToFIP("if", IF, -1);
        return IF;
    }
    if (strcmp(yytext, "while") == 0) {
        addToFIP("while", WHILE, -1);
        return WHILE;
    }
    if (strcmp(yytext, "int") == 0) {
        addToFIP("int", INT, -1);
        return INT;
    }
    if (strcmp(yytext, "float") == 0) {
        addToFIP("float", FLOAT, -1);
        return FLOAT;
    }
    if (strcmp(yytext, "string") == 0) {
        addToFIP("string", STRING, -1);
        return STRING;
    }
    if (strcmp(yytext, "main") == 0) {
        addToFIP("main", MAIN, -1);
        return MAIN;
    }
    if (strcmp(yytext, "scrie") == 0) {
        addToFIP("scrie", WRITE, -1);
        return WRITE;
    }
    if (strcmp(yytext, "citeste") == 0) {
        addToFIP("citeste", READ, -1);
        return READ;
    }
    if (strcmp(yytext, "executa") == 0) {
        addToFIP("executa", EXECUTA, -1);
        return EXECUTA;
    }
    /* Adaugă mai multe keyword-uri dacă este necesar */
}

{ID} {
    if (yyleng > 8) {
        printf("Error on line %d: Identifier length exceeds 8 characters: %s\n", currentLine, yytext);
        exit(1);
    }

    int index = getIndexRecursive(ts, yytext);
    if (index == -1) {
        ts = insertRecursive(ts, yytext);
        index = getIndexRecursive(ts, yytext);
    }
    addToFIP(yytext, ID, index);
    yylval.intval = index; /* Setează valoarea tokenului pentru parser */
    return ID;
}

{CONST} {
    int index = getIndexRecursive(ts, yytext);
    if (index == -1) {
        ts = insertRecursive(ts, yytext);
        index = getIndexRecursive(ts, yytext);
    }
    addToFIP(yytext, CONST, index);
    yylval.intval = index; /* Setează valoarea tokenului pentru parser */
    return CONST;
}

{OPERATOR} {
    if (strcmp(yytext, "+") == 0) {
        addToFIP("+", PLUS, -1);
        return PLUS;
    }
    if (strcmp(yytext, "-") == 0) {
        addToFIP("-", MINUS, -1);
        return MINUS;
    }
    if (strcmp(yytext, "*") == 0) {
        addToFIP("*", MULTIPLY, -1);
        return MULTIPLY;
    }
    if (strcmp(yytext, "/") == 0) {
        addToFIP("/", DIVIDE, -1);
        return DIVIDE;
    }
    if (strcmp(yytext, "%") == 0) {
        addToFIP("%", MODULO, -1);
        return MODULO;
    }
    if (strcmp(yytext, ">") == 0) {
        addToFIP(">", GREATER_THAN, -1);
        return GREATER_THAN;
    }
    if (strcmp(yytext, "<") == 0) {
        addToFIP("<", LESS_THAN, -1);
        return LESS_THAN;
    }
    if (strcmp(yytext, "<=") == 0) {
        addToFIP("<=", LESS_THAN_OR_EQUAL, -1);
        return LESS_THAN_OR_EQUAL;
    }
    if (strcmp(yytext, ">=") == 0) {
        addToFIP(">=", GREATER_THAN_OR_EQUAL, -1);
        return GREATER_THAN_OR_EQUAL;
    }
    if (strcmp(yytext, "!=") == 0) {
        addToFIP("!=", NOT_EQUAL, -1);
        return NOT_EQUAL;
    }
    if (strcmp(yytext, "==") == 0) {
        addToFIP("==", EQUAL_EQUAL, -1);
        return EQUAL_EQUAL;
    }
    if (strcmp(yytext, "=") == 0) {
        addToFIP("=", EQUAL, -1);
        return EQUAL;
    }
}

{SEPARATOR} {
    if (strcmp(yytext, ";") == 0) {
        addToFIP(";", SEMICOLON, -1);
        return SEMICOLON;
    }
    if (strcmp(yytext, "{") == 0) {
        addToFIP("{", LEFT_BRACE, -1);
        return LEFT_BRACE;
    }
    if (strcmp(yytext, "}") == 0) {
        addToFIP("}", RIGHT_BRACE, -1);
        return RIGHT_BRACE;
    }
    if (strcmp(yytext, "(") == 0) {
        addToFIP("(", LEFT_PAREN, -1);
        return LEFT_PAREN;
    }
    if (strcmp(yytext, ")") == 0) {
        addToFIP(")", RIGHT_PAREN, -1);
        return RIGHT_PAREN;
    }
    if (strcmp(yytext, ",") == 0) {
        addToFIP(",", COMMA, -1);
        return COMMA;
    }
    if (strcmp(yytext, ":") == 0) {
        addToFIP(":", COLON, -1);
        return COLON;
    }
}


[\n]    { currentLine++; }


[ \t\r]+  { /* Nicio acțiune */ }


.       {
            printf("Error on line %d: Unrecognized character '%s'\n", currentLine, yytext);
            exit(1);
        }

%%

/* Implementarea funcției printFIP */
void printFIP() {
    printf("FORMA INTERNA A PROGRAMULUI:\n");
    for (int i = 0; i < lenFIP; i++) {
        if (fip[i].codAtomTS == -1)
            printf("%s  |  %d  |  -\n", fip[i].atom, fip[i].codAtom);
        else
            printf("%s  |  %d  |  %d\n", fip[i].atom, fip[i].codAtom, fip[i].codAtomTS);
    }
}
