package letunov.microservice.integrity.adapter.rest.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class VerificationDto {
    private String graphLink;
    private List<String> messages;
}
