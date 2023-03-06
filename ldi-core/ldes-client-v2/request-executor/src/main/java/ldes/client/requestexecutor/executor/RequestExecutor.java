package ldes.client.requestexecutor.executor;


import ldes.client.requestexecutor.domain.valueobjects.Request;
import ldes.client.requestexecutor.domain.valueobjects.Response;

import java.util.function.Function;

public interface RequestExecutor extends Function<Request, Response> {

    Response apply(Request request);

}
