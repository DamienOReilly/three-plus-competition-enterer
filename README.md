# Three Plus Competition Enterer

Just a tiny app to enter you into the Three Plus (Ireland) competitions automatically.

### Prerequisites

Java 8+

### Compile and run tests
```
# sbt clean compile test
```

### Building a fat JAR
```
# sbt assembly
```

### Building a native binary image (with GraalVM)
```
# sbt graalvm-native-image:packageBin
```

### Building a docker image
This will dockerise the [GraalVM](https://www.graalvm.org/) native binary.
The docker image is based on [alpine linux](https://hub.docker.com/_/alpine).
First build the native binary using GraalVM
```
# sbt graalvm-native-image:packageBin
```
Then you can dockerise it:
```
# docker build --tag threepluscompetitionenterer:<version> .
```

### Running the JAR

Refer above to building and creating a jar, or download a pre-built jar from the [release][f76d66c9] section.

  [f76d66c9]: https://github.com/DamienOReilly/three-plus-competition-enterer/releases "Releases"

The app expects some environment variables to be populated.

```
# THREEPLUS_USERNAME=<USERNAME> THREEPLUS_PASSWORD=<password> java -jar ThreePlusCompetitionApp.jar
```

### Running the native binary

Refer above to building and creating a native binary. The app expects some environment variables to be populated.

```
# THREEPLUS_USERNAME=<USERNAME> THREEPLUS_PASSWORD=<password> ./ThreePlusCompetitionApp
```

### Running via docker

Refer above for creating a docker image
```
# docker run --rm=true --env THREEPLUS_USERNAME=<USERNAME> --env THREEPLUS_PASSWORD=<PASSWORD> threepluscompetitionenterer
```
Alternatively you can use the publically hosted image on Docker Hub: [damo2k/threepluscompetitionenterer](https://hub.docker.com/r/damo2k/threepluscompetitionenterer)
```
# docker run --rm=true --env THREEPLUS_USERNAME=<USERNAME> --env THREEPLUS_PASSWORD=<PASSWORD> damo2k:threepluscompetitionenterer
```

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details
