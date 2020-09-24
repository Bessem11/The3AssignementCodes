# Basic bash script to build and run JavaScript
#!/bin/bash

# Fail fast
set -e 

( >&2 echo "* Clean up project" )



( >&2 echo "* Run MasterMild" )
# This commands inherits std in
node master-mild.js

