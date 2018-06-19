#!/bin/bash

#ARGS: (nameFile, typeFile, password, HSM, cipher, pathToFile)

#MONITORING METHODS:
	#CPU: top -bn1
	#DISK: df -h
	#MEMORY: free -m

for i in "$@"; do
	echo "$i"
done

nameFile=$(echo "$1")
typeFile=$(echo "$2")
password=$(echo "$3")
hsm=$(echo "$4")
cipher=$(echo "$5")
pathToFile=$(echo "$6")

pathToDecrypt="/home/pi/Desktop/TFG/Decrypt/$typeFile"


echo -e "Decrypting..."
#Checking for salt, whether it is enabled or not.


case $cipher in
"AES")
	openssl enc -aes-128-cbc -d -in $pathToFile/AES/$nameFile -out $pathToDecrypt/AES$nameFile -k password -p
	;;
"3DES")
	openssl enc -des3 -d -in $pathToEncrypt/3DES/$nameFile -out $pathToDecrypt/3DES$nameFile -k password -p
	;;
"Camellia")
	openssl enc -camellia-128-cbc -d -in $pathToEncrypt/Camellia/$nameFile -out $pathToDecrypt/Camellia$nameFile -k password -p
	;;
esac 

exit 0