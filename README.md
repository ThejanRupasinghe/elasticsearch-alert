# Elasticsearch Email Alerting App
This queries given keywords to Elasticsearh and sends email alerts to configured email address when a response is found for the query.
The basic usage of this app is to work as a watcher for error logs and alert the user when there are new error log entries.

## Building and Running
* Get a clone or download source from [github](https://github.com/ThejanRupasinghe/elasticsearch-alert)
* Run the Maven command ```mvn clean install``` from the root directory.
* Add the properly configured config.json file to elasticsearch-alert/target directory.
* Go to the elasticsearch-alert/target directory from terminal and run ```java -jar elastic-alerting-app-1.0.jar``` . 
