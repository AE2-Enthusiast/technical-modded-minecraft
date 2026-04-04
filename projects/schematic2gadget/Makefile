JAVAC = javac

run: Schematic2Gadget.class
	@java -cp '../minecraft1.12.jar:../log4j-api.jar:../guava.jar:.' Schematic2Gadget ${ARGS}

Schematic2Gadget.class: Schematic2Gadget.java
	@javac -cp '../minecraft1.12.jar:.' Schematic2Gadget.java

PHONY: run
.SILENT:
