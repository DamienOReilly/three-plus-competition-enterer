FROM jeanblanchard/alpine-glibc:3.8

COPY target/threeplus target/libsunec.so ./

ENTRYPOINT ["./threeplus"]
