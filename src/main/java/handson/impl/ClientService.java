package handson.impl;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.defaultconfig.ApiRootBuilder;
import com.commercetools.api.defaultconfig.ServiceRegion;
import io.vrap.rmf.base.client.oauth2.ClientCredentials;

import java.io.IOException;
import java.util.Properties;

public class ClientService {
    /**
     * @throws IOException exception
     */
    public static ProjectApiRoot createApiClient(final String prefix) throws IOException {
        final Properties prop = new Properties();
        prop.load(ClientService.class.getResourceAsStream("/dev.properties"));
        String clientId = prop.getProperty(prefix + "clientId");
        String clientSecret = prop.getProperty(prefix + "clientSecret");
        String projectKey = prop.getProperty(prefix + "projectKey");

        return ApiRootBuilder.of().defaultClient(
                        ClientCredentials.of()
                                .withClientId(clientId)
                                .withClientSecret(clientSecret)
                                .build(),
                        ServiceRegion.GCP_EUROPE_WEST1.getOAuthTokenUrl(),
                        ServiceRegion.GCP_EUROPE_WEST1.getApiUrl()
                )
                .build(projectKey);
    }
}