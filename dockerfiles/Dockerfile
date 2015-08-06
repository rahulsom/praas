FROM rahulsom/docker-java:7u55

MAINTAINER Rahul Somasunderam

RUN yum install -y tar unzip

WORKDIR /opt

RUN wget -q http://nppes.viva-it.com/NPPES_Data_Dissemination_July_2015.zip && \
    mkdir NPI && \
    cd NPI && \
    unzip ../NPPES_Data_Dissemination_July_2015.zip.zip && \
    rm *FileHeader.csv && \
    cp *.csv small.csv

RUN source ~/.bashrc && \
    wget -q --no-check-certificate https://github.com/rahulsom/praas/archive/master.tar.gz && \
    tar xzvf master.tar.gz && \
    cd praas-master && \
    ./grailsw compile

EXPOSE 8080

VOLUME ["/opt/praas-master/data"]

WORKDIR /opt/praas-master

ENTRYPOINT source ~/.bashrc && \
    ./grailsw run-script userScripts/Dataload.groovy && \
    ./grailsw run-app
