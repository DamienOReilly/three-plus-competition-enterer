FROM alpine:3.14.2

COPY app/target/graalvm-native-image/ThreePlusCompetitionApp ./

ENTRYPOINT ["./ThreePlusCompetitionApp"]
