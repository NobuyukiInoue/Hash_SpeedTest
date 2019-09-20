JAVAC =javac
JAVAC_FLAGS =

all:	Out.class ExInteger.class TimeSpan.class ComputeHash.class Search_ClearText.class MySolution.class Main.class

Out.class:	Out.java
	$(JAVAC) $(JAVAC_FLAGS) $<

ExInteger.class:	ExInteger.java
	$(JAVAC) $(JAVAC_FLAGS) $<

TimeSpan.class:	TimeSpan.java
	$(JAVAC) $(JAVAC_FLAGS) $<

ComputeHash.class:	ComputeHash.java
	$(JAVAC) $(JAVAC_FLAGS) $<

Search_ClearText.class:	Search_ClearText.java
	$(JAVAC) $(JAVAC_FLAGS) $<

MySolution.class:	MySolution.java
	$(JAVAC) $(JAVAC_FLAGS) $<

Main.class:	Main.java
	$(JAVAC) $(JAVAC_FLAGS) $<

clean:
	rm ./*.class

.PHONY:	all clean
