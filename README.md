# Three Plus Competition Enterer

Just a tiny app to enter you into the Three Plus (Ireland) competitions automatically.

### Prerequisites

Java 8+

### Building
You can build using SBT:
`sbt clean compile`

You can create a fat jar with:
`sbt clean assembly`

### Running

Refer above to building and creating a jar, or download a pre-built jar from the [release][f76d66c9] section.

  [f76d66c9]: https://github.com/DamienOReilly/three-plus-competition-enterer/releases "Releases"

Recommendation is to run the app via a scheduler periodically (cron, Windows Scheduler etc..) every day or every few days as it seems not all competitions are released on 1st of the month (to be confirmed).

The app expect some environment variables to be populated, that you should be able to configure in your scheduler.
- THREEPLUS_USERNAME
- THREEPLUS_PASSWORD

For example, a cronjob may look like:
```
THREEPLUS_USERNAME=0870000000
THREEPLUS_PASSWORD="mypass"
35 16 * * * /usr/bin/java -jar /home/user/apps/ThreePlusCompetitionApp-assembly-0.1.jar >> /home/user/apps/ThreePlusCompetitionApp.log 2>&1
```

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details
