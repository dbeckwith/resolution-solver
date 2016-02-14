# Resolution Solver
Tests if a logical expression can be proven true by a set of known facts.

## About
This program takes in a set of propositional logic sentences. The first set of sentences are known as the "knowledge base" and represent what true facts are known in the given contents. The last sentence is then the conclusion that is tested on the knowledge base. Whether or not the conclusion can be shown to be true based on the knowledge base is determined using a proof by contradiction and resolution.

If the knowledge base is represented as a single statement consisting of a conjuction of facts (`KB`) and the conclusion is a statement (`a`), then `KB` entails `a` if and only if the statement "`KB` and not `a`" can be shown to be unsatisfiable (always false). This is done by first reducing this statement to [conjuctive normal form](https://en.wikipedia.org/wiki/Conjunctive_normal_form) (CNF), then repeatedly applying the [resolution rule](https://en.wikipedia.org/wiki/Resolution_(logic)) until either:
* two statements "`A`" and "not `A`" are resolved, producing a contradiction and showing the original statement is unsatisfiable
* no more statements can be resolved, showing the original statement is satisfiable

If the statement is shown to be unsatisfiable, then the conclusion (`a`) is shown to be provable given the knowledge base (`KB`).

## Building
Building this project requires [Maven 3](https://maven.apache.org/download.cgi) and [JDK 1.8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html). To build a standalone executable JAR file, run the following command from the project root directory:
```console
mvn package
```
This will generate a JAR file at `./target/resolution.jar`

## Execution
Running this program requires [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/index.html). To run the JAR file, execute the following command:
```console
java -jar resolution.jar
```
This will cause the program to show its usage and help message.
