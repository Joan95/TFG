#!/bin/bash

#ARGS: (logsFile)

echo -e "\n\n\tInside de 'calculator.sh'"

#MONITORING METHODS:
	#CPU: top -bn1
	#DISK: df -h
	#MEMORY: free -m

#for i in "$@"; do
	#echo "$i"
#done

encryptLogs=$(sed -n -e '/^encrypter$/,/^decrypter$/{/^encrypter$/d; /^decrypter$/d; p; }')

echo -e "\n$encryptLogs"

echo -e "\n\tDone, exiting from calculator.sh...\n\n"

exit 0