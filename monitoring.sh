#!/bin/bash 

echo "Encrypt" >> /home/pi/Desktop/TFG/Logs/outputEncrypt.txt
$(top -bn40 -d 0.2 | grep encrypter.sh) >> /home/pi/Desktop/TFG/Logs/outputEncrypt.txt


exit 0