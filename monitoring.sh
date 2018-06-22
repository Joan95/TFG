#!/bin/bash 

#ARGS: (logsFile, fileRunning, typeFile)
#WHERE 	logsFile		= $directoryLogsPath/$nameLogsFile
#WHERE 	directoryLogsPath	= $pathToLogs/$nameFolder
#WHERE 	pathToLogs 		= $base_directory/logs
#WHERE 	fileRunning 		= whether encrypter.sh or decrypter.sh

typeFile=$(echo "$3")

echo -e "\n\n\tMonitoring has begun for type $typeFile"

#MONITORING METHODS:
	#CPU: top -bn1
	#DISK: df -h
	#MEMORY: free -m

#for i in "$@"; do
	#echo "- $i"
#done

logsFile=$(echo "$1")
fileRunning=$(echo "$2")

if [ $typeFile == "Text" ] || [ $typeFile == "Audio" ]; then

	(top -bn 50 -d 0.01 | grep openssl ) >> $logsFile

else 

	(top -d 0.1 | grep openssl ) >> $logsFile

fi

echo -e "\tMonitoring has finished"

exit 0