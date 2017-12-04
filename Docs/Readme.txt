- Instructions to install and configure any prerequisites or dependencies
Dependencies added:
         <dependency>
            <groupId>javax.mail</groupId>
            <artifactId>mail</artifactId>
            <version>1.4.1</version>
	</dependency>

	<dependency>
	    <groupId>org.springframework.boot</groupId>
	    <artifactId>spring-boot-starter-mail</artifactId>
	</dependency>

	<dependency>
	    <groupId>org.apache.commons</groupId>
	    <artifactId>commons-lang3</artifactId>
	    <version>3.4</version>
	</dependency>

- Instructions to create and initialize the database
1) Run ddl script createdb-script.sql
2) Run dml script data.sql

- Instructions to configure and prepare the source code to build and run properly
1) Download the source code
2) In the root path of the project run the command "mvn clean install"
3) In the target folder run "java -jar journals-1.0.jar"

- Assumptions you have made - it is good to explain your thought process and the assumptions you have made
To make a better test for controller and services it is good to mock the layers below.
All the tests are going deeper in the last layer and calling the database which is not desirable. 
Controller should be calling a mock service. Service should be calling a mock repository. Integration tests should be calling all of them without mocking.
I tried to do that for the file load but I had to quit since I was spending too much time on it.

- Any issues you have faced while completing the assignment
Time was the worst issue.

- Any constructive feedback for improving the assignment
The trial was excellent but more time would improve the quality of the deliveries.

