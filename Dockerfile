FROM kdvolder/jdk8

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo "Asia/Shanghai" > /etc/timezone

VOLUME /home/logs
WORKDIR /home

ADD ./target/*.jar /home/netease-music.jar

RUN chmod -x netease-music.jar

ENV jvm_setting ''

ENTRYPOINT exec java -Dfile.encoding=utf-8 \
 -Dspring.profiles.active=prod \
 -jar $jvm_setting /home/netease-music.jar

EXPOSE 8088
