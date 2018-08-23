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
                
class Tree:
        def __init__(self, number = '*', text = "Main Menu", parent = None, children = None, hasChildren = False):
                self.number = number
                self.text = text
                self.parent = parent
                self.children = []
                self.hasChildren = hasChildren

        def __repr__(self):
                return str("%s - %s" % (self.number, self.text))

# ------------- ------------- Variables ------------- -------------# 
base_directory = "/home/pi/Desktop/TFG"                 
device_directory = "/media/pi/Transcend/TFG"

i2c_info_file = "/sys/kernel/debug/tracing/trace"
i2c_enable_tracing = "/sys/kernel/debug/tracing/events/i2c/enable"

optionFile = ""                 # Name File Option to be charged
optionFunction = ""             # Function has been choosen

noEnd = False                   # Boolean for while's switch menu
noSignatureEnd = False          # Boolean for Signature Loop
noTestEnd = False               # Boolean for TAP Test Loop

pathToResources = str("%s/%s" % (device_directory, "Resources"))
pathToRandomGenerated = str("%s/%s" % (pathToResources, "RandomGenerated"))
pathToEncrypt = str("%s/%s" % (device_directory, "Encrypt"))
pathToDecrypt = str("%s/%s" % (device_directory, "Decrypt"))
pathToLogs = str("%s/%s" % (base_directory, "Logs"))
pathToSignature = str("%s/%s" % (device_directory, "Signature"))
pathToSignedMessages = str("%s/%s" % (pathToSignature, "Messages"))
pathToSignatures = str("%s/%s" % (pathToSignature, "Signatures"))

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

num_signatures = 0

# ------------- ------------- Recommended Variables HSM ------------- -------------#
msOn = 0                                        # Amount of miliseconds LED will be ON
msOff = 0                                       # Amount of miliseconds LED will be OFF
numFlashes = 0                                  # Number of Flashes it will do
i2cAddress = '0x30'                             # I2C default address
tapSensibility = 50                             # TAP's default sensibility
tapSensibilityAxisX = 0                         # TAP's Axis X sensibility
tapSensibilityAxisY = 0                         # TAP's Axis Y sensibility
tapSensibilityAxisZ = 0                         # TAP's Axis Z sensibility
accelerometer_result = []                       # Empty array where it would be the TAP infromation with function get_accelerometer_data
timeTAPTest = 0                                 # Time in milliseconds for TAP Test
recommendedMinRandomFileSize = 0                # Recommended minimum size of Random File in Kilobytes
recommendedMaxRandomFileSize = 5120             # Recommended maximum size of Random File in Kilobytes

                ########## Dicctionaries ##########

SendMessage = {}                # Dictionary to send messages between devices
FDevice = {}                    # Dictionary to save file's information resident in the PenDrive
SFDevice = {}                   # Dictionary to send System's File recollected to the device

mainMenu = None

# ------------- ------------- Funtions ------------- -------------#
                ########## Display Main Menu ##########
def createMenu():
        global mainMenu
        mainMenu = Tree(hasChildren = True)
        RTC = Tree(0,"Get Real Time Clock (RTC)", mainMenu)
        LED = Tree(1, "LED Controller", mainMenu, hasChildren = True)
        LEDOn = Tree(1.1, "LED On", LED)
        LEDOff = Tree(1.2, "LED Off", LED)
        LEDToggle = Tree(1.3, "LED Toggle", LED)
        encryptDecrypt = Tree(2, "Encrypt and Decrypt files", mainMenu, hasChildren = True)
        details = Tree(2.1, "See Operation's details", encryptDecrypt)
        random = Tree(3, "Generate RANDOM files", mainMenu)
        signatures = Tree(4, "Signatures", mainMenu, hasChildren = True)
        signaturesGenerate = Tree(4.1, "Generate new Signature", signatures)
        signaturesCorrupt = Tree(4.2, "Corrupt a Signature", signatures)
        signaturesCheck = Tree(4.3, "Check a Signature", signatures)
        ecdsa = Tree(5, "ECDSA", mainMenu)
        i2c = Tree(6, "Change configuration of I2A bus", mainMenu)
        tap = Tree(7, "Configure TAP sensibility", mainMenu, hasChildren = True)
        tapTest = Tree(7.1, "Begin TAP Test", tap)

        
        LED.children.append(LEDOn)
        LED.children.append(LEDOff)
        LED.children.append(LEDToggle)

        encryptDecrypt.children.append(details)

        signatures.children.append(signaturesGenerate)
        signatures.children.append(signaturesCorrupt)
        signatures.children.append(signaturesCheck)

        tap.children.append(tapTest)
        
        mainMenu.children.append(RTC)
        mainMenu.children.append(LED)
        mainMenu.children.append(encryptDecrypt)
        mainMenu.children.append(random)
        mainMenu.children.append(signatures)
        mainMenu.children.append(ecdsa)
        mainMenu.children.append(i2c)
        mainMenu.children.append(tap)

def showMenu(position):
        print ""
        print "---------- MAIN MENU ----------"
        print ""
        if position == None:
                print "%s\t\t\t<---(Your position)" % mainMenu

                if mainMenu.hasChildren:
                        for child in mainMenu.children:
                                print "\t%s" % child 
                
        else: 
                print "%s\t\t\t<---(Your position)" % mainMenu.children[position]

                if mainMenu.children[position].hasChildren:       
                        for child in mainMenu.children[position].children:
                                print "\t%s" % child

        print ""
        print "---------- ********* ----------"
        print ""
        
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

		print "Message sent to target:\n\t%s" % SFDevice
		client_sock.send(SFDevice)
		FDevice = {}
		SFDevice = {}

                ########## Send Signatures Files to target ##########
def sendSignatures(pathToSignatures):
        signaturesDirectories = os.listdir(pathToSignatures)

        listOfSignatures = []
        
        print "\n\tSending all Signatures configuration..."
        print "\tSignatures:\n\t\t%s" % (signaturesDirectories)
                                        
        if signaturesDirectories != None:
                for signature in signaturesDirectories:
                        print signature
                        listOfSignatures.append(signature)

        
        SendMessage = {}
        SendMessage["NumberOfFiles"] = num_signatures
        SendMessage["ListOfSign"] = listOfSignatures
        SendMessage = json.dumps(SendMessage)
        SendMessage = str("{'Signatures Files': %s}" % (SendMessage))
        print "Message sent to target:\n\t%s" % SendMessage
        print "\n"        
        client_sock.send(SendMessage)


# ------------- ------------- Main ------------- ------------- #

print "\n\n\t\tWelcome to my TFG!\n\n"
zymkey.client.__init__                          # Zymkey initialization
zymkey.client.set_i2c_address(i2cAddress)       # Set I2C address to '0x30'
zymkey.client.set_tap_sensitivity('all', tapSensibility)

try:
	while True:
		print "Waiting for connection on RFCOMM channel %d " % port
		
		client_sock, client_info = server_sock.accept()

		print "Accepted connection from ", client_info

		try:
		    while True:
                        createMenu()
                        showMenu(None)

                        print "Please choose an option"
                        
                        data = client_sock.recv(1024)
                        if len(data) == 0: break

		        print "Message received from %s:\n\t[%s]\n" % (client_info, data)
	
			#Data to JSON
			jsonMessage = json.loads(data)
			#print "jsonMessage:\n%s" % jsonMessage

                        optionFunction = str("%s" % (jsonMessage.get("Function")))
			print "%s" % optionFunction
			optionNumber = int(jsonMessage.get("Number"))

                        # Enable loop boolean
                        noEnd = True
                        noSignatureEnd = True
                        noTestEnd = True
                        
                        ############# Enormous Switch Case #############

                        if optionFunction == 'RTC':
                                print "Option RTC has been choosen"

                                showMenu(optionNumber)
                                
                                while (noEnd) :
                                        noEnd = True

                                        SendMessage = {}
                                        SendMessage["RTCOperation"] = "started"
                                        SendMessage = json.dumps(SendMessage)
                                        SendMessage = str("{'RTC': %s}" % (SendMessage))
                                        print "Message sent to target:\n\t%s" % SendMessage
                                        print "\n"        
                                        client_sock.send(SendMessage)

                                        timeNotPrecise = zymkey.client.get_time(False)
                                        timePrecise = zymkey.client.get_time(True)
                                        
                                        SendMessage = {}
                                        SendMessage["RTCOperation"] = "results"
                                        SendMessage["RTCNotPreciseTime"] = timeNotPrecise
                                        SendMessage["RTCPreciseTime"] = timePrecise
                                        SendMessage = json.dumps(SendMessage)
                                        SendMessage = str("{'RTC': %s}" % (SendMessage))
                                        print "Message sent to target:\n\t%s" % SendMessage
                                        print "\n"        
                                        client_sock.send(SendMessage)

                                        time.sleep(0.5)

                                        SendMessage = {}
                                        SendMessage["RTCOperation"] = "ended"
                                        SendMessage = json.dumps(SendMessage)
                                        SendMessage = str("{'RTC': %s}" % (SendMessage))
                                        print "Message sent to target:\n\t%s" % SendMessage
                                        print "\n"        
                                        client_sock.send(SendMessage)

                                        print "Waiting to recieve order from device", client_info
                                        print "\n"

                                        data = client_sock.recv(1024)
                                        if len(data) == 0: break

                                        #Data to JSON
                                        jsonMessage = json.loads(data)
                                        print "Message recieved from device:\n\t%s" % jsonMessage
                                        print ""

                                        if jsonMessage.get("message") == 'endFunction':
                                                print "End of Function has been selected"
                                                print "Retruning to Main Menu"
                                                noEnd = False

                                                
			
                        elif optionFunction == 'LED':
                                print "Option LED has been choosen"

                                showMenu(optionNumber)
                                
                                while (noEnd) :
                                        noEnd = True

                                        print "Waiting to recieve LED's configuration from device ", client_info
                                        print "\n"

                                        data = client_sock.recv(1024)
                                        if len(data) == 0: break

                                        #Data to JSON
                                        jsonMessage = json.loads(data)
                                        print "Message received from device:\n\t%s" % jsonMessage
                                        print ""

                                        if jsonMessage.get("message") == 'endFunction':
                                                print "End of Function has been selected"
                                                print "Returning to Main Menu"
                                                noEnd = False

                                        elif jsonMessage.get("LED") == 'ON':
                                                print "Switching on the LED"
                                                zymkey.client.led_on()

                                        elif jsonMessage.get("LED") == 'OFF':
                                                print "Switching off the LED"
                                                zymkey.client.led_off()
                                                               
                                        elif jsonMessage.get("LED") == 'TRIGGER':
                                                msOn = jsonMessage.get("msOn")
                                                msOff = jsonMessage.get("msOff")
                                                numFlashes = jsonMessage.get("numFlashes")
                                                print "Triggering the LED %s times" % numFlashes

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

                                showMenu(optionNumber)
                                
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

                                        elif jsonMessage.get("encrypt_decrypt") == 'refresh':
                                                #print "Resending System Files"
                                                #print "\tCollecting information of files from: ", pathToResources
                                                FDevice = {}
                                                SFDevice = {}

                                                SendMessage = {}
                                                SendMessage["refreshOperation"] = "started"
                                                SendMessage = json.dumps(SendMessage)
                                                SendMessage = str("{'encrypt_decrypt': %s}" % (SendMessage))
                                                print "Message sent to target:\n\t%s" % SendMessage
                                                print "\n"
                                                client_sock.send(SendMessage)

                                                sendSF(pathToResources,FDevice,SFDevice)

                                                SendMessage = {}
                                                SendMessage["refreshOperation"] = "ended"
                                                SendMessage = json.dumps(SendMessage)
                                                SendMessage = str("{'encrypt_decrypt': %s}" % (SendMessage))
                                                print "Message sent to target:\n\t%s" % SendMessage
                                                print "\n"
                                                client_sock.send(SendMessage)
                                                        
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
                                                        SendMessage["operationEncryption"] = "started"
                                                        SendMessage = json.dumps(SendMessage)
                                                        SendMessage = str("{'encrypt_decrypt': %s}" % (SendMessage))
                                                        print "Message sent to target:\n\t%s" % SendMessage
                                                        print "\n"
                                                        
                                                        client_sock.send(SendMessage)
                                
                                                        createLogsSF(newMessage.typeFile, newMessage.cipher, newMessage.nameFile, newMessage.size, newMessage.hsm)
                                                        #print "Here is where its Log will be saved: ", logsFile
                                                        
                                                        fileRunning = "encrypter"
                                
                                                        me = subprocess.Popen(["./monitoring.sh", logsFile, fileRunning, newMessage.size, newMessage.hsm])
                                
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
                                                                SendMessage["operationEncryption"] = "ended"
                                                                SendMessage = json.dumps(SendMessage)
                                                                SendMessage = str("{'encrypt_decrypt': %s}" % (SendMessage))
                                                                print "Message sent to target:\n\t%s" % SendMessage
                                                                print "\n"
                                
                                                                client_sock.send(SendMessage)
                                                                
                                                                fileRunning = "decrypter"

                                                                md = subprocess.Popen(["./monitoring.sh", logsFile, fileRunning, newMessage.size, newMessage.hsm])
                                                                #print "Here is where its Log will be saved: ", logsFile
                                
                                                                SendMessage = {}
                                                                SendMessage["operationDecryption"] = "started"
                                                                SendMessage = json.dumps(SendMessage)
                                                                SendMessage = str("{'encrypt_decrypt': %s}" % (SendMessage))
                                                                print "Message sent to target:\n\t%s" % SendMessage
                                                                print "\n"
                                
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
                                                                        SendMessage["operationDecryption"] = "ended"
                                                                        SendMessage = json.dumps(SendMessage)
                                                                        SendMessage = str("{'encrypt_decrypt': %s}" % (SendMessage))
                                                                        print "Message sent to target:\n\t%s" % SendMessage
                                                                        print "\n"
                                
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
                                                                        #print dataTemporal
                                
                                                                        dataTemporal = dataTemporal.replace("\n", "").split(';')
                                                                        print dataTemporal

                                                                        infoForType = {}							

                                                                        for info in dataTemporal:
                                                                                aux = info.split("=")

                                                                                #print aux
                                                                                if (aux[0] == "type"):
                                                                                        auxType = aux[1]
                                                                                        auxType = str("%sResults" % (auxType))

                                                                                elif (aux[0] == "values"):
                                                                                        auxValues = aux[1]

                                                                                elif (aux[0] == "send"):
                                                                                        SendMessage = {}
                                                                                        SendMessage[auxType] = auxValues
                                                                                        SendMessage["values"] = infoForType
                                                                                        SendMessage = json.dumps(SendMessage)
                                                                                        SendMessage = str("{'encrypt_decrypt': %s}" % (SendMessage))
                                                                                        print "Message sent to target:\n\t%s" % SendMessage
                                                                                        print "\n"
                                                                                        client_sock.send(SendMessage)

                                                                                        infoForType = {}
                                                                                        
                                                                                else:
                                                                                        #print "aux 0: %s, aux 1: %s" % (aux[0], aux[1])
                                                                                        infoForType[aux[0]] = aux[1]

                                                                        fileTemporal.close()

                                                                        
                                                                        #Delete collections after send them
                                                                        del infoForType
                                
                                                                        if os.path.isfile("temporal.txt"):
                                                                                print ""
                                                                                os.remove("temporal.txt")
                                                                        else:
                                                                                print "Error: 'temporal.txt' not found"
                                
                                                                else:
                                                                        SendMessage = {}
                                                                        SendMessage["message"] = "decryption"
                                                                        SendMessage["error"] = "error"
                                                                        SendMessage["body"] = "Error during the decryption."
                                                                        SendMessage = json.dumps(SendMessage)
                                                                        SendMessage = str("{'encrypt_decrypt': %s}" % (SendMessage))
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
                                                                SendMessage = str("{'encrypt_decrypt': %s}" % (SendMessage))
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
                                                        SendMessage = str("{'encrypt_decrypt': %s}" % (SendMessage))
                                                        #print SendMessage
                                                        #print "\n"
                                
                                                        print "File does not exist!"
                                                        client_sock.send(SendMessage)


                        elif optionFunction == 'random':
                                print "Option RANDOM has been choosen"

                                showMenu(optionNumber)
                                
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
                                                print "Message sent to target:\n\t%s" % SendMessage
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

                                showMenu(optionNumber)
                                
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

                                        if jsonMessage.get("Signatures") == 'Generate':
                                                while(noSignatureEnd):
                                                        noSignatureEnd = True

                                                        print "Waiting to recieve information in order to generate signature from device ", client_info
                                                        print "\n"

                                                        data = client_sock.recv(1024)
                                                        if len(data) == 0: break

                                                        #Data to JSON
                                                        jsonMessage = json.loads(data)
                                                        print "jsonMessage:\n%s" % jsonMessage

                                                        if jsonMessage.get("message") == 'endFunction':
                                                                print "End of Function has been selected"
                                                                noSignatureEnd = False

                                                        if jsonMessage.get("SignaturesGenerate") == 'Generate':

                                                                SendMessage = {}
                                                                SendMessage["operationSignaturesGenerateStatus"] = "started"
                                                                SendMessage = json.dumps(SendMessage)
                                                                SendMessage = str("{'signatures': %s}" % (SendMessage))
                                                                print "Message sent to target:\n\t%s" % SendMessage
                                                                print "\n"
                                                                client_sock.send(SendMessage)
                                                                
                                                                signatureTitle = str("%s" % (jsonMessage.get("Title")))
                                                                signatureMessage = str("%s" % (jsonMessage.get("Message")))
                                                                auxPathToMessage = str("%s/%s.txt" % (pathToSignedMessages,signatureTitle))
                                                                auxPathToSignature = str("%s/%s.txt" % (pathToSignatures,signatureTitle))

                                                                print auxPathToMessage
                                                                print auxPathToSignature
                                                                
                                                                if (os.path.isfile(auxPathToMessage)):
                                                                        try:
                                                                                print "WARNING! Message and Signature already exist"
                                                                                print "Removing the message..."
                                                                                os.remove(auxPathToMessage)
                                                                                print "Done"
                                                                                print "Removing its signature..."
                                                                                os.remove(auxPathToSignature)
                                                                                print "Done"
                                                                                num_signatures = num_signatures - 1

                                                                        except OSError:
                                                                                print "Some files didn't exist, but no problem it carries on"
                                                                        
                                                                print "Creating message and signing it"
                                                                messageSignature = open(auxPathToMessage, "w+")
                                                                messageSignature.write(signatureMessage)
                                                                messageSignature.close()

                                                                newSignature = zymkey.client.sign(auxPathToMessage)

                                                                signatureOfMessage = open(auxPathToSignature, "w+")
                                                                signatureOfMessage.write(newSignature)
                                                                signatureOfMessage.close()

                                                                num_signatures = num_signatures + 1
                                                                
                                                                print "Message created and signed successfully"

                                                                SendMessage = {}
                                                                SendMessage["operationSignaturesGenerateStatus"] = "ended"
                                                                SendMessage = json.dumps(SendMessage)
                                                                SendMessage = str("{'signatures': %s}" % (SendMessage))
                                                                print "Message sent to target:\n\t%s" % SendMessage
                                                                print "\n"
                                                                client_sock.send(SendMessage)

                                                                                       
                                        elif jsonMessage.get("Signatures") == 'Corrupt':
                                                while(noSignatureEnd):
                                                        noSignatureEnd = True

                                                        print "Waiting to recieve the information in order to corrupt signature from device ", client_info
                                                        print "\n"

                                                        data = client_sock.recv(1024)
                                                        if len(data) == 0: break

                                                        #Data to JSON
                                                        jsonMessage = json.loads(data)
                                                        print "jsonMessage:\n%s" % jsonMessage

                                                        if jsonMessage.get("message") == 'endFunction':
                                                                print "End of Function has been selected"
                                                                noSignatureEnd = False


                                        elif jsonMessage.get("Signatures") == 'Check':
                                                while(noSignatureEnd):
                                                        noSignatureEnd = True

                                                        sendSignatures(pathToSignatures)

                                                        print "Waiting to recieve information in order to check signature from device ", client_info
                                                        print "\n"

                                                        data = client_sock.recv(1024)
                                                        if len(data) == 0: break

                                                        #Data to JSON
                                                        jsonMessage = json.loads(data)
                                                        print "jsonMessage:\n%s" % jsonMessage

                                                        if jsonMessage.get("message") == 'endFunction':
                                                                print "End of Function has been selected"
                                                                noSignatureEnd = False


                        elif optionFunction == 'ecdsa':
                                print "Option ECDSA has been choosen"

                                showMenu(optionNumber)
                                
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

                                showMenu(optionNumber)
                                
                                e = subprocess.Popen(["./i2c.sh"])
                                #print e.communicate()[0]
                                e.wait()
                                returncode = e.returncode
                                
                                #subprocess.check_output(['sudo','echo','1','>',i2c_enable_tracing])
                                line = subprocess.check_output(['sudo', 'cat', i2c_enable_tracing])
                                print line

                                while(noEnd):
                                        noEnd = True

                                        SendMessage = {}
                                        SendMessage["I2CCurrentAddress"] = str("%s" % (i2cAddress))
                                        SendMessage = json.dumps(SendMessage)
                                        SendMessage = str("{'I2C': %s}" % (SendMessage))
                                        print "Message sent to target:\n\t%s" % SendMessage
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
                                                line = subprocess.check_output(['gpio','readall'])
                                                print line
                                                line = subprocess.check_output(['sudo', 'tail', '-2', i2c_info_file])
                                                print line
                                                
                                        if jsonMessage.get("message") == 'endFunction':
                                                print "End of Function has been selected"
                                                noEnd = False


                        elif optionFunction == 'TAP':
                                print "Option TAP has been choosen"

                                showMenu(optionNumber)
                                
                                while(noEnd):
                                        noEnd = True

                                        SendMessage = {}
                                        SendMessage["TAPCurrentGlobalSensibility"] = str("%s" % (tapSensibility))
                                        SendMessage = json.dumps(SendMessage)
                                        SendMessage = str("{'TAP': %s}" % (SendMessage))
                                        print "Message sent to target:\n\t%s" % SendMessage
                                        print "\n"        
                                        client_sock.send(SendMessage)

                                        SendMessage = {}
                                        SendMessage["TAPCurrentAxisXSensibility"] = str("%s" % (tapSensibilityAxisX))
                                        SendMessage["TAPCurrentAxisYSensibility"] = str("%s" % (tapSensibilityAxisY))
                                        SendMessage["TAPCurrentAxisZSensibility"] = str("%s" % (tapSensibilityAxisZ))
                                        SendMessage = json.dumps(SendMessage)
                                        SendMessage = str("{'TAP': %s}" % (SendMessage))
                                        print "Message sent to target:\n\t%s" % SendMessage
                                        print "\n"        
                                        client_sock.send(SendMessage)
                                        
                                        print "Waiting to recieve the new TAP sensibility configuration from device ", client_info
                                        print "\n"

                                        data = client_sock.recv(1024)
                                        if len(data) == 0: break

                                        #Data to JSON
                                        jsonMessage = json.loads(data)
                                        print "jsonMessage:\n%s" % jsonMessage

                                        if jsonMessage.get("TAP") == "TAPGlobalSensibility":
                                                tapSensibility = int(jsonMessage.get("Sensibility"))
                                                zymkey.client.set_tap_sensitivity('all', tapSensibility)

                                        if jsonMessage.get("TAP") == "TAPAxisSensibility":
                                                tapSensibilityAxisX = jsonMessage.get("AxisX")
                                                tapSensibilityAxisY = jsonMessage.get("AxisY")
                                                tapSensibilityAxisZ = jsonMessage.get("AxisZ")
                                                zymkey.client.set_tap_sensitivity('x', tapSensibilityAxisX)
                                                zymkey.client.set_tap_sensitivity('y', tapSensibilityAxisY)
                                                zymkey.client.set_tap_sensitivity('z', tapSensibilityAxisZ)

                                        if jsonMessage.get("message") == 'endFunction':
                                                print "End of Function has been selected"
                                                noEnd = False

                                        if jsonMessage.get("TAP") == "TAPStartTAPTest":
                                                while(noTestEnd):
                                                        noTestEnd = True

                                                        print "Waiting to recieve the configuration for the TAP test from device ", client_info
                                                        print "\n"

                                                        data = client_sock.recv(1024)
                                                        if len(data) == 0: break

                                                        #Data to JSON
                                                        jsonMessage = json.loads(data)
                                                        print "jsonMessage:\n%s" % jsonMessage

                                                        if jsonMessage.get("TAPTest") == "TAPTestStart":
                                                                SendMessage = {}
                                                                SendMessage["TAPTestStatus"] = "started"
                                                                SendMessage = json.dumps(SendMessage)
                                                                SendMessage = str("{'TAPTest': %s}" % (SendMessage))
                                                                print "Message sent to target:\n\t%s" % SendMessage
                                                                print "\n"        
                                                                client_sock.send(SendMessage)
                                                                
                                                                timeTAPTest = int(jsonMessage.get("TAPTestSetTime"))*-1
                                                                
                                                                if timeTAPTest == 0:
                                                                        print "Time configuration would be of %s" % (timeTAPTest)
                                                                        zymkey.client.wait_for_tap()
                                                                else:
                                                                        print "Time configuration would be of %s" % (timeTAPTest)
                                                                        zymkey.client.wait_for_tap(timeout_ms = timeTAPTest)

                                                                

                                                                accelerometer_result = zymkey.client.get_accelerometer_data()
                                                                print accelerometer_result
                                                                print "Value of G force in Axis X was %s" % (accelerometer_result[0].g_force)
                                                                print "Value of TAP direction in Axis X was %s" % (accelerometer_result[0].tap_dir)
                                                                print "Value of G force in Axis Y was %s" % (accelerometer_result[1].g_force)
                                                                print "Value of TAP direction in Axis Y was %s" % (accelerometer_result[1].tap_dir)
                                                                print "Value of G force in Axis Z was %s" % (accelerometer_result[2].g_force)
                                                                print "Value of TAP direction in Axis Z was %s" % (accelerometer_result[2].tap_dir)

                                                                SendMessage = {}
                                                                SendMessage["TAPTestValues"] = "AxisX"
                                                                SendMessage["TAPTestGForce"] = accelerometer_result[0].g_force
                                                                SendMessage["TAPTestTAPDirection"] = accelerometer_result[0].tap_dir
                                                                SendMessage = json.dumps(SendMessage)
                                                                SendMessage = str("{'TAPTest': %s}" % (SendMessage))
                                                                print "Message sent to target:\n\t%s" % SendMessage
                                                                print "\n"        
                                                                client_sock.send(SendMessage)

                                                                time.sleep(0.5)

                                                                SendMessage = {}
                                                                SendMessage["TAPTestValues"] = "AxisY"
                                                                SendMessage["TAPTestGForce"] = accelerometer_result[1].g_force
                                                                SendMessage["TAPTestTAPDirection"] = accelerometer_result[1].tap_dir
                                                                SendMessage = json.dumps(SendMessage)
                                                                SendMessage = str("{'TAPTest': %s}" % (SendMessage))
                                                                print "Message sent to target:\n\t%s" % SendMessage
                                                                print "\n"        
                                                                client_sock.send(SendMessage)

                                                                time.sleep(0.5)

                                                                SendMessage = {}
                                                                SendMessage["TAPTestValues"] = "AxisZ"
                                                                SendMessage["TAPTestGForce"] = accelerometer_result[2].g_force
                                                                SendMessage["TAPTestTAPDirection"] = accelerometer_result[2].tap_dir
                                                                SendMessage = json.dumps(SendMessage)
                                                                SendMessage = str("{'TAPTest': %s}" % (SendMessage))
                                                                print "Message sent to target:\n\t%s" % SendMessage
                                                                print "\n"        
                                                                client_sock.send(SendMessage)

                                                                time.sleep(0.5)
                                                                
                                                                SendMessage = {}
                                                                SendMessage["TAPTestStatus"] = "ended"
                                                                SendMessage = json.dumps(SendMessage)
                                                                SendMessage = str("{'TAPTest': %s}" % (SendMessage))
                                                                print "Message sent to target:\n\t%s" % SendMessage
                                                                print "\n"        
                                                                client_sock.send(SendMessage)

                                                                
                                                        if jsonMessage.get("message") == 'endFunction':
                                                                print "End of Function has been selected"
                                                                noTestEnd = False

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
