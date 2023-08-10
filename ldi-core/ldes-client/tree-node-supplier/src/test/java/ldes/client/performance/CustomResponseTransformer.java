package ldes.client.performance;

import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.ResponseTransformer;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.Response;

public class CustomResponseTransformer extends ResponseTransformer {

    @Override
    public String getName() {
        return "custom-transformer";
    }

    @Override
    public com.github.tomakehurst.wiremock.http.Response transform(
            Request request,
            Response response,
            FileSource files,
            Parameters parameters
    ) {
        // Your custom logic to generate a dynamic response
        String dynamicResponse = "Generated dynamically based on request: " + request.getUrl();

        return Response.Builder.like(response)
                .but()
                .body(dynamicResponse)
                .build();
    }
}
