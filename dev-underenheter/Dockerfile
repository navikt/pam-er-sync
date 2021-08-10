FROM bash:5

RUN apk add --no-cache curl
RUN apk add --no-cache jq

COPY data/*.json /data/

COPY job.sh /job.sh
RUN chmod +x /job.sh
ENTRYPOINT ["/job.sh"]
