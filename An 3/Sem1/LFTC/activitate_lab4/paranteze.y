%{
#include <stdio.h>
void yyerror(const char *s);
int yylex(void);
%}

%start input
%token '\n'

%%

input:
      /* empty */
    | input line
    ;

line:
      expr '\n'   { printf("Parantezele sunt inchise corect.\n"); }
    ;

expr:
      /* empty */
    | '(' expr ')' expr  
    ;

%%

void yyerror(const char *s)
{
    fprintf(stderr, "%s\n", s);
}

int main()
{
    if (yyparse() == 0)
        return 0;
    else
    {
        printf("Parantezele nu sunt inchise corect.\n");
        return 1;
    }
}
