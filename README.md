# DHBW-Project turnie.re [![Build Status](https://travis-ci.com/Malaber/webengineeringdhbw.svg?token=8gXLxT52HoJJ3uBxddBm&branch=master)](https://travis-ci.com/Malaber/webengineeringdhbw)
Do you want to play some games with some teams but are too lazy to organize a tournament?
Luckily for you, some smart students from the DHBW Karlsruhe created a website that just fits your needs!

# Development
* Download SQLite JDBC driver jar from [here](https://oss.sonatype.org/content/repositories/releases/org/xerial/sqlite-jdbc/) and put it into your `<tomcat>/lib` folder
* Add Tomcat Run configuration and make sure there are artifacts selected for deployment (IntelliJ: Tomcat Run configuration -> Deployment -> Add -> Artifact -> Add -> Web Application: Exploded -> From module -> webengineeringdhbw)
* Specify SDK for java files
* change the sqlite path to the respective absolute path to the database file on your system;  [should be here](database.sqlite)
* mvn clean & install

## Troubleshooting
#### IntelliJ can't find my Servlets
File -> Project Structure -> Modules -> Select `src/` -> Click `Mark as: Sources`
