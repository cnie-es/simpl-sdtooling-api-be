package eu.europa.ec.simpl.sdtoolingbe.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Log4j2
@RequiredArgsConstructor
@ConditionalOnProperty(
        name = "catalogue-adapter.mock.revoke-resource-description",
        havingValue = "true",
        matchIfMissing = false)
public class RevokeResourceDescriptionMockAspect {

    private final ObjectMapper objectMapper;

    @Around(
            "execution(* eu.europa.ec.simpl.sdtoolingbe.service.resourcedescription.ResourceDescriptionServiceImpl.revokeResourceDescription(..))")
    public Object mockRevokeResourceDescription(ProceedingJoinPoint joinPoint) {
        String resourceDescriptionId = (String) joinPoint.getArgs()[0];
        log.warn("revokeResourceDescription() is MOCKED");

        log.warn("Simulating success for resourceDescriptionId '{}'", resourceDescriptionId);
        ObjectNode result = objectMapper.createObjectNode();
        result.put("id", resourceDescriptionId);
        result.put("status", "revoked");
        result.put("statusDatetime", Instant.now().toString());

        return result.toString();
    }
}
