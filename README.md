<h1 align="left">Boleto Microservice</h1>
<div align="center">
  <img src="https://github-readme-stats.vercel.app/api?hide_title=false&hide_rank=false&show_icons=true&include_all_commits=true&count_private=true&disable_animations=false&theme=github_dark&locale=en&hide_border=true&username=gvom" height="150" alt="stats graph"  />
  <img src="https://github-readme-stats.vercel.app/api/top-langs?locale=pt-br&hide_title=false&layout=compact&card_width=320&langs_count=5&theme=github_dark&hide_border=true&username=gvom" height="150" alt="languages graph"  />
</div>
<p align="left">Java Developer Technical Challenge from Builders</p>
<h2 align="left">Scenario</h2>
<p align="left">
This repository contains a solution for the Java Developer Technical Challenge from Builders. The goal is to develop an application that allows the user to enter an expired boleto code and receive interest and penalty values.<br><br>
Prerequisites<br>
To run and test the project, it is necessary to have Java 17 and Maven installed on your machine. It is also necessary to have a MongoDB database installed and configured. In addition, the variable.properties file must be configured in the src/main/resources folder.<br><br>

Running the project<br>
• Clone this repository and open the project in your preferred IDE.<br>
• Open the terminal and navigate to the root folder of the project.<br>
• Run the mvn clean install command to download the dependencies and compile the project.<br>
• Make sure that MongoDB is running on your machine before starting the project.<br>
• Run the mvn spring-boot:run command to start the server.<br><br>

🎯 Objective.<br>
The challenge is to build an application that meets the following functional requirements:<br><br>

• Receive a valid boleto code<br>
• Check if the boleto is expired.<br>
• Only NPC type boletos can be calculated<br>
• Consume the Builders Boletos API to receive boleto information.<br>
• In case of an error, return the reason for the error.<br>
• The definition of bank slip interest occurs considering the delay days, proportionally.<br>
• Interest rate of 1% per month<br>
• 1% every 30 days: 1 ÷ 30 = 0.033% per day<br>
• The late payment fine must be 2%<br>
• The final value of the boleto should be:<br>
• Boleto amount + fine value + delayed interest value = charged value<br>

At the end, all performed calculations are saved in the database, so that it is possible to cross-reference paid boletos with calculated boletos.<br><br>

🚀 Application Functioning.<br>
The application works through a Java-built REST API, which consumes the Builders Boletos API to calculate the interest and penalty of an expired boleto.<br>
The user must send a valid boleto code through the /api/boletoservice/calc-interest route, with the following payload:<br>

  ```
  {
    "bar_code": "string",
    "payment_date": "string"
  }
  ```

Then, the API will return a payload with boleto information and calculated interest and penalty values:

  ```
  {
    "original_amount": 0,
    "amount": 0,
    "due_date": "string",
    "payment_date": "string",
    "interest_amount_calculated": 0,
    "fine_amount_calculated": 0
  }
  ```
  
Routes:
  ```
  POST /api/boletoservice/calc-interest
  POST /api/user/updateUser
  POST /api/user/authenticate
  POST /api/user/addUser
  GET /api/user/getUser/{user-id}
  DELETE /api/user/deleteUser/{user-id}
  DOC. PAGE /swagger-ui/index.html
  ```

If an error occurs during processing, the API will return an error message with the reason for the problem.
</p>

###

<div align="left">
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/java/java-original.svg" height="40" width="52" alt="java logo"  />
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/spring/spring-original.svg" height="40" width="52" alt="spring logo"  />
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/mongodb/mongodb-original.svg" height="40" width="52" alt="mongodb logo"  />
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/tomcat/tomcat-original.svg" height="40" width="52" alt="tomcat logo"  />
</div>

###
