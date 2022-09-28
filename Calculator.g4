grammar Calculator;

//parser
equation: expression relationOperator expression;

expression: multiplyingExpression ((PLUS | MINUS) multiplyingExpression)*;

multiplyingExpression: powExpression ((TIMES | DIV) powExpression)*;

powExpression: signedAtom (POW signedAtom)*;

signedAtom : PLUS atom | MINUS atom | function | atom ;

atom : scientific | variable | constant | LPAREN expression RPAREN ;

scientific : SCIENTIFIC_NUMBER ;

constant : PI | EULER | I ;

variable : VARIABLE ;

//function : functionName LPAREN expression (COMMA expression)* RPAREN ;

function : functionCase1 | functionCase2 | floor | dec;

functionCase1 : functionNameCase1 LPAREN expression RPAREN ;

functionCase2 : functionNameCase2 LPAREN expression (COMMA expression) RPAREN ;

floor: '[' expression ']' ;

dec: '{' expression '}' ;

functionNameCase1 : SIN | COS | TAN | ASIN | ACOS | ATAN | LOG | LN | SQRT | SGN | UNIT ;

functionNameCase2 : LOGB | RT | PULSE;

relationOperator : EQ | GT | LT ;

//lexer
COS : 'cos' ;

SIN : 'sin' ;

TAN : 'tan' ;

ACOS : 'acos' ;

ASIN : 'asin' ;

ATAN : 'atan' ;

LN : 'ln' ;

LOG : 'log' ;

LOGB : 'logb' ;

SQRT : 'sqrt' ;

RT : 'rt' ;

SGN: 'sgn' ;

UNIT: 'unit' ;

PULSE: 'pulse' ;

LPAREN : '(' ;

RPAREN : ')' ;

PLUS : '+' ;

MINUS : '-' ;

TIMES : '*' ;

DIV : '/' ;

GT : '>' ;

LT : '<' ;

EQ : '=' ;

COMMA : ',' ;

POINT : '.' ;

POW : '^' ;

PI : 'pi' ;

EULER : E2 ;

I : 'i' ;

VARIABLE : VALID_ID_START VALID_ID_CHAR* ;

fragment VALID_ID_START : ('a' .. 'z') | ('A' .. 'Z') | '_' ;

fragment VALID_ID_CHAR : VALID_ID_START | ('0' .. '9') ;

SCIENTIFIC_NUMBER : NUMBER ((E1 | E2) SIGN? NUMBER)? ;

fragment NUMBER : ('0' .. '9') + ('.' ('0' .. '9') +)? ;

fragment E1 : 'E' ;

fragment E2 : 'e' ;

fragment SIGN : ('+' | '-') ;

WS : [ \r\n\t] + -> skip ;