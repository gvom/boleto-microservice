<h1 align="left">Boleto Microservice</h1>

###

<div align="center">
  <img src="https://github-readme-stats.vercel.app/api?hide_title=false&hide_rank=false&show_icons=true&include_all_commits=true&count_private=true&disable_animations=false&theme=github_dark&locale=en&hide_border=true&username=gvom" height="150" alt="stats graph"  />
  <img src="https://github-readme-stats.vercel.app/api/top-langs?locale=pt-br&hide_title=false&layout=compact&card_width=320&langs_count=5&theme=github_dark&hide_border=true&username=gvom" height="150" alt="languages graph"  />
</div>

###

<p align="left">Exemplo de um micro serviço completo.</p>

###

<h2 align="left">Cenário</h2>

###

<p align="left">
Este repositório contém uma solução para o desafio técnico de Java Developer da Builders. O objetivo é desenvolver uma aplicação que permita ao usuário digitar um código de boleto vencido e receber os valores de juros e multas.<br>.<br>

🎯 Objetivo.<br>
O desafio consiste em construir uma aplicação que cumpra os seguintes requisitos funcionais:.<br>.<br>

Incluir um código de boleto válido.<br>
O Boleto deve estar vencido.<br>
Apenas boletos do tipo NPC podem ser calculados.<br>
Para receber as informações do boleto, consumir a API de Boletos Builders.<br>
Em caso de erro, devolver o motivo do erro.<br>
A definição dos juros de boleto bancário ocorre considerando os dias de atraso, de maneira proporcional..<br>
Taxa de juros de 1% ao mês.<br>
1% a cada 30 dias: 1÷ 30= 0,033% ao dia.<br>
A multa por atraso deve ser de 2%.<br>
O valor final do boleto deve ser:.<br>
Valor do boleto + valor da multa + valor dos juros em atraso = valor cobrado.<br>
Salvar em um banco de dados todos os cálculos realizados, para que nosso time de dados possa depois cruzar as informações de boletos pagos com boletos calculados..<br>.<br>
🚀 Funcionamento da Aplicação.<br>
A aplicação funciona através de uma API REST construída em Java, que consome a API de Boletos Builders para calcular os juros e multas de um boleto vencido..<br>
O usuário deve enviar um código de boleto válido através da rota /api/boletoservice/calc-interest, com o seguinte payload:.<br>

```
{
  "bar_code": "string",
  "payment_date": "string"
}
```

Em seguida, a API irá retornar um payload com as informações do boleto e os valores de juros e multas calculados:

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

Caso ocorra algum erro durante o processamento, a API irá retornar uma mensagem de erro com o motivo do problema.
</p>

###

<div align="left">
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/java/java-original.svg" height="40" width="52" alt="java logo"  />
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/spring/spring-original.svg" height="40" width="52" alt="spring logo"  />
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/mongodb/mongodb-original.svg" height="40" width="52" alt="mongodb logo"  />
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/tomcat/tomcat-original.svg" height="40" width="52" alt="tomcat logo"  />
</div>

###
