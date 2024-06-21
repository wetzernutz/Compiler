%{
    SELECT, FROM, WHERE,
    ID
%}

select  [Ss][Ee][Ll][Ee][Cc][Tt]
from    [Ff][Rr][Oo][Mm]
where   [Ww][Hh][Ee][Rr][Ee]

letter  [A-Za-z]
digit   [0-9]
id      {letter|digit}+

%%

/* select rule is preferred over id match since it comes first */
{select}    {return(SELECT);}
{from}      {return(FROM);}
{where}     {return(WHERE);}
{id}        {yylval = (int) install_ID(); return(ID);}

.           {printf( "Unrecognized character: %s\n", yytext );}

%%
install_ID() {
    /* function to install the lexeme
        which is also converted in to capital case or lower case here,
        whose first character is pointed to by yytext,
        and whose length is yyleng, into the symbol table and return a pointer there to
    */

    /* We've to convert the lexeme to lowercase and install it

    example:
        yytext: foO
        yytext = lowercase(yytext)
        yylval = (int) installID()

        yytext: Strstr
        yytext = lowercase(yytext)
        yylval = (int) installID()

        yytext: strstr (already exists in the symbol table)
        yytext = lowercase(yytext)
        yylval = (int) installID()

    */
}
