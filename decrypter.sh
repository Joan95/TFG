#!/bin/bash

#ARGS: (nameFile, typeFile, password, HSM, cipher, pathToFile, pathToSaveFile)
#Where pathToFile = pathToEncrypt/typeFile

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
pathToSaveFile=$(echo "$7")


echo -e "Decrypting..."

case $cipher in
"AES")
	openssl enc -aes-128-cbc -d -in $pathToFile/AES/$nameFile -out $pathToSaveFile/AES/$nameFile -k password -p
	;;
"3DES")
	openssl enc -des3 -d -in $pathToFile/3DES/$nameFile -out $pathToSaveFile/3DES/$nameFile -k password -p
	;;
"Camellia")
	openssl enc -camellia-128-cbc -d -in $pathToFile/Camellia/$nameFile -out $pathToSaveFile/Camellia/$nameFile -k password -p
	;;
esac 

exit 0