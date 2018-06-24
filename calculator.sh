#!/bin/bash

#ARGS: (logsFile)

#MONITORING METHODS:
	#CPU: top -bn1
	#DISK: df -h
	#MEMORY: free -m

#TOP VALUES:
	#'D' = uninterruptible sleep -> it can not be interrupted by a signal, executing kernel functions
	#'R' = running
	#'S' = sleeping
	#'T' = traced or stopped
	#'Z' = zombie

echo -e "\n\n\tInside de 'calculator.sh'"

#for i in "$@"; do
	#echo "$i"
#done

logsFile=$(echo "$1")

lastLine=$(cat $logsFile | tail -n1)
lastLine=$(echo $lastLine | tail -c 4)

#echo -e "Last line is: $lastLine"

#Print a delimiter inside the file

if [ ! $lastLine == "end" ]; then 
	echo -e "end" >> $logsFile
fi


encryptLogs=$(cat $logsFile | sed -n -e '/^encrypter$/,/^decrypter$/{/^encrypter$/d; /^decrypter$/d; p; }')
decryptLogs=$(cat $logsFile | sed -n -e '/^decrypter$/,/^end$/{/^decrypter$/d; /^end$/d; p; }')

#echo -e "\n$encryptLogs"
#echo -e "\n$decryptLogs"

encryptCPUUSage=0	#Average of % of CPU
encryptMEMUsage=0	#Average of % of MEM
encryptMaxCPU=0		#Maximum CPU usage
encryptMaxMEM=0		#Maximum MEM consumption
encryptTime=0		#Time it took to the Server to do the task

decryptCPUUsage=0
decryptMEMUsage=0
cntCPU=0
cntMEM=0

#Change the value from Internal Field Separator to a '\n'
IFS=$'\n'
for j in $encryptLogs; do 
	auxCPUValue=$(echo -e "$j" | awk ' { print $9 } ') #CPU value
	auxMEMValue=$(echo -e "$j" | awk ' { print $10 } ') #MEM value

	#echo -e "$encryptMaxCPU < $auxCPUValue"
	comparation=$(awk ' BEGIN{ print ("'$encryptMaxCPU'"<"'$auxCPUValue'")} ')
	#echo -e "isIt: $comparation"

	if [ $comparation == '1' ]; then 
		encryptMaxCPU=$auxCPUValue
	fi

	#echo -e "$encryptMaxMEM < $auxMEMValue"
	comparation=$(awk ' BEGIN{ print ("'$encryptMaxMEM'"<"'$auxMEMValue'")} ')
	#echo -e "isIt: $comparation"
	
	if [ $comparation == '1' ]; then
		encryptMaxMEM=$auxMEMValue
	fi

	encryptCPUUsage=$(echo $encryptCPUUsage $auxCPUValue | awk '{print $1 + $2}') #Due to sum of float an integer
	encryptMEMUsage=$(echo $encryptMEMUsage $auxMEMValue | awk '{print $1 + $2}') 
	
	if [ ! $auxCPUValue ==  "0.0" ]; then
		cntCPU=$((cntCPU+1))
	fi  

	if [ ! $auxMEMValue == "0.0" ]; then 
		cntMEM=$((cntMEM+1))
	fi
done

echo -e "\tEncryption monitoring: "
echo -e "CPU encrypt usage: \t\t$encryptCPUUsage"
echo -e "Total trustly values: \t\t$cntCPU"
encryptCPUUsage=$(echo $encryptCPUUsage $cntCPU | awk '{print $1 / $2}')

echo -e "Average of %CPU consumption: \t$encryptCPUUsage%"
echo -e "Maximum %CPU consumption: \t$encryptMaxCPU%"

echo -e "Total trustly values: \t\t$cntMEM"
encryptMEMUsage=$(echo $encryptMEMUsage $cntMEM | awk '{print $1 / $2}')
echo -e "Average of %MEM consumption: \t$encryptMEMUsage%"
echo -e "Maximum %MEM consumption: \t$encryptMaxMEM%"


cntCPU=0
cntMEM=0

for j in $decryptLogs; do 
	auxCPUValue=$(echo -e "$j" | awk ' { print $9 } ') #CPU value
	auxMEMValue=$(echo -e "$j" | awk ' { print $10 } ') #MEM value

	#echo -e "$decryptMaxCPU < $auxCPUValue"
	comparation=$(awk ' BEGIN{ print ("'$decryptMaxCPU'"<"'$auxCPUValue'")} ')
	#echo -e "isIt: $comparation"

	if [ $comparation == '1' ]; then 
		decryptMaxCPU=$auxCPUValue
	fi

	#echo -e "$decryptMaxMEM < $auxMEMValue"
	comparation=$(awk ' BEGIN{ print ("'$decryptMaxMEM'"<"'$auxMEMValue'")} ')
	#echo -e "isIt: $comparation"
	
	if [ $comparation == '1' ]; then
		decryptMaxMEM=$auxMEMValue
	fi

	decryptCPUUsage=$(echo $decryptCPUUsage $auxCPUValue | awk '{print $1 + $2}') #Due to sum of float an integer
	decryptMEMUsage=$(echo $decryptMEMUsage $auxMEMValue | awk '{print $1 + $2}') 
	
	if [ ! $auxCPUValue ==  "0.0" ]; then
		cntCPU=$((cntCPU+1))
	fi  

	if [ ! $auxMEMValue == "0.0" ]; then 
		cntMEM=$((cntMEM+1))
	fi
done

echo -e "\n\tDecryption monitoring: "
echo -e "CPU decrypt usage: \t\t$decryptCPUUsage"
echo -e "Total trustly values: \t\t$cntCPU"
decryptCPUUsage=$(echo $decryptCPUUsage $cntCPU | awk '{print $1 / $2}')

echo -e "Average of %CPU consumption: \t$decryptCPUUsage%"
echo -e "Maximum %CPU consumption: \t$decryptMaxCPU%"

echo -e "Total trustly values: \t\t$cntMEM"
decryptMEMUsage=$(echo $decryptMEMUsage $cntMEM | awk '{print $1 / $2}')
echo -e "Average of %MEM consumption: \t$decryptMEMUsage%"
echo -e "Maximum %MEM consumption: \t$decryptMaxMEM%"


echo -e "\n\tDone, exiting from calculator.sh...\n\n"

exit 0