#!/usr/bin/python


import zymkey
from zymkey.exceptions import VerificationError

import sys

#DELETE IT 
import time

# Flash the LED to indicate the operation is underway
zymkey.client.led_flash(500, 100)

#ARGS: (nameFile, typeFile, password, HSM, cipher, pathToFile, directory)
#WHERE pathToFile	= $pathToResources/$typeFile/$nameFile
#WHERE pathToEncrypt 	= $device_directory/Encrypt
#WHERE directory	= $pathToEncrypt/$typeFile

print "\n\tInside of 'hsmEncrypter.py'"

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

time.sleep(5)

# Turn off the LED
zymkey.client.led_off()

print "\n\n\t---------- Start of encryption ----------"


print "\n\t\t---------- End of encryption ----------"
print "\n\t\tDone, exiting from 'hsmEncrypter.py'"
