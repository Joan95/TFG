#!/usr/bin/python

import os
import os.path
import sys
import traceback
import time
import subprocess
from subprocess import PIPE, Popen
from bluetooth import *

base_directory = "/home/pi/Desktop/TFG"
pathToResources = "/home/pi/Desktop/TFG/Resources"

class Message:
	def __init__(self, cipher, body, password, hsm):
		self.cipher = cipher
		self.body = body
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

wrongFields3 = "400;3"
wrongFields2 = "400;2"
wrongFields1 = "400;1"
fileNotFound = "404;0"
errorHasOccurred = "417;0"
startEncryption = "200;0"
endEncryption = "200;1"
startDecryption = "200;2"
endDecryption = "200;3"
cipher = ""


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
		
		message = data.split(';')

		newMessage = Message(message[0], message[1], message[2], message[3])
		password = message[2]
		print newMessage


		#Checking whether file exists.
		cipher=message[0]
		pathToFile = "%s/%s" % (pathToResources, message[1])
		print pathToFile

		if (os.path.isfile(pathToFile)):
			#FILE EXISTS
			client_sock.send(startEncryption)

			#CHECK WHAT KIND OF ENCRYPTION IS WANTED
			#THEN  CALL THE CORRECT SUBPROCESS
			action='Encrypt'

			p = subprocess.Popen(["./encrypter.sh",message[1],action,password,message[3],pathToResources,cipher])
			#print p.communicate()[0]
			p.wait()
			
			if (p.returncode == 0):
				print "File has been encrypted successfully"
				client_sock.send(endEncryption)

				time.sleep(5)

				client_sock.send(startDecryption)
				action='Dencrypt'
				p = subprocess.Popen(["./decrypter.sh",message[1],action,password,message[3],pathToResources,cipher])
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
			client_sock.send("404")

	except IOError:
		traceback.print_exc()
		pass	
	
	print "disconnected"
	
	client_sock.close()
	server_sock.close()
	print "all done"
