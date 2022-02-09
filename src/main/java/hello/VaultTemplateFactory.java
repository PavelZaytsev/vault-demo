package hello;

import hello.configs.FileConfig;
import org.springframework.vault.config.AbstractVaultConfiguration;
import org.springframework.vault.core.VaultTemplate;

public class VaultTemplateFactory {

    private VaultTemplateFactory() {

    }

    private static VaultTemplate fromConfig(AbstractVaultConfiguration config) {
        return new VaultTemplate(config.vaultEndpoint(), config.clientAuthentication());
    }

    public static VaultTemplate createConfig(String tokenPath, String host, int port) {
        return fromConfig(new FileConfig(tokenPath, host, port));
    }
}
