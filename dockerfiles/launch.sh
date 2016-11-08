#!/bin/bash
source ~/.bashrc

cd /opt/NPI
unzip ../NPPES_Data_Dissemination_October_2016.zip
rm ../NPPES_Data_Dissemination_October_2016.zip
rm *FileHeader.csv
cp *.csv small.csv

cd /opt/praas-master

./grailsw compile
./grailsw run-script userScripts/Dataload.groovy
./grailsw run-app
