FROM twosixlabsdart/java-fatjar-runner:latest

LABEL maintainer="john.hungerford@twosixlabs.com"

ENV SCALA_VERSION '2.12'

ENV JAVA_OPTS "-Xmx1024m -Xms1024m -XX:+UseConcMarkSweepGC"
ENV BACKEND_PUBLIC_DIR $APP_DIR/public

EXPOSE 8080

RUN apt update -y && apt install -y nodejs npm
ADD ./frontend/config-js $APP_DIR/config-js
COPY ./frontend/config-js/package.json $APP_DIR
COPY ./public $BACKEND_PUBLIC_DIR
COPY ./scripts/start-app.sh $APP_DIR
COPY ./backend/server/target/scala-$SCALA_VERSION/server-assembly-*.jar $APP_DIR

WORKDIR $APP_DIR/config-js

RUN npm install

WORKDIR $APP_DIR

RUN rm -rf public/js/config.js || :
RUN chmod -R 755 /opt/app

ENTRYPOINT ["/opt/app/start-app.sh"]
