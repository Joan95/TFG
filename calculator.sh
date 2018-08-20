#!/bin/bash

#ARGS: (logsFile, name, size)

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
name=$(echo "$2")
size=$(echo "$3")

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
encryptRAMUsage=0	#Average of RAM's KB used
encryptMaxCPU=0		#Maximum CPU usage
encryptMaxMEM=0		#Maximum MEM consumption
encryptMaxRAM=0		#Maximum RAM KB consumption
encryptTimeI=0		#Time when task started in the server
encryptTimeF=0		#Time when task finished in the server
encryptTime=0
encryptCntCPU=0		#Amount of trustly values from CPU usage
encryptCntMEM=0		#Amount of trustly values from MEM usage

decryptCPUUsage=0	#Consumption average of CPU
decryptMEMUsage=0	#Consumption average of MEM
decryptRAMUsage=0	#Average of RAM's KB used
decryptMaxCPU=0		#Maximum CPU usage
decryptMaxMEM=0		#Maximum MEM consumption
decryptMaxRAM=0		#Maximum RAM KB consumption
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
	auxRAMValue=$(echo -e "$j" | awk ' { print $6 } ') #RAM value
	auxCPUValue=$(echo -e "$j" | awk ' { print $9 } ') #CPU value
	auxMEMValue=$(echo -e "$j" | awk ' { print $10 } ') #MEM value
	encryptTimeF=$(echo -e "$j" | awk ' { print $11 } ') #Final time value

	#echo -e "$encryptMaxRAM < $auxRAMValue"
	comparation=$(awk ' BEGIN{ print ("'$encryptMaxRAM'"<"'$auxRAMValue'")} ')
	#echo -e "isIt: $comparation"

	if [ $comparation == '1' ]; then 
		encryptMaxRAM=$auxRAMValue
	fi

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

	encryptRAMUsage=$(echo $encryptRAMUsage $auxRAMValue | awk '{print $1 + $2}')
	encryptCPUUsage=$(echo $encryptCPUUsage $auxCPUValue | awk '{print $1 + $2}') #Due to sum of float an integer
	encryptMEMUsage=$(echo $encryptMEMUsage $auxMEMValue | awk '{print $1 + $2}') 
	
	if [ ! $auxCPUValue ==  "0.0" ]; then
		encryptCntCPU=$((encryptCntCPU+1))
	fi  

	if [ ! $auxMEMValue == "0.0" ]; then 
		encryptCntMEM=$((encryptCntMEM+1))
	fi
done


# ---------- Time Calclulation ---------- #
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
# ---------- End Time Calculation ---------- #

echo -e "\tEncryption process (Duration: $encryptTimeF - $encryptTimeI = $encryptTime min):"
echo -e "\t\tFile Details"

echo -e "\t\t\tName File: \t\t$name"
echo -e "\t\t\tFile Size: \t\t$size"

echo -e "\t\tCPU"

echo -e "\t\t\tCPU encrypt usage: \t\t$encryptCPUUsage"
echo -e "\t\t\tTotal trustly values: \t\t$encryptCntCPU"
encryptCPUUsage=$(echo $encryptCPUUsage $encryptCntCPU | awk '{print $1 / $2}')
echo -e "\t\t\tAverage of %CPU consumption: \t$encryptCPUUsage%"
echo -e "\t\t\tMaximum %CPU consumption: \t$encryptMaxCPU%"

echo -e "\t\tMEM"

echo -e "\t\t\tRAM encrypt usage: \t\t$encryptRAMUsage KB"
encryptRAMUsage=$(echo $encryptRAMUsage $encryptCntMEM | awk '{print $1 / $2}')
echo -e "\t\t\tAverage RAM KB consumption: \t$encryptRAMUsage KB"
echo -e "\t\t\tMaximum RAM KB consumption: \t$encryptMaxRAM KB"
echo -e "\t\t\tTotal trustly values: \t\t$encryptCntMEM"
encryptMEMUsage=$(echo $encryptMEMUsage $encryptCntMEM | awk '{print $1 / $2}')
echo -e "\t\t\tAverage of %MEM consumption: \t$encryptMEMUsage%"
echo -e "\t\t\tMaximum %MEM consumption: \t$encryptMaxMEM%"


decryptCntCPU=0
decryptCntMEM=0

firstLineDecrypter=$(echo -e "$decryptLogs" | head -n 1 )


decryptTimeI=$(echo -e "$firstLineDecrypter" | awk ' { print $11 } ') #First decrypter time value


for j in $decryptLogs; do 
	auxRAMValue=$(echo -e "$j" | awk ' { print $6 } ') #RAM value
	auxCPUValue=$(echo -e "$j" | awk ' { print $9 } ') #CPU value
	auxMEMValue=$(echo -e "$j" | awk ' { print $10 } ') #MEM value
	decryptTimeF=$(echo -e "$j" | awk ' { print $11 } ') #Final time value

	#echo -e "$decryptMaxRAM < $auxRAMValue"
	comparation=$(awk ' BEGIN{ print ("'$decryptMaxRAM'"<"'$auxRAMValue'")} ')
	#echo -e "isIt: $comparation"

	if [ $comparation == '1' ]; then 
		decryptMaxRAM=$auxRAMValue
	fi

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

	decryptRAMUsage=$(echo $decryptRAMUsage $auxRAMValue | awk '{print $1 + $2}')
	decryptCPUUsage=$(echo $decryptCPUUsage $auxCPUValue | awk '{print $1 + $2}') #Due to sum of float an integer
	decryptMEMUsage=$(echo $decryptMEMUsage $auxMEMValue | awk '{print $1 + $2}') 
	
	if [ ! $auxCPUValue ==  "0.0" ]; then
		decryptCntCPU=$((decryptCntCPU+1))
	fi  

	if [ ! $auxMEMValue == "0.0" ]; then 
		decryptCntMEM=$((decryptCntMEM+1))
	fi
done

# ---------- Time Calclulation ---------- #
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
# ---------- End Time Calculation ---------- #

echo -e "\n\tDecryption process (Duration $decryptTimeF - $decryptTimeI = $decryptTime min):"
echo -e "\t\tCPU"

echo -e "\t\t\tCPU decrypt usage: \t\t$decryptCPUUsage"
echo -e "\t\t\tTotal trustly values: \t\t$decryptCntCPU"
decryptCPUUsage=$(echo $decryptCPUUsage $decryptCntCPU | awk '{print $1 / $2}')

echo -e "\t\t\tAverage of %CPU consumption: \t$decryptCPUUsage%"
echo -e "\t\t\tMaximum %CPU consumption: \t$decryptMaxCPU%"

echo -e "\t\tMEM"

echo -e "\t\t\tRAM decrypt usage: \t\t$decryptRAMUsage KB"
decryptRAMUsage=$(echo $decryptRAMUsage $decryptCntMEM | awk '{print $1 / $2}')
echo -e "\t\t\tAverage RAM KB consumption: \t$decryptRAMUsage KB"
echo -e "\t\t\tMaximum RAM KB consumption: \t$decryptMaxRAM KB"
echo -e "\t\t\tTotal trustly values: \t\t$decryptCntMEM"
decryptMEMUsage=$(echo $decryptMEMUsage $decryptCntMEM | awk '{print $1 / $2}')
echo -e "\t\t\tAverage of %MEM consumption: \t$decryptMEMUsage%"
echo -e "\t\t\tMaximum %MEM consumption: \t$decryptMaxMEM%"

#ReturnValue 
	#e => Encrypt
	#encryptCPUUSage	#Average of % of CPU
	#encryptMEMUsage	#Average of % of MEM
	#encryptRAMUsage	#Average of KB of RAM
	#encryptMaxCPU		#Maximum CPU usage
	#encryptMaxMEM		#Maximum MEM consumption
	#encryptMaxRAM		#Maximum RAM consumption
	#encryptTime		#Time it took to the Server to do the task
	#encryptCntCPU		#Number of Trustly values of CPU
	#encryptCntMEM		#Number of Trustly values of MEM
	#d => Decrypt
	#decryptCPUUSage	#Average of % of CPU
	#decryptMEMUsage	#Average of % of MEM
	#decryptRAMUsage	#Average of KB of RAM
	#decryptMaxCPU		#Maximum CPU usage
	#decryptMaxMEM		#Maximum MEM consumption
	#decryptMaxRAM		#Maximum RAM consumption
	#decryptTime		#Time it took to the Server to do the task
	#decryptCntCPU		#Number of Trustly values of CPU
	#decryptCntMEM		#Number of Trustly values of MEM

returnValue="type=encryption;\nvalues=header;\nname=$name;\nsize=$size;\nsend;\nvalues=CPU;\nCPUUsage=$encryptCPUUsage;\nmaxCPU=$encryptMaxCPU;\ncntCPU=$encryptCntCPU;\nsend;\nvalues=MEM;\nMEMUsage=$encryptMEMUsage;\nmaxMEM=$encryptMaxMEM;\ncntMEM=$encryptCntMEM;\nsend;\nvalues=RAM;\nRAMUsage=$encryptRAMUsage;\nmaxRAM=$encryptMaxRAM;\ntimeUsed=$encryptTime;\nsend;
\n\ntype=decryption;\nvalues=header;\nname=$name;\nsize=$size;\nsend;\nvalues=CPU;\nCPUUsage=$decryptCPUUsage;\nmaxCPU=$decryptMaxCPU;\ncntCPU=$decryptCntCPU;\nsend;\nvalues=MEM;\nMEMUsage=$decryptMEMUsage;\nmaxMEM=$decryptMaxMEM;\ncntMEM=$decryptCntMEM;\nsend;\nvalues=RAM;\nRAMUsage=$decryptRAMUsage;\nmaxRAM=$decryptMaxRAM;\ntimeUsed=$decryptTime;\nsend"

echo -e "$returnValue" >> temporal.txt

echo -e "\n\tDone, exiting from calculator.sh...\n\n"

exit 0