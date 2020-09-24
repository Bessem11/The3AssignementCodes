# Start master-mild.js without enabling aspects should result in producing no output at all

cat inputs-1.txt | node ../master-mild.js > output 2>&1

file_size_kb=`du -k "output" | cut -f1`

if [ "${file_size_kb}" -gt "0" ]; then
	echo "Non-empty output. TEST FAILED."
        cat output
else
	echo "TEST PASSED"
fi

if [ -e output ]; then rm output; fi
