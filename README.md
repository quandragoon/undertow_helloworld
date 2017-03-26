# undertow_helloworld
sample undertow application

to run:

```
mvn package

java -cp target/lib/*:target/hellowworld-java-ws-1.0-SNAPSHOT.jar -Djava.net.preferIPv4Stack=true -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/home/y/var/crash/cacheCrashHeap -XX:+UseConcMarkSweepGC -XX:+ParallelRefProcEnabled -XX:+UseMontgomerySquareIntrinsic -XX:+UseMontgomeryMultiplyIntrinsic -XX:+UseSquareToLenIntrinsic -XX:+UseMultiplyToLenIntrinsic helloworld.rest.UndertowServer
```
