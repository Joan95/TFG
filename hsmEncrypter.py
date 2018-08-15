#!/usr/bin/python


import zymkey
from zymkey.exceptions import VerificationError

import sys
import os
import os.path

zymkey.__init__
# Flash the LED to indicate the operation is underway
zymkey.client.led_flash(500, 100)

#ARGS: (nameFile, typeFile, password, HSM, cipher, pathToFile, directory)
#WHERE pathToFile	= $pathToResources/$typeFile/$nameFile
#WHERE pathToEncrypt 	= $device_directory/Encrypt
#WHERE directory	= $pathToEncrypt/$typeFile

print "\n\t\tInside of 'hsmEncrypter.py'"

#print (sys.argv)

nameFile = sys.argv[1]
typeFile = sys.argv[2]
password = sys.argv[3]
hsm = sys.argv[4]
cipher = sys.argv[5]
pathToFile = sys.argv[6]
directory = sys.argv[7]

print "\n\t\tPath to file %s" % (pathToFile)
print "\n\t\tSave it here: %s" % (directory)

if not os.path.exists(directory):
	print "\n\t\t%s, doesn't exists, creating it" % (directory)
	os.makedirs(directory)

print "\n\t\t---------- Start of encryption using Zymbit ----------"

file = open("temp.txt", "w+")
file.write("1")
file.close()

zymbitDir = str("%s/zymbit" % (directory))
zymbitFile = str("%s/%s" % (zymbitDir, nameFile))
if not os.path.exists(zymbitDir):
	print "\n\t\t%s, doesn't exists, creating it" % (zymbitDir)
	os.makedirs(zymbitDir)

if os.path.exists(zymbitFile):
	print "\n\t%s file already exists, removing it" % (zymbitFile)
	os.remove(zymbitFile)

zymbitF = open(zymbitFile, "w+")
zymbitF.close()

encrypted = zymkey.client.lock(pathToFile, zymbitFile)


if os.path.isfile("temp.txt"):
	os.remove("temp.txt")


print "\n\t\t---------- End of encryption using Zymbit ----------"
print "\n\t\tDone, exiting from 'hsmEncrypter.py'"

# Turn off the LED
zymkey.client.led_off()
