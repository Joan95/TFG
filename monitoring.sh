#!/bin/bash 

#ARGS: (logsFile, fileRunning)
#WHERE 	logsFile		= $directoryLogsPath/$nameLogsFile
#WHERE 	directoryLogsPath	= $pathToLogs/$nameFolder
#WHERE 	pathToLogs 		= $base_directory/logs
#WHERE 	fileRunning 		= whether encrypter.sh or decrypter.sh

echo -e "\n\n\tInside de 'monitoring.sh' $logsFile"

#MONITORING METHODS:
	#CPU: top -bn1
	#DISK: df -h
	#MEMORY: free -m

for i in "$@"; do
	echo "- $i"
done

logsFile=$(echo "$1")
fileRunning=$(echo "$2")

(top -bn30 -d 0.2 | grep root ) >> $logsFile

echo -e "End of 'monitoring.sh'"

exit 0