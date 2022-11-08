#FROM kdvolder/jdk8
FROM unitfinance/jdk17-sbt-scala

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo "Asia/Shanghai" > /etc/timezone

VOLUME /home/logs
WORKDIR /home

ADD ./target/*.jar /home/netease-music.jar

RUN chmod -x netease-music.jar

#jdk17
ENV opens_util '--add-opens java.base/java.util=ALL-UNNAMED'
ENV opens_math '--add-opens java.base/java.math=ALL-UNNAMED'
ENV opens_lang '--add-opens java.base/java.lang=ALL-UNNAMED'

ENV jvm_setting '-XX:InitialRAMPercentage=75.0 -XX:MaxRAMPercentage=75.0 -XX:MinRAMPercentage=75.0 -XX:+UseG1GC -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/home/logs'

ENTRYPOINT exec java -Dfile.encoding=utf-8 $opens_util $opens_math $opens_lang \
 -Dspring.profiles.active=$profile \
 -jar $jvm_setting /home/netease-music.jar

EXPOSE 8088
