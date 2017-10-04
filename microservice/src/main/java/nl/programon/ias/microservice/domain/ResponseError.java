package nl.programon.ias.microservice.domain;

public class ResponseError {
    private String code;
    private String reason;

    public ResponseError(String code, String reason) {
        this.code = code;
        this.reason = reason;
    }


}
