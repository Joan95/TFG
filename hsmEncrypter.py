#!/usr/bin/python

import sys

#ARGS: (nameFile, typeFile, password, HSM, cipher, pathToFile, directory)
#WHERE pathToFile	= $pathToResources/$typeFile/$nameFile
#WHERE pathToEncrypt 	= $device_directory/Encrypt
#WHERE directory	= $pathToEncrypt/$typeFile

print "\n\tInside hsmEncrypter.py file"

#print (sys.argv)

nameFile = sys.argv[1]
typeFile = sys.argv[2]
password = sys.argv[3]
hsm = sys.argv[4]
cipher = sys.argv[5]
pathToFile = sys.argv[6]
directory = sys.argv[7]

print "\n"