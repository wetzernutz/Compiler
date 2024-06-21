%{
    /* definitions of manifest constants */
    LT, LE, EQ, NE, GT, GE,
    IF, THEN, ELSE, WHILE, ID, NUMBER, RELOP,
    STRING
%}

/* regular definitions */
delim       [ \t\n]
ws          {delim}+
letter_     [A-Za-z_]
digit       [0-9]
digits      {digit}+
id          {letter_}{letter_|digit}*
number      {digit}+(\.{digits})?(E[+-]?{digits})?

/* regular definitions for strings
" and \ are lex special characters, so \" means just one " in the input */
dq          [\"]
esc         [\\]
notDq       [^{dq}]mo
escDq       {esc}{dq}
str         {dq}{escDq|notDq}*{dq}

%%

{ws}        {/* no action and no return*/}
if          {return(IF);}
then        {return(THEN);}
else        {return(ELSE);}
while       {return(WHILE);}
{id}        {yylval = (int) installID(); return(ID);}
{number}    {yylval = (int) installNum(); return(NUMBER);}
{str}       {yylval = (int) installStr(); return(STRING);}
"=="        {yylval = EQ; return(RELOP);}
"!="        {yylval = NE; return(RELOP);}
">"         {yylval = GT; return(RELOP);}
"<"         {yylval = LT; return(RELOP);}
"<="        {yylval = LE; return(RELOP);}
">="        {yylval = GE; return(RELOP);}

%%

int installID() {
/* function to install the lexeme, whose first character is pointed to by yytext,
and whose length is yyleng, into the symbol table and return a pointer there to */
}

int installNum() {
/* similar to installID, but puts numerical constants into a separate table */
}

int installStr() {
/* similar to installID, but puts string constants into a separate table */
}