FROM cgr.dev/chainguard/wolfi-base:latest@sha256:c2a4815b6ca49b9bbafd451b4bde690d567a86b2b6b54877faac8082a7bc4296 AS jq

RUN apk add --no-cache jq

FROM cgr.dev/chainguard/bash:latest@sha256:99bf28d98b4a0671126ed9f45c779b5a12667b9d3dfb67ebcffb2d3dddfca27d

COPY --from=jq /usr/bin/jq /usr/bin/jq
COPY --from=jq /usr/lib/libjq.so.1 /usr/lib/libjq.so.1
COPY --from=jq /usr/lib/libonig.so.5 /usr/lib/libonig.so.5

COPY dev-underenheter/data/*.json /data/

COPY dev-underenheter/job.sh /job.sh
RUN chmod +x /job.sh
ENTRYPOINT ["/job.sh"]
