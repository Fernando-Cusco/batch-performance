package cusco.mejia.http;

import java.util.concurrent.CompletionStage;

import javax.enterprise.context.ApplicationScoped;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class RestClient {

    private WebClient webClient;

    public RestClient() {
        Vertx vertx = Vertx.vertx();
        webClient = WebClient.create(vertx);
    }

    public JsonObject sendRequest(String url) {
        try {
            long start = System.currentTimeMillis();
            log.info("Sending request to: {}", url);
            Future<HttpResponse<Buffer>> response = webClient.getAbs(url).send();
            CompletionStage<HttpResponse<Buffer>> cs = response.toCompletionStage();
            String res = cs.toCompletableFuture().get().bodyAsString();
            long end = System.currentTimeMillis();
            JsonObject jsonObject = new JsonObject(res);
            log.info("API Time to get response: {} ms", (end - start));
            return jsonObject;
        } catch (Exception e) {
            log.error("Error sending request", e);
            throw new RuntimeException(e);
        }
        
    }

    
}
