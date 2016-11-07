PrAAS
====

*Providers as a Service* is an attempt to turn a NPI listed provider data
into a microservice that is easy to deploy, and lets developers focus on
what is important to them - build great applications around these codes.

Hacking
----

1. Chekout the code.
2. Run `./grailsw`
3. Inside the grails prompt, run `run-app`
4. Download the data files from the sources linked here.
5. Using the api console, load the data. This takes a long time.
6. Code - Save - Refresh
7. (Optional but a good practice) Send a pull request.

Deployment
----

1. Docker - TODO
```bash
docker pull rahulsom/praas
docker run -d -p 8080 -v $PWD:/opt/praas-master/data rahulsom/praas
```
2. Manual Deployment
  1. Download the war
  2. Deploy to a tomcat instance.
  3. Download the code files.
  4. Using the api console, load the data.

Data Files
----

| DataSet | Location | Status | Comments |
|---------|--------|-----|-----|
| NUCC    | [ZIP/CSV](http://www.nucc.org/index.php?option=com_content&view=article&id=107&Itemid=132) | Pending | Open Licensed by NUCC |
| NPI     | [ZIP/CSV](http://nppes.viva-it.com/NPI_Files.html) | Pending | Open Licensed by CMS |

NUCC is required for NPI to work
