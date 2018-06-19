#!/bin/bash

#ARGS: (FileName, action, password, salt, pathToResources)

#MONITORING METHODS:
	#CPU: top -bn1
	#DISK: df -h
	#MEMORY: free -m

#for i in "$@"; do
	#echo "$i"
#done

nameFile=$(echo "$1")
action=$(echo "$2")
password=$(echo "$3")
pathToResources=$(echo "$5")
method=$(echo "$6")

pathToEncrypt="/home/pi/Desktop/TFG/Encrypt"


cd $pathToResources

echo -e "Encrypting..."
#Checking for salt, whether it is enabled or not.

#top -bn5 -d 0.2 | grep bash | grep root >> /home/pi/Desktop/TFG/Logs/output$2.log &

if [ "$4" = "true" ]
then 
	
	case $method in
	"AES")
		openssl enc -aes-128-cbc -in $nameFile -out $pathToEncrypt/AES$nameFile -k password -p

		;;
	"3DES")
		openssl enc -des3 -in $nameFile -out $pathToEncrypt/3DES$nameFile -k password -p
		;;
	"Camellia")
		openssl enc -camellia-128-cbc -in $nameFile -out $pathToEncrypt/Camellia$nameFile -k password -p
		;;
	esac 
else 
	openssl enc -nosalt -aes-128-cbc -in $nameFile -out $pathToEncrypt/noSalt$nameFile -k password -p
fi


exit 0