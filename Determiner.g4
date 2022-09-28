grammar Determiner;

//parser
start: (expression SPACE? NEWLINE?)* ;
expression: num | '(' num ')' | VARIABLE | WRONGVARIABLE;
num: ('+'|'-')?REAL;

//lexer
NEWLINE: [\r\n]+;
SPACE: ' ';
REAL: [0-9]+[.]?[0-9]* ;
VARIABLE: [a-zA-Z]('_')?([a-zA-Z0-9]('_')?)* | '_'([a-zA-Z0-9]('_')?)+;
WRONGVARIABLE: [0-9]+VARIABLE;
WS: [ \t\r\n]+ -> skip ;