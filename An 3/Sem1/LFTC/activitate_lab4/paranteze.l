%{
#include "paranteze.tab.h"
%}

%%

"("             { return '('; }
")"             { return ')'; }
[^\(\)\n]+      { }
[\n]            { return '\n'; }

%%

int yywrap() { return 1; }
