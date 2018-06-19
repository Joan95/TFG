#!/bin/bash

#ARGS: (nameFile, typeFile, password, HSM, cipher, pathToFile)

#MONITORING METHODS:
	#CPU: top -bn1
	#DISK: df -h
	#MEMORY: free -m

#for i in "$@"; do
	#echo "$i"
#done

nameFile=$(echo "$1")
typeFile=$(echo "$2")
password=$(echo "$3")
hsm=$(echo "$4")
cipher=$(echo "$5")
pathToFile=$(echo "$6")

pathToEncrypt="/home/pi/Desktop/TFG/Encrypt/$typeFile"

echo -e "Encrypting..."

#top -bn5 -d 0.2 | grep bash | grep root >> /home/pi/Desktop/TFG/Logs/outputEncrypt.log &


case $cipher in
"AES")
	openssl enc -aes-128-cbc -in $pathToFile -out $pathToEncrypt/AES/$nameFile -k password -p

	;;
"3DES")
	openssl enc -des3 -in $pathToFile -out $pathToEncrypt/3DES/$nameFile -k password -p
	;;
"Camellia")
	openssl enc -camellia-128-cbc -in $pathToFile -out $pathToEncrypt/Camellia/$nameFile -k password -p
	;;
esac 


exit 0