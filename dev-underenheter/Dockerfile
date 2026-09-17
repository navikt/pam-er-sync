FROM cgr.dev/chainguard/wolfi-base:latest@sha256:1d95114038f76513a9ace6fca107d5582b08c65981f81f61cb56bf7fd2ef216d AS jq

RUN apk add --no-cache jq

FROM cgr.dev/chainguard/bash:latest@sha256:d57efd5fbb52ca2092c6a4ef80ebe45db24bc496ad3a3103eeab15871a33dd95

COPY --from=jq /usr/bin/jq /usr/bin/jq
COPY --from=jq /usr/lib/libjq.so.1 /usr/lib/libjq.so.1
COPY --from=jq /usr/lib/libonig.so.5 /usr/lib/libonig.so.5

COPY dev-underenheter/data/*.json /data/

COPY dev-underenheter/job.sh /job.sh
RUN chmod +x /job.sh
ENTRYPOINT ["/job.sh"]
