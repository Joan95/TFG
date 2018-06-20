#!/bin/bash 

#ARGS: (logsFile)
#WHERE 	logsFile		= $directoryLogsPath/$nameLogsFile
#WHERE 	directoryLogsPath	= $pathToLogs/$nameFolder
#WHERE 	pathToLogs 		= $base_directory/logs

echo "Encrypt" >> /home/pi/Desktop/TFG/Logs/outputEncrypt.txt
$(top -bn40 -d 0.2 | grep encrypter.sh) >> /home/pi/Desktop/TFG/Logs/outputEncrypt.txt


exit 0