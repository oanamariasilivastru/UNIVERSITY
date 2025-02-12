#ifndef __PARSER_H__
#define __PARSER_H__

/*
Define tokens in the following index order:
"ID", "CONST", "(", ")", ",", "int", "float", "string", "{", "}", ";", "<-", "scrie", "citeste", "+", "-", "*", "/", "%", 
"while", "!=", "==", "<", ">", "<=", ">=", "if", ":", "\"", "main", "="
*/

#define ID 0
#define CONST 1
#define LEFT_PAREN 2
#define RIGHT_PAREN 3
#define COMMA 4
#define INT 5
#define FLOAT 6
#define STRING 7
#define LEFT_BRACE 8
#define RIGHT_BRACE 9
#define SEMICOLON 10
#define ASSIGN_LEFT 11
#define WRITE 12
#define READ 13
#define PLUS 14
#define MINUS 15
#define MULTIPLY 16
#define DIVIDE 17
#define MODULO 18
#define WHILE 19
#define NOT_EQUAL 20
#define EQUAL_EQUAL 21
#define LESS_THAN 22
#define GREATER_THAN 23
#define LESS_THAN_OR_EQUAL 24
#define GREATER_THAN_OR_EQUAL 25
#define IF 26
#define COLON 27
#define QUOTE 28
#define MAIN 29
#define EQUAL 30
#define CATTIMP 31
#define EXECUTA 32
#define SFCATTIMP 33
#endif // __PARSER_H__
