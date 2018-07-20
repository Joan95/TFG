#!/bin/bash 

#ARGS: (logsFile, fileRunning, sizeOfFileMB, hsm)
#WHERE 	logsFile		= $directoryLogsPath/$nameLogsFile
#WHERE 	directoryLogsPath	= $pathToLogs/$nameFolder
#WHERE 	pathToLogs 		= $base_directory/logs
#WHERE 	fileRunning 		= whether encrypter.sh or decrypter.sh

sizeOfFileMB=$(echo "$3")

echo -e "\n\n\t'monitoring.sh' - Inside of 'monitoring.sh'"
echo -e "\n\t'monitoring.sh' - Monitoring has begun for file of $sizeOfFileMB MBytes"

#MONITORING METHODS:
	#CPU: top -bn1
	#DISK: df -h
	#MEMORY: free -m

#for i in "$@"; do
	#echo "- $i"
#done

logsFile=$(echo "$1")
fileRunning=$(echo "$2")
hsm=$(echo "$4")

echo -e "$fileRunning" >> $logsFile

#As bigger is the file, it should increase the value of -d
#As smaller is the file, it should reduce the value of -d
#1GB = 1000MB aprox 50 sec
#Standard of 90 times in 50 sec for 1GB,
#it means that it will take a top every
#1.8 seconds, so, the factor will be:

#	 1000 	-	1.8
#sizeOfFileMB	- 	X

frecOfTop=$(echo $sizeOfFileMB  | awk '{print $1 * "1.8" / "1000"}')

comparation=$(awk ' BEGIN{ print ("'$frecOfTop'"<"0.001")} ')
#echo -e "isIt: $comparation"

if [ $comparation == '1' ]; then
	frecOfTop=0.001
fi

echo -e "\t'monitoring.sh' - Frequency will be of: $frecOfTop seconds for file of $sizeOfFileMB MB\n"

if [ $hsm == 'True' ]; then 
	while [ ! -f temp.txt ]; do 
		echo -e "\t'monitoring.sh' - Waiting for start of encryption using Zymbit"
		sleep 1
	done

	aux=0

	while [ -f temp.txt ] && [ $(more temp.txt) == "1" ]; do
		echo -e "\t'monitoring.sh' - Monitoring the encryption,\n\t\tplease wait, it will take a while - $aux s" 
		(top -bn 10 -d 0.5 | grep zymbit | grep zkifc ) >> $logsFile
		aux=$((aux + 5))
	done 

	if [ ! -f temp.txt ]; then 
		echo -e "\n\t'monitoring.sh' - Monitoring has finished\n"
		exit 0
	fi
else 
	(top -bn 70 -d $frecOfTop | grep openssl ) >> $logsFile
fi

echo -e "\n\t'monitoring.sh' - Monitoring has finished\n"

exit 0