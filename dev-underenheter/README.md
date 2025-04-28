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

## Update username and password

The app needs a username and password to create `underenheter` in the OpenSearch instance. If this password has expired, you can do the following to create a new password:

1. Create a new user with a one-year lifetime:

   `nais aiven create --access admin -s <name of the secret> -e 365 --instance stilling opensearch ignored teampam`

2. Get the new username and password from step 1:

   `nais aiven get opensearch <name of the secret> teampam`
3. Update the app's Kubernetes secret: `pam-er-sync-dev-underenheter-env`:

   `k modify-secret pam-er-sync-dev-underenheter-env`

4. Save the changes
5. Trigger a job run via Nais console and verify that it runs as expected.
