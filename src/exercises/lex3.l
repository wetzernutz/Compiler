/*
Exercise 3.5.3:
Write a Lex program that copies a C program, replacing each
instance of the keyword float by double.
*/

%{
    #include <stdio.h>

    FILE *fic;
%}

%%

float   {fprintf(fic, "%s", "double");}
.       {fprintf(fic, "%s", yytext);}
[\n]    {fprintf(fic, "%s", yytext);}

%%

int main() {
    fic = fopen("output.txt", "w");
    if (!fic) {
        perror("Error opening file");
        return 1;
    }
    yylex();
    return 0;
}