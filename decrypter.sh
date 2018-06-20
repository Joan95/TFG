#!/bin/bash

#ARGS: (nameFile, typeFile, password, HSM, cipher, pathToFile, directory)
#WHERE pathToFile	= $pathToEncrypt/$typeFile/$cipher/$nameFile
#WHERE directory	= $pathToDecrypt/$typeFile

echo -e "\n\n\tInside de 'decrypter.sh'"

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
directory=$(echo "$7")

echo -e "\n\tPath to file $pathToFile"
echo -e "\tSave it here: $directory"

#Check if $directory exists; if it doesn't, make it
if [ ! -d "$directory/$cipher" ]; then
	echo -e "\n\t$directory/$cipher, doesn't exists, creating it"
	mkdir $directory/$cipher
fi

echo -e "\n\tStart decryption\n"

case $cipher in
"AES")
	openssl enc -aes-128-cbc -d -in $pathToFile -out $directory/$cipher/$nameFile -k password -p
	;;
"3DES")
	openssl enc -des3 -d -in $pathToFile -out $directory/$cipher/$nameFile -k password -p
	;;
"Camellia")
	openssl enc -camellia-128-cbc -d -in $pathToFile -out $directory/$cipher/$nameFile -k password -p
	;;
esac 

echo -e "\n\tDone, exiting from decrypter.sh...\n\n"

exit 0