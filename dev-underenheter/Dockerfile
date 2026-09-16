FROM cgr.dev/chainguard/wolfi-base:latest@sha256:3754b6da0e1ccdab0fe46abfdd7bbbba994b593c28149f6659fa6597f2261aeb AS jq

RUN apk add --no-cache jq

FROM cgr.dev/chainguard/bash:latest@sha256:8825f9ffefa9f749506433992da8f0ca51c7ea320f5ec6c7129b675ede9c3c94

COPY --from=jq /usr/bin/jq /usr/bin/jq
COPY --from=jq /usr/lib/libjq.so.1 /usr/lib/libjq.so.1
COPY --from=jq /usr/lib/libonig.so.5 /usr/lib/libonig.so.5

COPY dev-underenheter/data/*.json /data/

COPY dev-underenheter/job.sh /job.sh
RUN chmod +x /job.sh
ENTRYPOINT ["/job.sh"]
