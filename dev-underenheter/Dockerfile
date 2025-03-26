FROM bash:5

RUN apk add --no-cache curl
RUN apk add --no-cache jq

COPY dev-underenheter/data/*.json /data/

COPY dev-underenheter/job.sh /job.sh
RUN chmod +x /job.sh
ENTRYPOINT ["/job.sh"]
