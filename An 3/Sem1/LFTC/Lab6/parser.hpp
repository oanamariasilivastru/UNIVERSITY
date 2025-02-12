#ifndef YYTOKENTYPE
# define YYTOKENTYPE
   /* Put the tokens into the symbol table, so that GDB and other debuggers
      know about them.  */
   enum yytokentype {
     LID = 258,
     ID = 259,
     CONST = 260,
     INCLUDE = 261,
     USING = 262,
     NAMESPACE = 263,
     STD = 264,
     IOSTREAM = 265,
     MAIN = 266,
     INT = 267,
     CIN = 268,
     COUT = 269,
     RETURN = 270,
     LEFT_BRACE = 271,
     RIGHT_BRACE = 272,
     LEFT_PAREN = 273,
     RIGHT_PAREN = 274,
     COMMA = 275,
     SEMICOLON = 276,
     LESS_THAN = 277,
     GREATER_THAN = 278,
     EQUAL = 279,
     PLUS = 280,
     MINUS = 281,
     MULTIPLY = 282,
     DIVIDE = 283,
     LEFT_SHIFT = 284,
     RIGHT_SHIFT = 285,
     HASH = 286,
     INCREMENT = 287,
     DECREMENT = 288
   };
#endif



#if ! defined YYSTYPE && ! defined YYSTYPE_IS_DECLARED
typedef union YYSTYPE
{

/* Line 1676 of yacc.c  */
#line 34 ".\\parser.y"

	char* value;



/* Line 1676 of yacc.c  */
#line 91 "parser.hpp"
} YYSTYPE;
# define YYSTYPE_IS_TRIVIAL 1
# define yystype YYSTYPE /* obsolescent; will be withdrawn */
# define YYSTYPE_IS_DECLARED 1
#endif

extern YYSTYPE yylval;