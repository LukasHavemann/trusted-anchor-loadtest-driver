# trusted-anchor-loadtest-driver

Gatling based loadtest driver for performance testing of [trusted anchor server](https://github.com/LukasHavemann/trusted-anchor-server).

## Test Execution

Launch trusted anchor server. For example:

```
docker pull postgres
docker run --name postgres -e POSTGRES_PASSWORD=test -p 5432:5432 -e POSTGRES_USER=postgres -d postgres -c shared_buffers=4GB -c max_connections=200 -c work_mem=2GB 

docker pull lukashavemann/trusted-anchor-server
docker run --name anchor --link postgres -p 8080:8080 -e SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/postgres -e TRUSTEDANCHOR_BATCHER_BATCHSIZE=10 -e TRUSTEDANCHOR_BATCHER_WORKERTHREADS=48 -e LOGGING_LEVEL_DE_TRUSTED_ANCHOR_SERVER_SERVICE=INFO -d lukashavemann/trusted-anchor-server 
```

Pull trusted-anchor-loadtest-driver repository and run ```mvn gatling:test```. Gatling will start with the default loadtest scenario. Edit ```de/trusted/anchor/server/LoadTestTrustedAnchorSimulation.scala``` for a different loadtest profile.