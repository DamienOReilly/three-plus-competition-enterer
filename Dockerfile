FROM FROM alpine:3.12.0

COPY app/target/graalvm-native-image/ThreePlusCompetitionApp ./

ENTRYPOINT ["./ThreePlusCompetitionApp"]
