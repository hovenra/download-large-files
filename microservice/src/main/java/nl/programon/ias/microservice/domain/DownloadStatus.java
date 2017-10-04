package nl.programon.ias.microservice.domain;

public class DownloadStatus {

    private String status;

    public DownloadStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
