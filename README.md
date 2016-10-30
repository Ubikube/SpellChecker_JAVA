# SpellChecker_JAVA
Spell checker program, that reads in a list of words and uses that to spell check a file

compile from CMD with:
  javac SpellChecker.java
run with:
  java SpellChecker < SysIn.txt > output.txt
  
Inside SysIn.txt is the single line "inputs\big.txt". big.txt is the default file I have chosen to spell check. you can replace it with a number of files included in the inputs folder. all you have to do to check another file, is change that line. For example, if you wish to spell check hamlet which I have included, replace inputs\big.txt with inputs\hamlet.txt.
the program is built from the dictionary included "dict.txt" a relatively small txt file with around 25 thousand words. This txt file will be read into a hash table. Afterwards, a file will be read and each word will be checked against the hash table for any misspellings.
