FROM cgr.dev/chainguard/wolfi-base:latest@sha256:bef0f4d47edc72a93d1537eae54eb53db2b2cc352c028128ff0f16c5b5a3c1e4 AS jq

RUN apk add --no-cache jq

FROM cgr.dev/chainguard/bash:latest@sha256:ef3fce1ef2179805c04abe2ee7e542c4b7e2a4746e4f703d98a4f6adef0d8b7c

COPY --from=jq /usr/bin/jq /usr/bin/jq
COPY --from=jq /usr/lib/libjq.so.1 /usr/lib/libjq.so.1
COPY --from=jq /usr/lib/libonig.so.5 /usr/lib/libonig.so.5

COPY dev-underenheter/data/*.json /data/

COPY dev-underenheter/job.sh /job.sh
RUN chmod +x /job.sh
ENTRYPOINT ["/job.sh"]
