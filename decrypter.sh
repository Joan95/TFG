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
pathToDecrypt="/home/pi/Desktop/TFG/Decrypt"


cd $pathToResources

echo -e "Decrypting..."
#Checking for salt, whether it is enabled or not.

if [ "$4" = "true" ]
then
	case $method in
	"AES")
		openssl enc -aes-128-cbc -d -in $pathToEncrypt/AES$nameFile -out $pathToDecrypt/AES$nameFile -k password -p
		;;
	"3DES")
		openssl enc -des3 -d -in $pathToEncrypt/3DES$nameFile -out $pathToDecrypt/3DES$nameFile -k password -p
		;;
	"Camellia")
		openssl enc -camellia-128-cbc -d -in $pathToEncrypt/Camellia$nameFile -out $pathToDecrypt/Camellia$nameFile -k password -p
		;;
	esac 
else
	openssl enc -aes-128-cbc -d -in $pathToEncrypt/noSalt$nameFile -out $pathToEncrypt/noSalt$nameFile -k password -p

fi

exit 0