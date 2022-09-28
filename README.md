# ContextFreeGrammarsImplementationWithSchematic
Implementation of fully-functioned CFG & PDAs, also include schematically implementation

**"Theory of Languages & Automata"** course project, IUST, Dr. Hossein Rahmani, Spring 2019

We've implemented a complete and comprehensive system to work with context-free grammars (CFG) & push-down automata (PDA). This project meets your needs in the following areas:
1. Convert a grammar to the simplest form by removing **Useless, Lambda & Unit productions** with usage of 'DeleteTrash' function
2. Check if the input grammar is not in Chomskey Normal Form (CNF) and convert it with usage of `ChangeToChomskyForm` function
3. Check if the input grammar is not in Greibach Normal Form (GNF) and convert it with usage of `ChangeToGreibachForm` function
4. Check the acceptance of a string with a grammar using the **CYK algorithm** with usage of 'IsGenerateByGrammar' function
5. Write a grammar file using `Antlr` for calculator and usign PDA to calculate the final result after checking acceptance of an input string
6. Show schematically image of the input PDA

Input format:
```
5
<START> -> <SUBJECT><DISTANCE><VERB><DISTANCE><OBJECTS>
<SUBJECT> -> i|we
<DISTANCE> ->
<VERB> -> eat|drink |go
<OBJECTS> - > food|water|lamda
```

```
4
<S> -> <S>a | <S>b | <A>a | <B>b
<A> -> ab<A> | <B>ca | lambda
<B> -> b<B> | <C>f
<C> -> a<C> | lambda
```

**Notes:**
* The project can be run in both terminal or GUI version by choosing the proper option at the first.
* We use `lambda` as the notation used for lambda transition.
* You need to input your PDA transitions into `PDA.txt` file in `Project2/src/` directory & run `PDA.java` to test 5th & 6th functionalties upper mentioned.

![image](https://s6.uupload.ir/files/q1_2qeu.png)
![image](https://s6.uupload.ir/files/q2_9cy.png)

Project contributors:

* Danial Bazmandeh, BSc, Computer Engineering
* Alireza Haghani, BSc, Computer Engineering
