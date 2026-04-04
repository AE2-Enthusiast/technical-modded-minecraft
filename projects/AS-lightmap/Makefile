JAVAC = javac

run: Grapher.class SkyCollectionHelper.class
	java -cp '../minecraft1.12.jar:../log4j-api.jar:.' Grapher

Grapher.class: Grapher.java
	javac -cp '../minecraft1.12.jar:.' Grapher.java

SkyCollectionHelper.class: SkyCollectionHelper.java
	javac -cp '../minecraft1.12.jar:.' SkyCollectionHelper.java

PHONY: run
