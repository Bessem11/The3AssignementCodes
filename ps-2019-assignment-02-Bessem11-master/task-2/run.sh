javac -cp lib/client.jar:lib/guava-21.0.jar *.java
java -cp .:lib/client.jar:lib/guava-21.0.jar MasterMild "$(pwd)/plugins/client-plugin.jar"

