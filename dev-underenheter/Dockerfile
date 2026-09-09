FROM cgr.dev/chainguard/wolfi-base:latest@sha256:918a593b8268c222afd4e2c4f06860ac984e60719b4697e4c71d796bc8fcd042 AS jq

RUN apk add --no-cache jq

FROM cgr.dev/chainguard/bash:latest@sha256:6250d4dc773cc3bc8852b9df8d031a4cede16b851c03014596708210dceb28bf

COPY --from=jq /usr/bin/jq /usr/bin/jq
COPY --from=jq /usr/lib/libjq.so.1 /usr/lib/libjq.so.1
COPY --from=jq /usr/lib/libonig.so.5 /usr/lib/libonig.so.5

COPY dev-underenheter/data/*.json /data/

COPY dev-underenheter/job.sh /job.sh
RUN chmod +x /job.sh
ENTRYPOINT ["/job.sh"]
