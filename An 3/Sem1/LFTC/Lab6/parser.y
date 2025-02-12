%{
    #include <iostream>
    #include <fstream>
    #include <string>
    #include <vector>
    #include <queue>
    #include <stack>
    #include <set>
    #include <cmath>
    #include <string.h>

    // Declarăm funcțiile și variabilele externe
    extern int yylex();
    extern int yyparse();
    extern FILE* yyin;
    extern int yylineno;

    // Funcții utilizate în cod
    void yyerror(const char* s);
    void addDeclaration(std::string s, std::string sValue = "0", std::string type = " dd ");
    void printDeclarations();
    void printCode();

    // Liste/structuri globale
    std::vector<std::string> imports;
    std::vector<std::string> code;
    std::vector<std::string> tokens;
    std::set<std::string> declarations;
    std::string outputFileName = "outputCode.asm";
    std::ofstream outputFile;

    bool isOperator(std::string c);
    bool isNumber(const std::string& s);
    void evaluateRPN();
%}

/* 
 * Definim un %union pentru a lega tipul de date (char*).
 * Apoi folosim %type <value> pentru neterminale.
 */
%union {
    char* value;
}

/* Pentru fiecare neterminal și token care va conține date (char*) */
%type <value> program corp instr_corp instr lista_decl_instr decl_instr
%type <value> atribuire atribuire_valoare expr_aritmetica term factor
%type <value> instr_intrare instr_iesire instr_return

%token <value> ID 
%token <value> CONST 
%token <value> INCLUDE 
%token <value> USING 
%token <value> NAMESPACE 
%token <value> STD
%token <value> IOSTREAM MAIN
%token <value> INT 
%token <value> CIN COUT RETURN
%token <value> LEFT_BRACE RIGHT_BRACE LEFT_PAREN RIGHT_PAREN
%token <value> COMMA SEMICOLON 
%token <value> LESS_THAN GREATER_THAN EQUAL
%token <value> PLUS MINUS MULTIPLY DIVIDE
%token <value> LEFT_SHIFT RIGHT_SHIFT HASH

%start program

%%

program:
    /* #include <iostream> */
      HASH INCLUDE LESS_THAN IOSTREAM GREATER_THAN
    /* using namespace std; */
    USING NAMESPACE STD SEMICOLON
    /* int main() */
    INT MAIN LEFT_PAREN RIGHT_PAREN
    corp
    {
        // eventuale inițializări
    }
;

corp:
    LEFT_BRACE instr_corp RIGHT_BRACE
;

instr_corp:
      instr instr_corp
    | instr
;

instr:
      lista_decl_instr
    | atribuire SEMICOLON
    | instr_return
    | instr_intrare
    | instr_iesire
;

/* Declarații de variabile */
lista_decl_instr:
    INT decl_instr SEMICOLON
;

decl_instr:
      ID COMMA decl_instr
        {
            addDeclaration($1);
        }
    | ID atribuire_valoare COMMA decl_instr
        {
            addDeclaration($1, $2);
            code.push_back("pop dword [" + std::string($1) + "]");
        }
    | ID
        {
            addDeclaration($1);
        }
    | ID atribuire_valoare
        {
            addDeclaration($1, $2);
            code.push_back("pop dword [" + std::string($1) + "]");
        }
;

/* Atribuire */
atribuire:
    ID atribuire_valoare
    {
        code.push_back("pop dword [" + std::string($1) + "]\n");
    }
;

atribuire_valoare:
    EQUAL expr_aritmetica
    {
        /* După ce parsează expr_aritmetica, generăm RPN și apoi clear la tokens */
        $<value>$ = strdup($2);
        evaluateRPN();   // generează instrucțiuni ASM pentru RPN
        tokens.clear();
    }
;

/* Expresii aritmetice cu paranteze */
expr_aritmetica:
      term PLUS expr_aritmetica
        {
            tokens.push_back("+");
        }
    | term MINUS expr_aritmetica
        {
            tokens.push_back("-");
        }
    | term
;

term:
      factor MULTIPLY term
        {
            tokens.push_back("*");
        }
    | factor DIVIDE term
        {
            tokens.push_back("/");
        }
    | factor
;

factor:
      LEFT_PAREN expr_aritmetica RIGHT_PAREN
    | ID
      {
          tokens.push_back("[" + std::string($1) + "]");
      }
    | CONST
      {
          tokens.push_back($1);
      }
;

/* Instrucțiuni intrare/ieșire */
instr_intrare:
    CIN RIGHT_SHIFT ID SEMICOLON
    {
        /* Se apelează scanf cu format, deci e nevoie de format în .data */
        code.push_back(
            "push dword " + std::string($3) + "\n"
            "\tpush dword format\n"
            "\tcall scanf\n"
            "\tadd esp, 8\n"
        );
    }
;

instr_iesire:
    COUT LEFT_SHIFT ID SEMICOLON
    {
        /* Afișăm conținutul variabilei [ID] cu printf */
        code.push_back(
            "push dword [" + std::string($3) + "]\n"
            "\tpush dword format\n"
            "\tcall printf\n"
            "\tadd esp, 8\n"
        );
    }
;

instr_return:
    RETURN CONST SEMICOLON
;

%%

/* Funcție de eroare */
void yyerror(const char* s)
{
    std::cerr << "Syntax error at line: " << yylineno << "\n";
    exit(1);
}

/* Funcția main */
int main(){
    FILE *fp;
    char filename[128];

    std::cout << "Enter file name: ";
    scanf("%s", filename);
    fp = fopen(filename, "r");
    yyin = fp;

    yyparse();
    std::cout << "The program is syntax correct\n";

    // Deschidem fișierul ASM
    outputFile.open(outputFileName);

    // Scriem antetul ASM
    outputFile << "bits 32\n"
               << "global start\n\n";

    // Extern
    outputFile << "extern scanf\n"
               << "extern printf\n"
               << "extern exit\n\n";

    // secțiunea .data
    outputFile << "section .data\n";

    // IMPORTANT: adăugăm simbolul `format`, dacă nu e deja declarat
    // astfel încât `push dword format` să funcționeze
    addDeclaration("format", "\"%d\", 0", " db ");

    // Afișăm declarațiile
    printDeclarations();

    // secțiunea .text + cod
    outputFile << "\nsection .text\nstart:\n";
    printCode();

    // exit(0)
    outputFile << "\tpush dword 0\n"
               << "\tcall exit\n";

    outputFile.close();
    return 0;
}

/* Funcție de adăugare declarații */
void addDeclaration(std::string s, std::string sValue, std::string type)
{
    // Verificăm dacă e deja declarat
    for(const auto &decl : declarations) {
        if(decl.find(s + type) != std::string::npos)
            return;
    }
    declarations.insert(s + type + sValue);
}

/* Afișare declarații */
void printDeclarations()
{
    for(const auto &decl : declarations) {
        outputFile << "\t" << decl << "\n";
    }
}

/* Afișare instrucțiuni ASM */
void printCode()
{
    for(const auto &codeLine : code) {
        outputFile << "\t" << codeLine << "\n";
    }
}

/* Verifică dacă e operator (+, -, *, /) */
bool isOperator(std::string c) {
    return (c == "+" || c == "-" || c == "*" || c == "/");
}

/* Verifică dacă e numeric */
bool isNumber(const std::string& s)
{
    for(char ch : s) {
        if(!isdigit(ch) && ch != '-')
            return false;
    }
    return !s.empty();
}

/* Construiește RPN -> ASM */
void evaluateRPN()
{
    std::stack<double> valueStack;

    for(const auto &token : tokens) {
        if(isOperator(token)) {
            double b = valueStack.top(); valueStack.pop();
            double a = valueStack.top(); valueStack.pop();

            if(token == "+") {
                valueStack.push(a + b);
                code.push_back("pop edx");
                code.push_back("pop eax");
                code.push_back("add eax, edx");
                code.push_back("push eax");
                code.push_back("");
            }
            else if(token == "-") {
                valueStack.push(a - b);
                code.push_back("pop edx");
                code.push_back("pop eax");
                code.push_back("sub eax, edx");
                code.push_back("push eax");
                code.push_back("");
            }
            else if(token == "*") {
                valueStack.push(a * b);
                code.push_back("pop edx");
                code.push_back("pop eax");
                code.push_back("imul eax, edx");
                code.push_back("push eax");
                code.push_back("");
            }
            else if(token == "/") {
                valueStack.push(a / b);
                code.push_back("pop ebx");
                code.push_back("pop eax");
                code.push_back("xor edx, edx");
                code.push_back("div ebx");
                code.push_back("push eax");
                code.push_back("");
            }
        }
        else if(isNumber(token)) {
            // e un literal numeric
            valueStack.push(std::stod(token));
            code.push_back("push " + token);
        }
        else {
            // e un ID -> [x]
            code.push_back("push dword " + token);
            valueStack.push(0); 
        }
    }
}
