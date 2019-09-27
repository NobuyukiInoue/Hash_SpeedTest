JAVAC =javac
JAVAC_FLAGS =-encoding SJIS

all:	Main.class MySolution.class ExInteger.class TimeFormatter.class SearchClearTextBytes.class ComputeHashToBytes.class

Out.class:	Out.java
	$(JAVAC) $(JAVAC_FLAGS) $<

ExInteger.class:	ExInteger.java
	$(JAVAC) $(JAVAC_FLAGS) $<

TimeFormatter.class:	TimeFormatter.java
	$(JAVAC) $(JAVAC_FLAGS) $<

ComputeHashToBytes.class:	ComputeHashToBytes.java
	$(JAVAC) $(JAVAC_FLAGS) $<

ComputeHashToString.class:	ComputeHashToString.java
	$(JAVAC) $(JAVAC_FLAGS) $<

SearchClearTextBytes.class:	SearchClearTextBytes.java
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
