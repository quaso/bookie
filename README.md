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

#### to update frontend repo:
```
git submodule update --remote
```
more info: https://git-scm.com/book/en/v2/Git-Tools-Submodules


