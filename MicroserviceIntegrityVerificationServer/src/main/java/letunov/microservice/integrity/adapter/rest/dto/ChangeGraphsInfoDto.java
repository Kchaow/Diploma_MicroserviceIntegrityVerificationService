package letunov.microservice.integrity.adapter.rest.dto;

import lombok.Data;

@Data
public class ChangeGraphsInfoDto {
    private String id;
    private int commitedMicroservices;
    private int associatedMicroservices;
    private String status;
    private String dateTime;
}
