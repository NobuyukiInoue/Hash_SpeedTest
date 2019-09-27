JAVAC =javac
JAVAC_FLAGS =-encoding SJIS

all:	Out.class ExInteger.class TimeFormatter.class ComputeHashToString.class SearchClearTextString.class MySolution.class Main.class

Out.class:	Out.java
	$(JAVAC) $(JAVAC_FLAGS) $<

ExInteger.class:	ExInteger.java
	$(JAVAC) $(JAVAC_FLAGS) $<

TimeFormatter.class:	TimeFormatter.java
	$(JAVAC) $(JAVAC_FLAGS) $<

ComputeHashToString.class:	ComputeHashToString.java
	$(JAVAC) $(JAVAC_FLAGS) $<

SearchClearTextString.class:	SearchClearTextString.java
	$(JAVAC) $(JAVAC_FLAGS) $<

MySolution.class:	MySolution.java
	$(JAVAC) $(JAVAC_FLAGS) $<

Main.class:	Main.java
	$(JAVAC) $(JAVAC_FLAGS) $<

clean:
	rm ./*.class

.PHONY:	all clean
