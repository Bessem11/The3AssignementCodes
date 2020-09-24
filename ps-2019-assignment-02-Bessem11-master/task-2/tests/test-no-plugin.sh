# The default implementation of the client is to generate inputs like 0000 until the game is on.
# This tests assume that digits=4 and secret.code=1234 and max.attempts=10

java -cp ..:../lib/client.jar:../lib/guava-21.0.jar MasterMild > output 2>&1

diff ../tests/expected-default-output.txt output > /dev/null 2>&1
error=$?
if [ ! $error -eq 0 ]; then
    echo "Program output does not match. TEST FAILED"
    diff ../tests/expected-default-output.txt output
else
    echo "TEST PASSED"
fi

if [ -e output ]; then rm output; fi