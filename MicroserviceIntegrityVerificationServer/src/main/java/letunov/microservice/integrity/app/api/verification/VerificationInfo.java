package letunov.microservice.integrity.app.api.verification;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class VerificationInfo {
    private String graphLink;
    private List<String> messages;
}
