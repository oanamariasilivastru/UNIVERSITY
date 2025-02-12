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

#ifndef YY_YY_YACC_TAB_H_INCLUDED
# define YY_YY_YACC_TAB_H_INCLUDED
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
    MAIN = 258,                    /* MAIN  */
    LPAREN = 259,                  /* LPAREN  */
    RPAREN = 260,                  /* RPAREN  */
    LBRACE = 261,                  /* LBRACE  */
    RBRACE = 262,                  /* RBRACE  */
    SEMICOLON = 263,               /* SEMICOLON  */
    COMMA = 264,                   /* COMMA  */
    INT = 265,                     /* INT  */
    CHAR = 266,                    /* CHAR  */
    FLOAT = 267,                   /* FLOAT  */
    DOUBLE = 268,                  /* DOUBLE  */
    STRING = 269,                  /* STRING  */
    ASSIGN = 270,                  /* ASSIGN  */
    PLUS = 271,                    /* PLUS  */
    MINUS = 272,                   /* MINUS  */
    MUL = 273,                     /* MUL  */
    DIV = 274,                     /* DIV  */
    MOD = 275,                     /* MOD  */
    BIT_AND = 276,                 /* BIT_AND  */
    BIT_OR = 277,                  /* BIT_OR  */
    BIT_XOR = 278,                 /* BIT_XOR  */
    LSHIFT = 279,                  /* LSHIFT  */
    RSHIFT = 280,                  /* RSHIFT  */
    NOT = 281,                     /* NOT  */
    AND = 282,                     /* AND  */
    OR = 283,                      /* OR  */
    EQ = 284,                      /* EQ  */
    NEQ = 285,                     /* NEQ  */
    LT = 286,                      /* LT  */
    GT = 287,                      /* GT  */
    LTE = 288,                     /* LTE  */
    GTE = 289,                     /* GTE  */
    IF = 290,                      /* IF  */
    ELSE = 291,                    /* ELSE  */
    WHILE = 292,                   /* WHILE  */
    FOR = 293,                     /* FOR  */
    STRUCT = 294,                  /* STRUCT  */
    DOT = 295,                     /* DOT  */
    CIN = 296,                     /* CIN  */
    COUT = 297,                    /* COUT  */
    SWITCH = 298,                  /* SWITCH  */
    CASE = 299,                    /* CASE  */
    DEFAULT = 300,                 /* DEFAULT  */
    BREAK = 301,                   /* BREAK  */
    COLON = 302,                   /* COLON  */
    CONST = 303,                   /* CONST  */
    IDENTIFIER = 304,              /* IDENTIFIER  */
    COMMENT = 305,                 /* COMMENT  */
    UNKNOWN = 306                  /* UNKNOWN  */
  };
  typedef enum yytokentype yytoken_kind_t;
#endif

/* Value type.  */
#if ! defined YYSTYPE && ! defined YYSTYPE_IS_DECLARED
typedef int YYSTYPE;
# define YYSTYPE_IS_TRIVIAL 1
# define YYSTYPE_IS_DECLARED 1
#endif


extern YYSTYPE yylval;


int yyparse (void);


#endif /* !YY_YY_YACC_TAB_H_INCLUDED  */
