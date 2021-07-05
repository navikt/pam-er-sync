# Insert Altinn test companies for dev-gcp environment in underenhet index

Some Altinn company orgnrs that we use in our testing environment are not
available in production data downloaded from brreg.no. This is a Kubernetes job
which we can run on dev-gcp to add some of the test companies to underenhet
index in Elastic Search stilling cluster periodically.

Test companies are located as ES stilling underenhet formatted index documents,
one JSON file per orgnr, under `data/*.json`.

## Deploying naisjob

It is built and automatically deployed to dev-gcp whenever changes are pushed to
content under this directory or any subdirectory. This job shall never run in
the production environment.

