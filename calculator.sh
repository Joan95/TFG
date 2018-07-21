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

echo -e "\n\n\tInside de 'calculator.sh'\n"

#for i in "$@"; do
	#echo "$i"
#done

logsFile=$(echo "$1")

lastLine=$(cat $logsFile | tail -n1)
lastLine=$(echo $lastLine | tail -c 4)

#echo -e "Last line is: $lastLine"

#Print a delimiter inside the file

isThereDec=$(echo $(cat $logsFile | grep "decrypter"))
#echo -e "isThereDec = $isThereDec"

while [ ! "$isThereDec" == "decrypter" ]; do
	echo -e "\t'calculator.sh' - Waiting for 'decrypter' to be wrote into the file."
	sleep 1
done

if [ ! $lastLine == "end" ]; then 
	echo -e "end" >> $logsFile
fi


encryptLogs=$(cat $logsFile | sed -n -e '/^encrypter$/,/^decrypter$/{/^encrypter$/d; /^decrypter$/d; p; }')
decryptLogs=$(cat $logsFile | sed -n -e '/^decrypter$/,/^end$/{/^decrypter$/d; /^end$/d; p; }')

#echo -e "\n$encryptLogs"
#echo -e "\n$decryptLogs"

encryptCPUUSage=0	#Consumption average of CPU
encryptMEMUsage=0	#Consumption average of MEM
encryptMaxCPU=0		#Maximum CPU usage
encryptMaxMEM=0		#Maximum MEM consumption
encryptTimeI=0		#Time when task started in the server
encryptTimeF=0		#Time when task finished in the server
encryptTime=0
encryptCntCPU=0		#Amount of trustly values from CPU usage
encryptCntMEM=0		#Amount of trustly values from MEM usage

decryptCPUUsage=0	#Consumption average of CPU
decryptMEMUsage=0	#Consumption average of MEM
decryptMaxCPU=0		#Maximum CPU usage
decryptMaxMEM=0		#Maximum MEM consumption
decryptTimeI=0		#Time when task started in the server
decryptTimeF=0		#Time when task finished in the server
decryptTime=0
decryptCntCPU=0		#Amount of trustly values from CPU usage
decryptCntMEM=0		#Amount of trustly values from MEM usage

#Change the value from Internal Field Separator to a '\n'
IFS=$'\n'

firstLineEncrypter=$(echo -e "$encryptLogs" | head -n 1 )
encryptTimeI=$(echo -e "$firstLineEncrypter" | awk ' { print $11 } ') #First encrypter time value

for j in $encryptLogs; do 
	#echo -e "$j"
	auxCPUValue=$(echo -e "$j" | awk ' { print $9 } ') #CPU value
	auxMEMValue=$(echo -e "$j" | awk ' { print $10 } ') #MEM value
	encryptTimeF=$(echo -e "$j" | awk ' { print $11 } ') #Final time value

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
		encryptCntCPU=$((encryptCntCPU+1))
	fi  

	if [ ! $auxMEMValue == "0.0" ]; then 
		encryptCntMEM=$((encryptCntMEM+1))
	fi
done

encryptTime=$(echo $encryptTimeF | sed -e "s/\./\:/g")
#echo -e "$encryptTime after replacement"

encryptTime=$(echo $encryptTimeF | awk -F':' '{ print ($1 * 3600) + ($2 * 60) + ($3 * 1) }')
#echo -e "$encryptTime ready for the substraction"

encryptTimeAux=$(echo $encryptTimeI | sed -e "s/\./\:/g")
#echo -e "$encryptTimeAux after replacement"
encryptTimeAux=$(echo $encryptTimeI | awk -F':' '{ print ($1 * 3600) + ($2 * 60) + ($3 * 1) }')
#echo -e "$encryptTimeAux ready for the substraction"

encryptTime=$(echo $encryptTime $encryptTimeAux | awk '{ print $1 - $2 }')
encryptTime=$(echo $encryptTime | awk '{ print $1 / 60 }')

echo -e "\tEncryption process (Duration: $encryptTimeF - $encryptTimeI = $encryptTime min):"
echo -e "\t\tCPU"
echo -e "\t\t\tCPU encrypt usage: \t\t$encryptCPUUsage"
echo -e "\t\t\tTotal trustly values: \t\t$encryptCntCPU"
encryptCPUUsage=$(echo $encryptCPUUsage $encryptCntCPU | awk '{print $1 / $2}')

echo -e "\t\t\tAverage of %CPU consumption: \t$encryptCPUUsage%"
echo -e "\t\t\tMaximum %CPU consumption: \t$encryptMaxCPU%"

echo -e "\t\tMEM"

echo -e "\t\t\tTotal trustly values: \t\t$encryptCntMEM"
encryptMEMUsage=$(echo $encryptMEMUsage $encryptCntMEM | awk '{print $1 / $2}')
echo -e "\t\t\tAverage of %MEM consumption: \t$encryptMEMUsage%"
echo -e "\t\t\tMaximum %MEM consumption: \t$encryptMaxMEM%"


decryptCntCPU=0
decryptCntMEM=0

firstLineDecrypter=$(echo -e "$decryptLogs" | head -n 1 )
decryptTimeI=$(echo -e "$firstLineDecrypter" | awk ' { print $11 } ') #First decrypter time value

for j in $decryptLogs; do 
	auxCPUValue=$(echo -e "$j" | awk ' { print $9 } ') #CPU value
	auxMEMValue=$(echo -e "$j" | awk ' { print $10 } ') #MEM value
	decryptTimeF=$(echo -e "$j" | awk ' { print $11 } ') #Final time value

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
		decryptCntCPU=$((decryptCntCPU+1))
	fi  

	if [ ! $auxMEMValue == "0.0" ]; then 
		decryptCntMEM=$((decryptCntMEM+1))
	fi
done

decryptTime=$(echo $decryptTimeF | sed -e "s/\./\:/g")
#echo -e "$decryptTime after replacement"

decryptTime=$(echo $decryptTimeF | awk -F':' '{ print ($1 * 3600) + ($2 * 60) + ($3 * 1) }')
#echo -e "$decryptTime ready for the substraction"

decryptTimeAux=$(echo $decryptTimeI | sed -e "s/\./\:/g")
#echo -e "$decryptTimeAux after replacement"
decryptTimeAux=$(echo $decryptTimeI | awk -F':' '{ print ($1 * 3600) + ($2 * 60) + ($3 * 1) }')
#echo -e "$decryptTimeAux ready for the substraction"

decryptTime=$(echo $decryptTime $decryptTimeAux | awk '{ print $1 - $2 }')
decryptTime=$(echo $decryptTime | awk '{ print $1 / 60 }')

echo -e "\n\tDecryption process (Duration $decryptTimeF - $decryptTimeI = $decryptTime min):"
echo -e "\t\tCPU"
echo -e "\t\t\tCPU decrypt usage: \t\t$decryptCPUUsage"
echo -e "\t\t\tTotal trustly values: \t\t$decryptCntCPU"
decryptCPUUsage=$(echo $decryptCPUUsage $decryptCntCPU | awk '{print $1 / $2}')

echo -e "\t\t\tAverage of %CPU consumption: \t$decryptCPUUsage%"
echo -e "\t\t\tMaximum %CPU consumption: \t$decryptMaxCPU%"

echo -e "\t\tMEM"
echo -e "\t\t\tTotal trustly values: \t\t$decryptCntMEM"
decryptMEMUsage=$(echo $decryptMEMUsage $decryptCntMEM | awk '{print $1 / $2}')
echo -e "\t\t\tAverage of %MEM consumption: \t$decryptMEMUsage%"
echo -e "\t\t\tMaximum %MEM consumption: \t$decryptMaxMEM%"

#ReturnValue 
	#e => Encrypt
	#encryptCPUUSage	#Average of % of CPU
	#encryptMEMUsage	#Average of % of MEM
	#encryptMaxCPU		#Maximum CPU usage
	#encryptMaxMEM		#Maximum MEM consumption
	#encryptTime		#Time it took to the Server to do the task
	#encryptCntCPU		#Number of Trustly values of CPU
	#encryptCntMEM		#Number of Trustly values of MEM
	#d => Decrypt
	#decryptCPUUSage	#Average of % of CPU
	#decryptMEMUsage	#Average of % of MEM
	#decryptMaxCPU		#Maximum CPU usage
	#decryptMaxMEM		#Maximum MEM consumption
	#decryptTime		#Time it took to the Server to do the task
	#decryptCntCPU		#Number of Trustly values of CPU
	#decryptCntMEM		#Number of Trustly values of MEM

returnValue="type=encryption;\nCPUUsage=$encryptCPUUsage;\nMEMUsage=$encryptMEMUsage;\nmaxCPU=$encryptMaxCPU;\nmaxMEM=$encryptMaxMEM;\ntimeUsed=$encryptTime;\ncntCPU=$encryptCntCPU;\ncntMEM=$encryptCntMEM;new;\n\ntype=decryption;\nCPUUsage=$decryptCPUUsage;\nMEMUsage=$decryptMEMUsage;\nmaxCPU=$decryptMaxCPU;\nmaxMEM=$decryptMaxMEM;\ntimeUsed=$decryptTime;\ncntCPU=$decryptCntCPU;\ncntMEM=$decryptCntMEM;new"

echo -e "$returnValue" >> temporal.txt

echo -e "\n\tDone, exiting from calculator.sh...\n\n"

exit 0