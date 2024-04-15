package server.model;

import java.util.Optional;
import server.enumerated.HttpMethod;

public record RequestParams(HttpMethod httpMethod, Optional<Integer> requestParam, String requestBodyInString){

}


