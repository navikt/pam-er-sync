FROM cgr.dev/chainguard/wolfi-base:latest@sha256:9a8d954d8f03a21bcf2be73d4628f0ad26d35c3275469925de63a34eebd58f13 AS jq

RUN apk add --no-cache jq

FROM cgr.dev/chainguard/bash:latest@sha256:17874e864d6068138e0b9d20bab7c0ceec3721d351c350266bac39330f3b1f6d

COPY --from=jq /usr/bin/jq /usr/bin/jq
COPY --from=jq /usr/lib/libjq.so.1 /usr/lib/libjq.so.1
COPY --from=jq /usr/lib/libonig.so.5 /usr/lib/libonig.so.5

COPY dev-underenheter/data/*.json /data/

COPY dev-underenheter/job.sh /job.sh
RUN chmod +x /job.sh
ENTRYPOINT ["/job.sh"]
