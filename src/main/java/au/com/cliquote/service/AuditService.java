package au.com.cliquote.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AuditService {
    private static final Logger logger = LoggerFactory.getLogger(AuditService.class);

    public void logAction(String action, String username) {
        logger.info("Audit log - Action: {}, Performed by: {}", action, username);
    }
}
