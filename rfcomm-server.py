#!/usr/bin/python

import os
from os import listdir
import os.path
from os.path import isfile, join 

import sys
import traceback
import time
import datetime

import json

import subprocess
from multiprocessing import Process
from subprocess import PIPE, Popen

from bluetooth import *

base_directory = "/home/pi/Desktop/TFG"
device_directory = "/media/pi/Transcend/TFG"

pathToResources = str("%s/%s" % (device_directory, "Resources"))
pathToEncrypt = str("%s/%s" % (device_directory, "Encrypt"))
pathToDecrypt = str("%s/%s" % (device_directory, "Decrypt"))
pathToLogs = str("%s/%s" % (base_directory, "Logs"))

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


	#Used variables
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

SFDevice = {}
FDevice = {}
SendMessage = {}

timesUsed = 0

############# Functions ############# 

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

		#print SFDevice
		client_sock.send(SFDevice)
		FDevice = {}
		SFDevice = {}

############# Main ############# 

print "\n\n\t\tWelcome to my TFG!\n\n"

try:
	while True:		
		print "Waiting for connection on RFCOMM channel %d " % port
		
		client_sock, client_info = server_sock.accept()
		print "Accepted connection from ", client_info

		#print "\tCollecting information of files from: ", pathToResources
		
		sendSF(pathToResources,FDevice,SFDevice)
			
		print "\tThe whole System File information has sent to: ", client_info
		print "\n"
		print "Please press \'REFRESH\' if there are some files you haven't received"
		print "Waiting to recieve the message from device ", client_info
		print "\n"
		
		try:
		    while True:
		        data = client_sock.recv(1024)
			
			#Clean SendMessage collection due to 'str' object does not support item assignment in python
			del SendMessage
			SendMessage = {}
	
		        if len(data) == 0: break

		        print "Message received from %s:\n\t[%s]\n" % (client_info, data)
	
			#Data to JSON
			jsonMessage = json.loads(data)
			#print "jsonMessage:\n%s" % jsonMessage
	
			if (jsonMessage.get("message")):
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
						eh = subprocess.Popen(["python","hsmEncrypter.py", newMessage.nameFile,newMessage.typeFile,newMessage.password,newMessage.hsm,newMessage.cipher,pathToFile,pathToSaveFile])
						#print eh.communicate()[0]
						eh.wait()
						returncode = eh.returncode
					else:
						#ARGS: (nameFile, typeFile, password, HSM, cipher, pathToFile, pathToSaveFile)
						e = subprocess.Popen(["./encrypter.sh",newMessage.nameFile,newMessage.typeFile,newMessage.password,newMessage.hsm,newMessage.cipher,pathToFile,pathToSaveFile])
						#print e.communicate()[0]
						e.wait()
						returncode = e.returncode
					
					time.sleep(3)
		
					if (returncode == 0):
						#Stop monitoring
						me.wait()
						
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
		
						pathToFile = str("%s/%s/%s/%s" % (pathToEncrypt, newMessage.typeFile, newMessage.cipher, newMessage.nameFile))
						pathToSaveFile = str("%s/%s" % (pathToDecrypt, newMessage.typeFile))
						
						#ARGS: (nameFile, typeFile, password, hsm, cipher, pathToFile, pathToSaveFile)
						d = subprocess.Popen(["./decrypter.sh",newMessage.nameFile,newMessage.typeFile,newMessage.password,newMessage.hsm,newMessage.cipher,pathToFile,pathToSaveFile])
						#print d.communicate()[0]
						d.wait()
						
						if (d.returncode == 0):
							#Stop monitoring
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
							calc = subprocess.Popen(["./calculator.sh", logsFile])
							calc.wait()
		
							time.sleep(1)
		
							fileTemporal = open("temporal.txt", "r")
							dataTemporal = fileTemporal.read()
		
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
		
							timesUsed = timesUsed + 1 
							
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
		
		
				print "Application has been used %s time(s)\n" % (timesUsed)
				print "---------- Ready for the %s Measurement ----------" % (timesUsed+1)
	
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