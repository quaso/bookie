[![Build Status](https://travis-ci.org/quaso/bookie.svg?branch=master)](https://travis-ci.org/quaso/bookie)

#### to start external HSQL database

```
hsql\runBookieDb.bat
hsql/runBookieDb.sh
```

to connect to datasource use:
```
datasource.url=jdbc:hsqldb:hsql://localhost:9001/bookie
datasource.username=sa
datasource.password=
```

#### to start the app with external HSQL database

```
java -Dspring.profiles.active=hsql -jar bookieApp-0.0.1-SNAPSHOT.jar
```

### Build with frontend
1. in maven root directory run `package` (make sure BookieFrontend module is enabled!)
2. done (either deploy on a server or run as Spring Boot app)

Warning: make sure you regularly update BookieFrontend git-submodule to have an up-to-date frontend.

### Run frontend
Navigate your browser to: http://localhost:8080/index.html?org=<ORGANIZATION_CODE>


#### to update frontend repo:
```
git submodule update --remote
```
more info: https://git-scm.com/book/en/v2/Git-Tools-Submodules


