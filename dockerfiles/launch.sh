#!/bin/bash
if [ ! -f /opt/NPI/small.csv ]; then
    cd /opt
    wget -q http://download.cms.gov/nppes/NPPES_Data_Dissemination_October_2016.zip
    cd /opt/NPI
    unzip ../NPPES_Data_Dissemination_October_2016.zip
    rm ../NPPES_Data_Dissemination_October_2016.zip
    rm *FileHeader.csv
    cp *.csv small.csv
fi

cd /opt/praas-master

./grailsw compile
./grailsw run-script userScripts/Dataload.groovy
./grailsw run-app
