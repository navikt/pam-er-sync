PAM Enhetsregister Sync

Laster ned datasettet fra BRREG og indekserer til lokal en Elastic Search instance.

TODO:

# Elasticsearch on localhost Docker
If you're able to run Docker, you could run the application against your own Elasticsearch instance.
1. Create the container, as described [here](https://www.elastic.co/guide/en/elasticsearch/reference/current/docker.html) (check version yourself, :latest didn't work):
```
> docker run -d --name elastic -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" docker.elastic.co/elasticsearch/elasticsearch:6.2.4
```
2. Change your configuration to use this instance:
```
pam:
  elasticsearch:
    url: http://localhost:9200
```
