# Start master-mild.js with aspects enabled should result in producing some output

cat inputs-1.txt | node ../master-mild.js --enable-aspects > output 2>&1

file_size_kb=`du -k "output" | cut -f1`

if [ "${file_size_kb}" -gt "0" ]; then
        echo "TEST PASSED"
else
        echo "Empty output. TEST FAILED."
fi

if [ -e output ]; then rm output; fi
