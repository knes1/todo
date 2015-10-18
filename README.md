# Logging query counts per request in a simple Spring Boot Todo List Application

This is a minimal Spring Boot Todo List application. I've created this app as a testing ground for an idea on how
to log number of SQL queries Hibernate executes during rendering of a view (page).

Application has a request interceptor and Hibernate interceptor registered and they count of the queries executed
and log them. Count is also exposed in the model so stats can be displayed on the page itself. The details on how
this works are explained in this blog post: [Counting Queries Per Request With Hibernate And Spring](http://knes1.github.io/blog/2015/2015-07-08-counting-queries-per-request-with-hibernate-and-spring.html)

Idea is to use the stats (and display them on the page) while in development in order to quickly detect potential
performance issues with execessive query generations (such as N+1 problems).

## Logstash

Application logs it's log entries to `application.log` file. It also has a Logstash configuration to ship the log entries to a locally running instance of Elasticsearch. Logstash insists on absolute paths in config files and therefore you will need to change the absolute path entries in the `logstash.conf` in order for Logstash to work correctly. Having logs shipped to Elasticsearch with Logstash and analyzed with Kibana is described in this blog post: [
Manage Spring Boot Logs with Elasticsearch, Logstash and Kibana](http://knes1.github.io/blog/2015/2015-08-16-manage-spring-boot-logs-with-elasticsearch-kibana-and-logstash.html).


## Running

To run the app download / clone the repository and then use gradle wrapper script to run it.

Linux / Mac:

`./gradlew run`

Windows:
  
`gradlew run`

Point the browser to [localhost:8080](http://localhost:8080), click through the app and watch the logs display timings and query counts
for each request.



