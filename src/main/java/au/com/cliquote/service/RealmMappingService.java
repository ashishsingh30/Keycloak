package au.com.cliquote.service;

import org.keycloak.representations.idm.RealmRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RealmMappingService {

    @Autowired
    private Keycloak keycloak;

    private final Map<String, String> domainToRealmMap = new HashMap<>();

    @Scheduled(fixedDelay = 3600000) // Refresh every hour
    public void refreshRealms() {
        List<RealmRepresentation> realms = keycloak.realms().findAll();
        for (RealmRepresentation realm : realms) {
            String domain = realm.getDisplayName(); //  displayName should contain the domain
            if (domain != null && !domain.isEmpty()) {
                domainToRealmMap.put(domain, realm.getRealm());
            }
        }
    }

    public String getRealmForDomain(String domain) {
        return domainToRealmMap.getOrDefault(domain, "defaultRealm");
    }
}
