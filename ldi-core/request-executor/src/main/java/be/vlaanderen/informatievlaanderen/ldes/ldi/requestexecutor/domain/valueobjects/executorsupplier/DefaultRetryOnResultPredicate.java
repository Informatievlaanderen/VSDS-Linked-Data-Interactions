package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.domain.valueobjects.executorsupplier;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.domain.valueobjects.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class DefaultRetryOnResultPredicate implements Predicate<Response> {

    public static final int HTTP_TOO_MANY_REQUESTS = 429; // not included in apache HttpStatus

    private final List<Integer> customStatusList;

    public DefaultRetryOnResultPredicate(List<Integer> customStatusList) {
        this.customStatusList = Objects.requireNonNullElse(customStatusList, new ArrayList<>());
    }

    @Override
    public boolean test(Response response) {
        return response == null
                || response.getHttpStatus() >= 500
                || response.hasStatus(List.of(HTTP_TOO_MANY_REQUESTS))
                || response.hasStatus(customStatusList);
    }

}
