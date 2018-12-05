# Three Plus Competition Enterer

Just a tiny app to enter you into the Three Plus (Ireland) competitions automatically.

### Prerequisites

Java 8+

### Building a jar
You can build using SBT:
`sbt clean compile`

You can create a fat jar with:
`sbt clean assembly`

### Running the jar

Refer above to building and creating a jar, or download a pre-built jar from the [release][f76d66c9] section.

  [f76d66c9]: https://github.com/DamienOReilly/three-plus-competition-enterer/releases "Releases"

The app expect some environment variables to be populated.

```
# THREEPLUS_USERNAME=<USERNAME> THREEPLUS_PASSWORD=<password> java -jar ThreePlusCompetitionApp-assembly-0.2.jar

18:05:10.538 [scala-execution-context-global-16] INFO  o.d.tpce.ThreePlusCompetitionApp$ - Successfully entered Some(Win 1 of 5 €50 Nasty Gal vouchers)
Successfully entered Some(Win 1 of 10 exclusive Kildare Village shopping experiences)
Successfully entered Some(Win a 'Ralph Breaks The Internet’ family break in Limerick Strand Hotel.)
Successfully entered Some(Win a Skinfull Affairs Vegan Hamper worth over €200)
Successfully entered Some(Win 1 of 2 €100 vouchers for The Body Shop)
Successfully entered Some(Win 1 of 2 €100 Easons.com Vouchers)
Successfully entered Some(Win a Garmin DriveSmart)
Successfully entered Some(Win a 1 year floating Flyefit membership)
Successfully entered Some(Win a Glamoriser Wireless Hair Straightener)
Successfully entered Some(Love Music? Win a pair of 3Arena tickets)
Successfully entered Some(Win the new SUCCESS collection and a €400 voucher for Melissa Curry Jewellers)
Successfully entered Some(Win a pair of IMC Cinema Tickets)
```

Recommendation is to run the app via a scheduler periodically (cron, Windows Scheduler etc..) every day or every few days as it seems not all competitions are released on 1st of the month.

For example, a cronjob may look like:
```
THREEPLUS_USERNAME=<USERNAME>
THREEPLUS_PASSWORD=<PASSWORD>
35 16 * * * /usr/bin/java -jar /home/user/apps/ThreePlusCompetitionApp-assembly-0.2.jar >> /home/user/apps/ThreePlusCompetitionApp.log 2>&1
```

### Building a docker image
This will build a very lightweight native binary using [GraalVM](https://www.graalvm.org/). The docker image is based on [alpine linux](https://hub.docker.com/r/jeanblanchard/alpine-glibc/).
```
# ./build_docker_image.sh
```

```
# docker images damo2k/threepluscompetitionenterer
REPOSITORY                           TAG                 IMAGE ID            CREATED             SIZE
damo2k/threepluscompetitionenterer   bc1c249             e59f830703ed        42 minutes ago      51.5MB
```

### Running via docker
```
# docker run --rm=true --env THREEPLUS_USERNAME=<USERNAME> --env THREEPLUS_PASSWORD=<PASSWORD> damo2k/threepluscompetitionenterer

18:05:10.538 [scala-execution-context-global-16] INFO  o.d.tpce.ThreePlusCompetitionApp$ - Successfully entered Some(Win 1 of 5 €50 Nasty Gal vouchers)
Successfully entered Some(Win 1 of 10 exclusive Kildare Village shopping experiences)
Successfully entered Some(Win a 'Ralph Breaks The Internet’ family break in Limerick Strand Hotel.)
Successfully entered Some(Win a Skinfull Affairs Vegan Hamper worth over €200)
Successfully entered Some(Win 1 of 2 €100 vouchers for The Body Shop)
Successfully entered Some(Win 1 of 2 €100 Easons.com Vouchers)
Successfully entered Some(Win a Garmin DriveSmart)
Successfully entered Some(Win a 1 year floating Flyefit membership)
Successfully entered Some(Win a Glamoriser Wireless Hair Straightener)
Successfully entered Some(Love Music? Win a pair of 3Arena tickets)
Successfully entered Some(Win the new SUCCESS collection and a €400 voucher for Melissa Curry Jewellers)
Successfully entered Some(Win a pair of IMC Cinema Tickets)
```

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details
