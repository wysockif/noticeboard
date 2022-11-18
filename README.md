
<a href="https://www.noticeboard.pl" target="_blank"><img src="https://raw.githubusercontent.com/wysockif/noticeboard/master/docs/screenshots/logo.png" alt="Noticeboard" style="height: auto !important;width: auto !important;" ></a>

![GitHub release (latest by date)](https://img.shields.io/github/v/release/wysockif/noticeboard)
[![Website shields.io](https://img.shields.io/website-up-down-green-red/http/shields.io.svg)](https://noticeboard.pl)
---

## Table of Contents
* [General information & features](#general-information-&-features)
* [Built With](#built-with)
* [Requirements](#requirements)
* [Setup & usage](#setup-&-usage)
* [Tests](#tests)
* [Screenshots](#screenshots)
* [Motivation & Project Status](#motivation-&-project-status)

---

### General information & features

Noticeboard is an advertising service, where you can:
* register (with account activation via e-mail) and login;
* create, read, update and delete a single notice;
* read, filter, search and sort pages of notices;
* change your password, profile image and delete your account;
* visit your and other users profile pages.

The project consists of a RESTful web service with Spring Boot & (responsive) single page application with React. 

<img src="https://raw.githubusercontent.com/wysockif/noticeboard/master/docs/screenshots/homepage.png" alt="drawing" style="width:1100px; margin: 10px;"/>

---

### Built with
Noticeboard is a full-stack project, so it uses a number of technologies & tools to work properly:

1. Database:
    * `PostgreSQL 13.2` - a free and open-source relational database management system;
    * `Docker 19.03.13` - an open platform for developing, shipping, and running applications;
    * `DBeaver Community 21.1.0` - a free multi-platform database tool for developers & database administrators.

2. Back-end:
    * `Java 11` - a high-level, class-based, object-oriented programming language;
    * `Spring Boot 2.4.4` - an application framework and inversion of control container for the Java platform;
    * `IntelliJ IDEA Ultimate 2021.1.2` - an integrated development environment (Java IDE) for developing computer software;
    * `Apache Maven 3.6.3` - a build automation tool used primarily for Java projects;
    * `Postman 7.36.5` - a collaboration platform for API development.

3. Front-end:
    * `JavaScript ES6` - a lightweight, interpreted, or just-in-time compiled programming language;
    * `ReactJS 17.0.2` - a JavaScript library for building user interfaces;
    * `Redux 4.0.5` - a predictable state container for JavaScript apps;
    * `Bootstrap 5.0.0` - a free and open-source CSS framework directed at responsive, mobile-first front-end web development;
    * `HTML5` - a standard markup language for documents designed to be displayed in a web browser;
    * `CSS3` - a style sheet language used for describing the presentation of a document written in a markup language;
    * `Npm 7.11.2` - a package manager for the JavaScript programming language;
    * `Webstorm 2021.1.2` -  an integrated development environment (JavaScript IDE) for developing computer software.

---

### Requirements

To run the application locally you need:

* `Docker 19.03.13`;
* `Docker-compose  1.25.0`;
* `Java 11`;
* `Apache Maven 3.6.3`;
* `Npm 7.11.2`;
* `Node 10.19.0`.

The project uses the latest version  of React. You can refer to the  <a href="https://reactjs.org/docs/react-dom.html#browser-support" target="_blank">React documentation</a> for more information about supported browsers.

---

### Setup & usage

To use the production app <a href="https://www.noticeboard.pl" target="_blank">click here</a>.

To run it locally (dev profile) follow the instructions:

0. Clone this repo to your desktop:

    ```bash
    git clone git@github.com:wysockif/noticeboard.git
    ```

1. Database:
    
    Change your directory to `noticeboard/database` and type:

    ```bash
    docker-compose up database
    ```

    to set up a database server on port 5555.

2. Back-end:

    Change your directory to `noticeboard/back-end` and run:
    ```bash
    mvn clean install
    ```
    to build a JAR file and:
    ```bash
    java -Djasypt.encryptor.password=secretKey -jar noticeboard-0.0.1-SNAPSHOT.jar
    ```
    to run it on port 8080.

    As a 'secretKey' you need to provide the key that enable decrypting the passwords in application.yml.


3. Front-end:

    Change your directory to `noticeboard/front-end` and run:
    ```bash
    npm install
    ```

    to install all the dependencies and:
    
    ```bash
    npm start
    ```

    to run the application on port 3000.


---

### Tests
The tests were created mostly in TDD by following the F.I.R.S.T. principles.

1. Back-end (JUnit 4, AssertJ):

    Change your directory to `noticeboard/back-end` and type:
    ```bash
    mvn test
    ```
    to run all (187) tests.


2. Front-end (testing-library, Jest):

    Change your directory to `noticeboard/front-end` and type:

    ```bash
    npm test
    ```

    to run all (82) tests.
---

### Screenshots

* Home page:
<img src="https://raw.githubusercontent.com/wysockif/noticeboard/master/docs/screenshots/homepage-filters.png" alt="drawing" style="width:1100px; margin: 10px;"/>

* Register page:
<img src="https://raw.githubusercontent.com/wysockif/noticeboard/master/docs/screenshots/registerpage.png" alt="drawing" style="width:1100px; margin: 10px;"/>

* Login page:
<img src="https://raw.githubusercontent.com/wysockif/noticeboard/master/docs/screenshots/loginpage.png" alt="drawing" style="width:1100px; margin: 10px;"/>

* Adding notice page:
<img src="https://raw.githubusercontent.com/wysockif/noticeboard/master/docs/screenshots/addingnoticepage.png" alt="drawing" style="width:1100px; margin: 10px;"/>

* Notice page:
<img src="https://raw.githubusercontent.com/wysockif/noticeboard/master/docs/screenshots/noticepage-ownerview.png" alt="drawing" style="width:1100px; margin: 10px;"/>

* User profile page:
<img src="https://raw.githubusercontent.com/wysockif/noticeboard/master/docs/screenshots/profilepage.png" alt="drawing" style="width:1100px; margin: 10px;"/>

* Edit user page:
<img src="https://raw.githubusercontent.com/wysockif/noticeboard/master/docs/screenshots/edituserpage.png" alt="drawing" style="width:1100px; margin: 10px;"/>


