# Elasticsearch Email Alerting App
This queries given keywords to Elasticsearh and sends email alerts to configured email address when a response is found for the query.
The basic usage of this app is to work as a watcher for error logs and alert the user when there are new error log entries.

## Building and Running
* Get a clone or download source from [github](https://github.com/ThejanRupasinghe/elasticsearch-alert)
* Run the Maven command ```mvn clean install``` from the root directory.
* Add the properly configured config.json ( edit the config.json in the root directory ) file to elasticsearch-alert/target directory.
* Go to the elasticsearch-alert/target directory from terminal and run ```java -jar elastic-alerting-app-1.0.jar``` . 

## Configuring config.json
```
{

  "email_details": {

    "email_host": "smtp.gmail.com",                   // Add your email host here. For gmail it's smtp.gmail.com
    "email_port": 587,                                // Add the email sending port here. For google smtp it's 587
    "email_username": "sender@example.com",           // Add your email / sender's email address here
    "email_password": "password/appkey,               // Add your email password / mail app key in gmail here
    "email_receiver_address": "receiver@example.com"  // Add the receiver's email / email address you want to receive alerts

  },

  "elasticsearch_details": {

    "host": "localhost",                              // Add the Elasticsearch host IP ( or localhost ) here
    "cluster_name": "elasticsearch"                   // Add the name of the Elasticsearch cluster you want to search
  },

  "match_list": [                                     // Add the strings you want to match here in a array
    "ERROR"
  ],

  "polling_time": 25000                               // Add the time interval you want to query in milliseconds

}
```
