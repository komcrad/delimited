# delimited

## cli app usage

Given some file with records having fields delimited by `,`, `|`, or ` ` (space):

    $ lein run test/resources/comma-sample


Since jvm takes awhile to load, I'd recommend using a native linux binary  

	$ lein uberjar

	$ sudo docker run -it -v $(pwd):/project --rm tenshi/graalvm-native-image -Dclojure.compiler.direct-linking=true -jar target/uberjar/delimited-0.1.0-SNAPSHOT-standalone.jar

Now you can run 

    $ ./delimited-0.1.0-SNAPSHOT-standalone test/resources/comma-sample


## REST API usage

To just run the REST API

	$ lein with-profile web run

To compile the REST API standalone jar

	$ lein with-profile web uberjar

### Interacting with the REST API

Post to the server

	$ curl -X POST localhost:8080/records -d "record=Shel, Darsey, male, Liliac, 02/22/1986"

Here's a script that will quickly populate a bunch of records into the REST API

    $ ./test/resources/populate-rest

Get all records sorted by lastname

	$ curl localhost:8080/records/name

Get all records sorted by gender

	$ curl localhost:8080/records/gender

Get all records sorted by birthdate

	$ curl localhost:8080/records/birthdate
