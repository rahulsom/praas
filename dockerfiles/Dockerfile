FROM rahulsom/docker-java:7u55
MAINTAINER Rahul Somasunderam

RUN yum install -y tar unzip

WORKDIR /opt

RUN wget -q http://download.cms.gov/nppes/NPPES_Data_Dissemination_October_2016.zip && \
    mkdir NPI && \
    cd NPI && \
    unzip ../NPPES_Data_Dissemination_October_2016.zip && \
    rm ../NPPES_Data_Dissemination_October_2016.zip && \
    rm *FileHeader.csv && \
    cp *.csv small.csv

RUN source ~/.bashrc && \
    wget -q --no-check-certificate https://github.com/rahulsom/praas/archive/master.tar.gz && \
    tar xzvf master.tar.gz && \
    rm master.tar.gz && \
    cd praas-master && \
    ./grailsw compile

EXPOSE 8080

VOLUME ["/opt/praas-master/data"]

WORKDIR /opt/praas-master

ENTRYPOINT source ~/.bashrc && \
    ./grailsw run-script userScripts/Dataload.groovy && \
    ./grailsw run-app
