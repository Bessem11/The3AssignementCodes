# Basic bash script to build and run Java
#!/bin/bash

# Fail fast
set -e 

( >&2 echo "* Clean up .class files" )
( >&2 find . -iname "*.class" -exec rm -v {} \;)



( >&2 echo "* Compile MasterMild" )
javac -cp . *.java

( >&2 echo "* Run MasterMild" )
java -cp . MasterMild

