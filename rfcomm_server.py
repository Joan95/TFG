#!/usr/bin/python

# ------------- ------------- Imports ------------- ------------- #
import os
from os import listdir
import os.path
from os.path import isfile, join 

import sys
import traceback
import time
import datetime

import json


import zymkey
from zymkey.exceptions import VerificationError

import subprocess
from multiprocessing import Process
from subprocess import PIPE, Popen


from bluetooth import *

# ------------- ------------- Socket's configuration ------------- ------------- #
server_sock=BluetoothSocket( RFCOMM )
server_sock.bind(("",PORT_ANY))
server_sock.listen(1)

port = server_sock.getsockname()[1]

uuid = "94f39d29-7d6d-437d-973b-fba39e49d4ee"

advertise_service( server_sock, "SampleServer",
                   service_id = uuid,
                   service_classes = [ uuid, SERIAL_PORT_CLASS ],
                   profiles = [ SERIAL_PORT_PROFILE ] 
#                   protocols = [ OBEX_UUID ] 
                    )

# ------------- ------------- Classes ------------- -------------# 

class Message:
	def __init__(self, cipher, typeFile, nameFile, password, hsm):
		self.cipher = cipher
		self.typeFile = typeFile
		self.nameFile = nameFile
		self.password = password
		self.hsm = hsm
		self.size = None
	
	def __repr__(self):
		return str(self.__dict__)


# ------------- ------------- Variables ------------- -------------# 
base_directory = "/home/pi/Desktop/TFG"                 
device_directory = "/media/pi/Transcend/TFG"

i2c_info_file = "/sys/kernel/debug/tracing/trace"

optionFile = ""                 # Name File Option to be charged
optionFunction = ""             # Function has been choosen

noEnd = False                   # Boolean for while's switch menu

msOn = 0                        # Amount of miliseconds LED will be ON
msOff = 0                       # Amount of miliseconds LED will be OFF
numFlashes = 0                  # Number of Flashes it will do 

pathToResources = str("%s/%s" % (device_directory, "Resources"))
pathToRandomGenerated = str("%s/%s" % (pathToResources, "RandomGenerated"))
pathToEncrypt = str("%s/%s" % (device_directory, "Encrypt"))
pathToDecrypt = str("%s/%s" % (device_directory, "Decrypt"))
pathToLogs = str("%s/%s" % (base_directory, "Logs"))

cipher = ""
typeFile = ""
nameFile = ""
password = ""
hsm = ""

now = ""
nameFolder = ""
nameLogsFile = ""
directoryLogsPath = ""
pathToLogsFile = ""
logsFile = ""

fileRunning = ""

# ------------- ------------- Recommended Variables ------------- -------------#
i2cAddress = '0x30'                             # I2C default address
tapSensibility = 50                             # TAP's default sensibility 
recommendedMinRandomFileSize = 0                # Recommended minimum size of Random File in Kilobytes
recommendedMaxRandomFileSize = 5120             # Recommended maximum size of Random File in Kilobytes

                ########## Dicctionaries ##########

SendMessage = {}                # Dictionary to send messages between devices
FDevice = {}                    # Dictionary to save file's information resident in the PenDrive
SFDevice = {}                   # Dictionary to send System's File recollected to the device


# ------------- ------------- Funtions ------------- -------------#

                ########## System Files Logs Creation ##########
def createLogsSF(typeFile, cipher, nameFile, sizeOfFileMB, hsm):
	now = datetime.datetime.now()

	#Name format = 'ss-mm-hh.txt'
	nameLogsFile = str("%s-%s-%s.txt" % (now.hour, now.minute, now.second))

	#Name folder format = 'DD-MM-YYYY'
	nameFolder = str("%s-%s-%s" % (now.day, now.month, now.year))

	global directoryLogsPath
	directoryLogsPath = str("%s/%s" % (pathToLogs, nameFolder))
	
	#Check whether the directory for logs exists or not
	if not os.path.exists(directoryLogsPath):
		os.makedirs(directoryLogsPath)

	global logsFile
	logsFile = str("%s/%s" % (directoryLogsPath, nameLogsFile))
	
	toWrite = str("type:%s, cipher:%s, nameFile:%s, sizeOfFileMB:%s, hsm:%s\n" % (typeFile, cipher, nameFile, sizeOfFileMB, hsm))
	file = open(logsFile, "w+")
	file.write(toWrite)
	file.close()

                ########## Send System File to target ##########
def sendSF(pathToResources,FDevice,SFDevice):
	deviceDirectories = os.listdir(pathToResources)

	print "\n\tSending all the System Files routes..."
	print "\tDirectories:\n\t\t%s" % (deviceDirectories)

	for directory in deviceDirectories:
		deviceFilePath = str("%s/%s" % (pathToResources, directory))
		listFDevice = []

		for file in listdir(deviceFilePath):
			if isfile:
				fileName = file
				fileSizeMB = os.path.getsize(str("%s/%s" % (deviceFilePath, file))) >> 20
				FDevice["name"] = file
				FDevice["size"] = fileSizeMB
			listFDevice.append(FDevice.copy())

		SFDevice["type"] = directory
		SFDevice["files"] = listFDevice			
		
		SFDevice = json.dumps(SFDevice)
		SFDevice = str("{'System Files': %s}" % (SFDevice))

		print SFDevice
		client_sock.send(SFDevice)
		FDevice = {}
		SFDevice = {}



# ------------- ------------- Main ------------- ------------- #

print "\n\n\t\tWelcome to my TFG!\n\n"
zymkey.client.__init__                          # Zymkey initialization
zymkey.client.set_i2c_address(i2cAddress)       # Set I2C address to '0x30'

try:
	while True:
		print "Waiting for connection on RFCOMM channel %d " % port
		
		client_sock, client_info = server_sock.accept()

		print "Accepted connection from ", client_info

		try:
		    while True:
		        data = client_sock.recv(1024)
	
		        if len(data) == 0: break

		        print "Message received from %s:\n\t[%s]\n" % (client_info, data)
	
			#Data to JSON
			jsonMessage = json.loads(data)
			#print "jsonMessage:\n%s" % jsonMessage

                        optionFunction = str("%s" % (jsonMessage.get("Function")))
			print "%s" % optionFunction
			optionFile = str("%s.py" % (jsonMessage.get("Function")))

                        SendMessage = {}        # Clear Dicctionary for a new assignment
                        SendMessage["OpF"] = optionFunction
			SendMessage["msg"] = "OK"
			SendMessage["act"] = "start"
			SendMessage["bytes"] = sys.getsizeof(SendMessage)

			
			SendMessage = json.dumps(SendMessage)
			print "Sending message %s" % SendMessage
			print "\n"
					
			client_sock.send(SendMessage)

                        noEnd = True
                        
                        ############# Enormous Switch Case #############


                        if optionFunction == 'RTC':
                                print "Option RTC has been choosen"
                                while (noEnd) :
                                        noEnd = True

                                        timeNotPrecise = zymkey.client.get_time(False)
                                        timePrecise = zymkey.client.get_time(True)
                                        
                                        SendMessage = {}
                                        SendMessage["RTCNotPreciseTime"] = timeNotPrecise
                                        SendMessage["RTCPreciseTime"] = timePrecise
                                        SendMessage = json.dumps(SendMessage)
                                        SendMessage = str("{'RTC': %s}" % (SendMessage))
                                        print SendMessage
                                        print "\n"        
                                        client_sock.send(SendMessage)

                                        print "Waiting to recieve confirmation from device ", client_info
                                        print "\n"

                                        data = client_sock.recv(1024)
                                        if len(data) == 0: break

                                        #Data to JSON
                                        jsonMessage = json.loads(data)
                                        print "jsonMessage:\n%s" % jsonMessage

                                        if jsonMessage.get("message") == 'endFunction':
                                                print "End of Function has been selected"
                                                noEnd = False

                                                
			
                        elif optionFunction == 'LED':
                                print "Option LED has been choosen"
                                while (noEnd) :
                                        noEnd = True

                                        print "Waiting to recieve LED's configuration from device ", client_info
                                        print "\n"

                                        data = client_sock.recv(1024)
                                        if len(data) == 0: break

                                        #Data to JSON
                                        jsonMessage = json.loads(data)
                                        print "jsonMessage:\n%s" % jsonMessage

                                        if jsonMessage.get("message") == 'endFunction':
                                                print "End of Function has been selected"
                                                noEnd = False

                                        elif jsonMessage.get("LED") == 'ON':
                                                print "LED on has been received"
                                                zymkey.client.led_on()

                                        elif jsonMessage.get("LED") == 'OFF':
                                                print "LED off has been received"
                                                zymkey.client.led_off()
                                                               
                                        elif jsonMessage.get("LED") == 'TRIGGER':
                                                print "LED trigger has been received"
                                                msOn = jsonMessage.get("msOn")
                                                msOff = jsonMessage.get("msOff")
                                                numFlashes = jsonMessage.get("numFlashes")

                                                if msOn == "":
                                                        msOn = 0
                                                else:
                                                        msOn = int(msOn)

                                                if msOff == "":
                                                        msOff = 0
                                                else:
                                                        msOff = int(msOff)

                                                if numFlashes == "":
                                                        numFlashes = 0
                                                else:
                                                        numFlashes = int(numFlashes) * 2
                                                
                                                if msOn == 0:
                                                        msOn = 100
                                                                 
                                                zymkey.client.led_flash(msOn,msOff,numFlashes)

                                        else:
                                                print "No possible option"

                        elif optionFunction == 'encrypt_decrypt':
                                print "Option encrypt_decrypt has been choosen"
                                while (noEnd):
                                        noEnd = True
                                        
                                        sendSF(pathToResources,FDevice,SFDevice)
                                                
                                        print "\tThe whole System File information has sent to: ", client_info
                                        print "\n"
                                        print "Please press \'REFRESH\' if there are some files you haven't received"
                                        print "Waiting to recieve the message from device ", client_info
                                        print "\n"

                                        data = client_sock.recv(1024)
                                        if len(data) == 0: break

                                        #Data to JSON
                                        jsonMessage = json.loads(data)
                                        print "jsonMessage:\n%s" % jsonMessage

                                        if jsonMessage.get("message") == 'endFunction':
                                                print "End of Function has been selected"
                                                noEnd = False

                                        elif jsonMessage.get("message") == 'refresh':
                                                #print "Resending System Files"
                                                #print "\tCollecting information of files from: ", pathToResources
                                                FDevice = {}
                                                SFDevice = {}

                                                sendSF(pathToResources,FDevice,SFDevice)
                                                        
                                                print "\tThe whole System File information has sent to ", client_info
                                                print "\n"

                                        else:
                                                #print "\ncipher: %s" % jsonMessage["cipher"]
                                                #print "typeFile: %s" % jsonMessage["typeFile"]
                                                #print "nameFile: %s" % jsonMessage["nameFile"]
                                                #print "password: %s" % jsonMessage["password"]
                                                #print "hsm: %s" % jsonMessage["hsm"]
                                
                                                cipher = str(jsonMessage["cipher"])
                                                typeFile = str(jsonMessage["typeFile"])
                                                nameFile = str(jsonMessage["nameFile"])
                                                password = str(jsonMessage["password"])
                                                hsm = str(jsonMessage["hsm"])
                                
                                
                                                newMessage = Message(cipher, typeFile, nameFile, password, hsm)
                                
                                
                                                #Checking whether file exists.
                                                
                                                pathToFile = str("%s/%s/%s" % (pathToResources, newMessage.typeFile, newMessage.nameFile))
                                
                                                sizeOfFileMB = str("%s" % (os.path.getsize(pathToFile) >> 20))
                                                newMessage.size = sizeOfFileMB
                                
                                                print "Path to File:\n\t\t%s\nSize of File:\n\t\t%sMB" % (pathToFile, newMessage.size)
                                
                                                if (os.path.isfile(pathToFile)):
                                                        #FILE EXISTS
                                                        SendMessage = {}
                                                        SendMessage["message"] = "encryption"
                                                        SendMessage["action"] = "start"
                                                        SendMessage = json.dumps(SendMessage)
                                                        #print SendMessage
                                                        #print "\n"
                                                        
                                                        client_sock.send(SendMessage)
                                
                                                        createLogsSF(newMessage.typeFile, newMessage.cipher, newMessage.nameFile, newMessage.size, newMessage.hsm)
                                                        #print "Here is where its Log will be saved: ", logsFile
                                                        
                                                        fileRunning = "encrypter"
                                
                                                        me = subprocess.Popen(["./monitoring.sh", logsFile, fileRunning, newMessage.size, newMessage.hsm])
                                
                                                        time.sleep(1)
                                
                                                        #ARGS: (nameFile, typeFile, password, HSM, cipher, pathToFile)
                                                        #print newMessage.nameFile
                                                        #print newMessage.typeFile
                                                        #print newMessage.password
                                                        #print newMessage.hsm
                                                        #print newMessage.cipher
                                
                                                        pathToSaveFile = str("%s/%s" % (pathToEncrypt, newMessage.typeFile))

                                                        if (newMessage.hsm == "True"):
                                                                #ARGS: (nameFile, typeFile, password, HSM, cipher, pathToFile, pathToSaveFile)
                                                                eh = subprocess.Popen(["sudo","python","hsmEncrypter.py", newMessage.nameFile,newMessage.typeFile,newMessage.password,newMessage.hsm,newMessage.cipher,pathToFile,pathToSaveFile])
                                                                #print eh.communicate()[0]
                                                                eh.wait()
                                                                returncode = eh.returncode						

                                                        else:
                                                                #ARGS: (nameFile, typeFile, password, HSM, cipher, pathToFile, pathToSaveFile)
                                                                e = subprocess.Popen(["./encrypter.sh",newMessage.nameFile,newMessage.typeFile,newMessage.password,newMessage.hsm,newMessage.cipher,pathToFile,pathToSaveFile])
                                                                #print e.communicate()[0]
                                                                e.wait()
                                                                returncode = e.returncode		
                                
                                                        if (returncode == 0):
                                                                #Wait for ending of monitoring
                                                                me.wait()

                                                                time.sleep(3)

                                                                print "File has been encrypted successfully"
                                
                                                                SendMessage = {}
                                                                SendMessage["message"] = "encryption"
                                                                SendMessage["action"] = "end"
                                                                SendMessage = json.dumps(SendMessage)
                                                                #print SendMessage
                                                                #print "\n"
                                
                                                                client_sock.send(SendMessage)
                                                                
                                                                fileRunning = "decrypter"

                                                                md = subprocess.Popen(["./monitoring.sh", logsFile, fileRunning, newMessage.size, newMessage.hsm])
                                                                #print "Here is where its Log will be saved: ", logsFile
                                
                                                                time.sleep(1)
                                
                                                                SendMessage = {}
                                                                SendMessage["message"] = "decryption"
                                                                SendMessage["action"] = "start"
                                                                SendMessage = json.dumps(SendMessage)
                                                                #print SendMessage
                                                                #print "\n"
                                
                                                                client_sock.send(SendMessage)
                                
                                                                
                                                                

                                                                if (newMessage.hsm == "True"):
                                                                        pathToFile = str("%s/%s/zymbit/%s" % (pathToEncrypt, newMessage.typeFile, newMessage.nameFile))
                                                                        pathToSaveFile = str("%s/%s" % (pathToDecrypt, newMessage.typeFile))
                                                                        
                                                                        #ARGS: (nameFile, typeFile, password, HSM, cipher, pathToFile, pathToSaveFile)
                                                                        eh = subprocess.Popen(["sudo","python","hsmDecrypter.py", newMessage.nameFile,newMessage.typeFile,newMessage.password,newMessage.hsm,newMessage.cipher,pathToFile,pathToSaveFile])
                                                                        #print eh.communicate()[0]
                                                                        eh.wait()
                                                                        returncode = eh.returncode						

                                                                else:
                                                                        pathToFile = str("%s/%s/%s/%s" % (pathToEncrypt, newMessage.typeFile, newMessage.cipher, newMessage.nameFile))
                                                                        pathToSaveFile = str("%s/%s" % (pathToDecrypt, newMessage.typeFile))

                                                                        #ARGS: (nameFile, typeFile, password, hsm, cipher, pathToFile, pathToSaveFile)
                                                                        d = subprocess.Popen(["./decrypter.sh",newMessage.nameFile,newMessage.typeFile,newMessage.password,newMessage.hsm,newMessage.cipher,pathToFile,pathToSaveFile])
                                                                        #print d.communicate()[0]
                                                                        d.wait()
                                                                        returncode = d.returncode
                                                                
                                                                if (returncode == 0):
                                                                        #Wait for ending of monitoring
                                                                        md.wait()
                                                                        
                                                                        print "File has been decrypted successfully"
                                
                                                                        SendMessage = {}
                                                                        SendMessage["message"] = "decryption"
                                                                        SendMessage["action"] = "end"
                                                                        SendMessage = json.dumps(SendMessage)
                                                                        #print SendMessage
                                                                        #print "\n"
                                
                                                                        client_sock.send(SendMessage)
                                
                                                                        #Calculate %CPU & %MEM used
                                                                        calc = subprocess.Popen(["./calculator.sh", logsFile, newMessage.nameFile, newMessage.size])
                                                                        calc.wait()
                                
                                                                        time.sleep(1)
                                
                                                                        fileTemporal = open("temporal.txt", "r")
                                                                        dataTemporal = fileTemporal.read()
                                                                        
                                                                        fileLogsFile = open(logsFile, "a")
                                                                        fileLogsFile.write(dataTemporal)
                                                                        fileLogsFile.close()
                                                                        print dataTemporal
                                
                                                                        dataTemporal = dataTemporal.replace("\n", "").split(';')
                                                                        #print dataTemporal

                                                                        infoForType = {}							

                                                                        for info in dataTemporal:
                                                                                aux = info.split("=")

                                                                                #print aux
                                                                                if (aux[0] == "type"):
                                                                                        auxType = aux[1]

                                                                                if (aux[0] == "new"):
                                                                                        SendMessage = {}
                                                                                        SendMessage["message"] = auxType
                                                                                        SendMessage["result"] = infoForType
                                                                                        SendMessage = json.dumps(SendMessage)
                                                                                        print SendMessage
                                                                                        print "\n"
                                                                                        
                                                                                        client_sock.send(SendMessage)
                                                                                        time.sleep(1)

                                                                                else:
                                                                                        #print "%s %s" % (aux[0], aux[1])
                                                                                        infoForType[aux[0]] = aux[1]

                                                                        fileTemporal.close()

                                                                        
                                                                        #Delete collections after send them
                                                                        del infoForType
                                
                                                                        if os.path.isfile("temporal.txt"):
                                                                                os.remove("temporal.txt")
                                                                        else:
                                                                                print "Error: 'temporal.txt' not found"
                                
                                                                else:
                                                                        SendMessage = {}
                                                                        SendMessage["message"] = "decryption"
                                                                        SendMessage["error"] = "error"
                                                                        SendMessage["body"] = "Error during the decryption."
                                                                        SendMessage = json.dumps(SendMessage)
                                                                        #print SendMessage
                                                                        #print "\n"
                                
                                                                        print "Error during the decryption has ocurred..."
                                                                        client_sock.send(SendMessage)
                                                        else:
                                                                SendMessage = {}
                                                                SendMessage["message"] = "encryption"
                                                                SendMessage["error"] = "error"
                                                                SendMessage["body"] = "Error during the encryption."
                                                                SendMessage = json.dumps(SendMessage)
                                                                #print SendMessage
                                                                #print "\n"
                                
                                                                print "Error during the encryption has ocurred..."
                                                                client_sock.send(SendMessage)
                                                else:
                                                        SendMessage = {}
                                                        SendMessage["message"] = "encryption"
                                                        SendMessage["error"] = "error"
                                                        SendMessage["body"] = "File not found."
                                                        SendMessage = json.dumps(SendMessage)
                                                        #print SendMessage
                                                        #print "\n"
                                
                                                        print "File does not exist!"
                                                        client_sock.send(SendMessage)


                        elif optionFunction == 'random':
                                print "Option RANDOM has been choosen"
                                while(noEnd):
                                        noEnd = True
                                        
                                        print "Waiting to recieve the message from device ", client_info
                                        print "\n"

                                        data = client_sock.recv(1024)
                                        if len(data) == 0: break

                                        #Data to JSON
                                        jsonMessage = json.loads(data)
                                        print "jsonMessage:\n%s" % jsonMessage

                                        if jsonMessage.get("message") == 'endFunction':
                                                print "End of Function has been selected"
                                                noEnd = False

                                        elif jsonMessage.get("RANDOM") == 'GENERATE':
                                                randomFileSize = jsonMessage.get("size")
                                                randomFileName = jsonMessage.get("name")
                                                print "\nChecking %s characteristics..." % (randomFileName)
                                                if (randomFileSize <= recommendedMinRandomFileSize):
                                                        print "\nWARING!\n%s Kilobytes is not big enough, it will be changed to 1024 Kilobytes" % (randomFileSize)
                                                        randomFileSize = 1024

                                                elif (randomFileSize > recommendedMaxRandomFileSize):
                                                        print "\nWARNING!\n%s Kilobytes is too big, and it is not recommeded due to HSM's speed characteristics, it will be changed to 1024 Kilobytes" % (randomFileSize)
                                                        randomFileSize = 1024

                                                print "\nBegin of operation, creating file %s KB" % randomFileSize
                                                pathToNewRandomGenerated = str("%s/%s" % (pathToRandomGenerated, randomFileName))
                                                print "\nIt will be stored at: %s" % pathToNewRandomGenerated

                                                if (os.path.isfile(pathToNewRandomGenerated)):
                                                        #FILE EXISTS
                                                        print "File already exists, deleting it..."

                                                        if os.path.isfile(pathToNewRandomGenerated):
                                                                                os.remove(pathToNewRandomGenerated)
                                                randomFileSize = randomFileSize * 1024
                                                print "Generating the file with %s bytes, please wait..." % (randomFileSize)

                                                SendMessage = {}
                                                SendMessage["RANDOMOperation"] = "started"
                                                SendMessage = json.dumps(SendMessage)
                                                SendMessage = str("{'RANDOM': %s}" % (SendMessage))
                                                print SendMessage
                                                print "\n"        
                                                client_sock.send(SendMessage)
                                                
                                                zymkey.client.create_random_file(pathToNewRandomGenerated,randomFileSize)
                                                print "End of operation. File has been generated correctly!"
                                                
                                                SendMessage = {}
                                                SendMessage["RANDOMOperation"] = "ended"
                                                SendMessage = json.dumps(SendMessage)
                                                SendMessage = str("{'RANDOM': %s}" % (SendMessage))
                                                print SendMessage
                                                print "\n"        
                                                client_sock.send(SendMessage)

                        elif optionFunction == 'signatures':
                                print "Option Signatures has been choosen"
                                while(noEnd):
                                        noEnd = True
                                        
                                        print "Waiting to recieve the message from device ", client_info
                                        print "\n"

                                        data = client_sock.recv(1024)
                                        if len(data) == 0: break

                                        #Data to JSON
                                        jsonMessage = json.loads(data)
                                        print "jsonMessage:\n%s" % jsonMessage

                                        if jsonMessage.get("message") == 'endFunction':
                                                print "End of Function has been selected"
                                                noEnd = False



                        elif optionFunction == 'ecdsa':
                                print "Option ECDSA has been choosen"
                                while(noEnd):
                                        noEnd = True
                                        
                                        print "Waiting to recieve the message from device ", client_info
                                        print "\n"

                                        data = client_sock.recv(1024)
                                        if len(data) == 0: break

                                        #Data to JSON
                                        jsonMessage = json.loads(data)
                                        print "jsonMessage:\n%s" % jsonMessage

                                        if jsonMessage.get("message") == 'endFunction':
                                                print "End of Function has been selected"
                                                noEnd = False



                        elif optionFunction == 'i2c':
                                print "Option I2C has been choosen"
                                while(noEnd):
                                        noEnd = True

                                        SendMessage = {}
                                        SendMessage["I2CCurrentAddress"] = str("%s" % (i2cAddress))
                                        SendMessage = json.dumps(SendMessage)
                                        SendMessage = str("{'I2C': %s}" % (SendMessage))
                                        print SendMessage
                                        print "\n"        
                                        client_sock.send(SendMessage)
                                        
                                        print "Waiting to recieve the message from device ", client_info
                                        print "\n"

                                        data = client_sock.recv(1024)
                                        if len(data) == 0: break

                                        #Data to JSON
                                        jsonMessage = json.loads(data)
                                        print "jsonMessage:\n%s" % jsonMessage

                                        if jsonMessage.get("I2CNewAddress"):
                                                i2cAddress = jsonMessage.get("I2CNewAddress")
                                                zymkey.client.set_i2c_address(i2cAddress)
                                                line = subprocess.check_output(['sudo', 'tail', '-2', i2c_info_file])
                                                print line
                                                
                                        if jsonMessage.get("message") == 'endFunction':
                                                print "End of Function has been selected"
                                                noEnd = False



                        elif optionFunction == 'TAP':
                                print "Option TAP has been choosen"
                                while(noEnd):
                                        noEnd = True
                                        
                                        print "Waiting to recieve the message from device ", client_info
                                        print "\n"

                                        data = client_sock.recv(1024)
                                        if len(data) == 0: break

                                        #Data to JSON
                                        jsonMessage = json.loads(data)
                                        print "jsonMessage:\n%s" % jsonMessage


                                        if jsonMessage.get("message") == 'endFunction':
                                                print "End of Function has been selected"
                                                noEnd = False



                        else:
                                print "No possible Option"
						
		except BluetoothError:
			print "Lost connection of ", client_info
			pass
	
		except IOError:
			traceback.print_exc()
			pass
		
		print "\n\tDisconnected"

		client_sock.close()
		#server_sock.close()
	
		print "\tAll done"
		print "\tIf you want to stop server's socket, please press \'CTRL + C\'\n\n"

except KeyboardInterrupt:
	print "Thank you, goodbye!"
