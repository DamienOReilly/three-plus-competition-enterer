FROM alpine:3.13.4

COPY app/target/graalvm-native-image/ThreePlusCompetitionApp ./

ENTRYPOINT ["./ThreePlusCompetitionApp"]
