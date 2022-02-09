package hello.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.SimpleVaultEndpointProvider;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.client.VaultEndpointProvider;
import org.springframework.vault.config.AbstractVaultConfiguration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

@Configuration
public class FileConfig extends AbstractVaultConfiguration {

    private final String tokenFile;
    private final String host;
    private final int port;

    public FileConfig(String tokenFile, String host, int port) {
        this.tokenFile = tokenFile;
        this.host = host;
        this.port = port;
    }

    @Override
    public VaultEndpoint vaultEndpoint() {
        VaultEndpoint vaultEndpoint = VaultEndpoint.create(host, port);
        vaultEndpoint.setScheme("http");
        return vaultEndpoint;
    }

    @Override
    public ClientAuthentication clientAuthentication() {
        return new TokenAuthentication(getToken(tokenFile));
    }

    @Override
    public VaultEndpointProvider vaultEndpointProvider() {
        return SimpleVaultEndpointProvider.of(vaultEndpoint());
    }

    private String getToken(String propFile) {
        try (InputStream fis = new FileInputStream(propFile)) {
            System.out.println("prop file: " + propFile);
            Properties prop = new Properties();
            prop.load(fis);
            return Optional.ofNullable(prop.getProperty("token")).orElseThrow(() ->
                    new IllegalStateException("Token should be specified in the config file."));

        } catch (IOException io) {
            throw new RuntimeException(io);
        }
    }

}


