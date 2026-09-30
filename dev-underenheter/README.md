# Insert Altinn test companies for dev-gcp environment in underenhet index

Some Altinn company orgnrs that we use in our testing environment are not
available in production data downloaded from brreg.no. This is a Kubernetes job
which we can run on dev-gcp to add some of the test companies to underenhet
index in the Enhetsregisteret OpenSearch cluster periodically.

Test companies are located as Enhetsregisteret OpenSearch underenhet-formatted index documents,
one JSON file per orgnr, under `data/*.json`.

## Deploying naisjob

It is built and deployed by the `main` workflow (matrix entry `dev-underenheter`),
to dev-gcp on every push and to prod-gcp after dev on the default branch.
The Naisjob is defined in `.nais/app.yaml`, with overrides for prod-gcp in `.nais/app.prod-gcp.yaml`.
This job shall never do anything in the production environment: in prod-gcp the schedule
never fires, OpenSearch access is read-only, and `job.sh` exits immediately unless
`NAIS_CLUSTER_NAME` is `dev-gcp`.
