%{
#include <stdio.h>

FILE *fic;

%}
blank   [ \t\n]
ws      {blank}+

%%

{ws}    {fprintf(fic, "%s", " ");}
.       {fprintf(fic, "%s", yytext);}

%%

int main() {
    fic = fopen("output.txt","w");
    if (!fic) {
        perror("Error opening file");
        return 1;
    }
    yylex();
    return 0;
}
