/* A Bison parser, made by GNU Bison 3.8.2.  */

/* Bison interface for Yacc-like parsers in C

   Copyright (C) 1984, 1989-1990, 2000-2015, 2018-2021 Free Software Foundation,
   Inc.

   This program is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program.  If not, see <https://www.gnu.org/licenses/>.  */

/* As a special exception, you may create a larger work that contains
   part or all of the Bison parser skeleton and distribute that work
   under terms of your choice, so long as that work isn't itself a
   parser generator using the skeleton or a modified version thereof
   as a parser skeleton.  Alternatively, if you modify or redistribute
   the parser skeleton itself, you may (at your option) remove this
   special exception, which will cause the skeleton and the resulting
   Bison output files to be licensed under the GNU General Public
   License without this special exception.

   This special exception was added by the Free Software Foundation in
   version 2.2 of Bison.  */

/* DO NOT RELY ON FEATURES THAT ARE NOT DOCUMENTED in the manual,
   especially those whose name start with YY_ or yy_.  They are
   private implementation details that can be changed or removed.  */

#ifndef YY_YY_PARSER_TAB_H_INCLUDED
# define YY_YY_PARSER_TAB_H_INCLUDED
/* Debug traces.  */
#ifndef YYDEBUG
# define YYDEBUG 0
#endif
#if YYDEBUG
extern int yydebug;
#endif

/* Token kinds.  */
#ifndef YYTOKENTYPE
# define YYTOKENTYPE
  enum yytokentype
  {
    YYEMPTY = -2,
    YYEOF = 0,                     /* "end of file"  */
    YYerror = 256,                 /* error  */
    YYUNDEF = 257,                 /* "invalid token"  */
    ID = 258,                      /* ID  */
    CONST = 259,                   /* CONST  */
    LEFT_PAREN = 260,              /* LEFT_PAREN  */
    RIGHT_PAREN = 261,             /* RIGHT_PAREN  */
    COMMA = 262,                   /* COMMA  */
    INT = 263,                     /* INT  */
    FLOAT = 264,                   /* FLOAT  */
    STRING = 265,                  /* STRING  */
    LEFT_BRACE = 266,              /* LEFT_BRACE  */
    RIGHT_BRACE = 267,             /* RIGHT_BRACE  */
    SEMICOLON = 268,               /* SEMICOLON  */
    ASSIGN_LEFT = 269,             /* ASSIGN_LEFT  */
    WRITE = 270,                   /* WRITE  */
    READ = 271,                    /* READ  */
    PLUS = 272,                    /* PLUS  */
    MINUS = 273,                   /* MINUS  */
    MULTIPLY = 274,                /* MULTIPLY  */
    DIVIDE = 275,                  /* DIVIDE  */
    MODULO = 276,                  /* MODULO  */
    WHILE = 277,                   /* WHILE  */
    NOT_EQUAL = 278,               /* NOT_EQUAL  */
    EQUAL_EQUAL = 279,             /* EQUAL_EQUAL  */
    LESS_THAN = 280,               /* LESS_THAN  */
    GREATER_THAN = 281,            /* GREATER_THAN  */
    LESS_THAN_OR_EQUAL = 282,      /* LESS_THAN_OR_EQUAL  */
    GREATER_THAN_OR_EQUAL = 283,   /* GREATER_THAN_OR_EQUAL  */
    IF = 284,                      /* IF  */
    COLON = 285,                   /* COLON  */
    QUOTE = 286,                   /* QUOTE  */
    MAIN = 287,                    /* MAIN  */
    EQUAL = 288,                   /* EQUAL  */
    CATTIMP = 289,                 /* CATTIMP  */
    EXECUTA = 290,                 /* EXECUTA  */
    SFCATTIMP = 291                /* SFCATTIMP  */
  };
  typedef enum yytokentype yytoken_kind_t;
#endif

/* Value type.  */
#if ! defined YYSTYPE && ! defined YYSTYPE_IS_DECLARED
union YYSTYPE
{
#line 19 "parser.y"

    int intval;
    char *strval;

#line 105 "parser.tab.h"

};
typedef union YYSTYPE YYSTYPE;
# define YYSTYPE_IS_TRIVIAL 1
# define YYSTYPE_IS_DECLARED 1
#endif


extern YYSTYPE yylval;


int yyparse (void);


#endif /* !YY_YY_PARSER_TAB_H_INCLUDED  */
