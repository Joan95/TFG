#!/usr/bin/python

import os
import os.path
import sys
import traceback
import time
import subprocess

import json

from subprocess import PIPE, Popen
from bluetooth import *

base_directory = "/home/pi/Desktop/TFG"
pathToResources = "/home/pi/Desktop/TFG/Resources"
pathToEncrypt = "/home/pi/Desktop/TFG/Encrypt"

class Message:
	def __init__(self, cipher, typeFile, nameFile, password, hsm):
		self.cipher = cipher
		self.typeFile = typeFile
		self.nameFile = nameFile
		self.password = password
		self.hsm = hsm
	
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


errorHasOccurred = "417;0"

endEncryption = "200;1"
startDecryption = "200;2"
endDecryption = "200;3"

	#Used messages
startEncryption = "200;0"
fileNotFound = "404;0"


	#Used variables
cipher = ""
typeFile = ""
nameFile = ""
password = ""
hsm = ""


while True:   
	print "Waiting for connection on RFCOMM channel %d " % port
	
	client_sock, client_info = server_sock.accept()
	print "Accepted connection from ", client_info
	
	try:
	    while True:
	        data = client_sock.recv(1024)

	        if len(data) == 0: break

		m = subprocess.Popen(["./monitoring.sh"])

	        print "received [%s]" % data

		#Data to JSON
		jsonMessage = json.loads(data)
		#print "jsonMessage:\n%s" % jsonMessage

		
		#print "\n%s" % jsonMessage["cipher"]
		#print "\n%s" % jsonMessage["typeFile"]
		#print "\n%s" % jsonMessage["nameFile"]
		#print "\n%s" % jsonMessage["password"]
		#print "\n%s" % jsonMessage["hsm"]

		cipher = str(jsonMessage["cipher"])
		typeFile = str(jsonMessage["typeFile"])
		nameFile = str(jsonMessage["nameFile"])
		password = str(jsonMessage["password"])
		hsm = str(jsonMessage["hsm"])

		newMessage = Message(cipher, typeFile, nameFile, password, hsm)


		#Checking whether file exists.
		
		pathToFile = str("%s/%s/%s" % (pathToResources, typeFile, nameFile))
		
		print pathToFile

		if (os.path.isfile(pathToFile)):
			#FILE EXISTS
			client_sock.send(startEncryption)

			#CHECK WHAT KIND OF ENCRYPTION IS WANTED
			#THEN  CALL THE CORRECT SUBPROCESS

			#ARGS: (nameFile, typeFile, password, HSM, cipher, pathToFile)
			#print newMessage.nameFile
			#print newMessage.typeFile
			#print newMessage.password
			#print newMessage.hsm
			#print newMessage.cipher


			p = subprocess.Popen(["./encrypter.sh",newMessage.nameFile,newMessage.typeFile,newMessage.password,newMessage.hsm,newMessage.cipher,pathToFile])
			#print p.communicate()[0]
			p.wait()
			
			if (p.returncode == 0):
				print "File has been encrypted successfully"
				client_sock.send(endEncryption)

				time.sleep(5)

				client_sock.send(startDecryption)

				pathToFile = str("%s/%s" % (pathToEncrypt, typeFile))
				
				#ARGS: (nameFile, typeFile, password, hsm, cipher, pathToFile)
				p = subprocess.Popen(["./decrypter.sh",newMessage.nameFile,newMessage.typeFile,newMessage.password,newMessage.hsm,newMessage.cipher,pathToFile])
				#print p.communicate()[0]
				p.wait()
				
				if (p.returncode == 0):
					print "File has been decrypted successfully"
					client_sock.send(endDecryption)
				else:
					print "Error during the decryption has ocurred..."
					client_sock.send(errorHasOccurred)
			else:
				print "Error during the encryption has ocurred..."
				client_sock.send(errorHasOccurred)
		else:
			print "File does not exist!"
			client_sock.send(fileNotFound)

	except IOError:
		traceback.print_exc()
		pass	
	
	print "disconnected"
	
	client_sock.close()
	server_sock.close()
	print "all done"
