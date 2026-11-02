FROM cgr.dev/chainguard/wolfi-base:latest@sha256:88a20e2d017bd20cdc0cb3910de603e7bea9512a6c351a8dd3c9df212ab03d73 AS jq

RUN apk add --no-cache jq

FROM cgr.dev/chainguard/bash:latest@sha256:2b851eb58be129bc411c716283085864f5caabcffb9e2d17a9bc6e31e7b81f5d

COPY --from=jq /usr/bin/jq /usr/bin/jq
COPY --from=jq /usr/lib/libjq.so.1 /usr/lib/libjq.so.1
COPY --from=jq /usr/lib/libonig.so.5 /usr/lib/libonig.so.5

COPY dev-underenheter/data/*.json /data/

COPY dev-underenheter/job.sh /job.sh
RUN chmod +x /job.sh
ENTRYPOINT ["/job.sh"]
