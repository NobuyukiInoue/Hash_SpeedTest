JAVAC =javac
JAVAC_FLAGS =

all:	Out.class ExInteger.class TimeFormatter.class ComputeHash.class SearchClearText.class MySolution.class Main.class

Out.class:	Out.java
	$(JAVAC) $(JAVAC_FLAGS) $<

ExInteger.class:	ExInteger.java
	$(JAVAC) $(JAVAC_FLAGS) $<

TimeFormatter.class:	TimeFormatter.java
	$(JAVAC) $(JAVAC_FLAGS) $<

ComputeHash.class:	ComputeHash.java
	$(JAVAC) $(JAVAC_FLAGS) $<

SearchClearText.class:	SearchClearText.java
	$(JAVAC) $(JAVAC_FLAGS) $<

MySolution.class:	MySolution.java
	$(JAVAC) $(JAVAC_FLAGS) $<

Main.class:	Main.java
	$(JAVAC) $(JAVAC_FLAGS) $<

clean:
	rm ./*.class

.PHONY:	all clean
