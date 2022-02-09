package hello;

import org.springframework.vault.core.VaultKeyValueOperations;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponse;

import java.util.Collections;

import static org.springframework.vault.core.VaultKeyValueOperationsSupport.KeyValueBackend.KV_2;

public class ReaderApp {
    public static void main(String[] args) {
        String readerConfig;
        String host;
        int port;

        if (args.length != 3) {
            throw new IllegalArgumentException("Wrong number of args. Needed 3.");
        } else {
            readerConfig = args[0];
            host = args[1];
            port = Integer.parseInt(args[2]);
        }

        System.out.println("Running reader app with args: " + args);
        VaultTemplate vaultTemplate = VaultTemplateFactory.createConfig(readerConfig, host, port);
        String kvPath = "/secret";
        VaultKeyValueOperations vaultKeyValueOperations = vaultTemplate.opsForKeyValue(kvPath, KV_2);

        System.out.println("Reading the secret:");
        VaultResponse response = vaultKeyValueOperations.get("kvstore");
        System.out.println("Result: " + response.getData());

        Secrets secrets = new Secrets();
        secrets.key = "randomKey";
        secrets.value = "randomValue";

        System.out.println(String.format("Trying to write key: `%s` and value: `%s` without permissions", secrets.key, secrets.value));
        vaultKeyValueOperations.put("kvstore", Collections.singletonMap(secrets.key, secrets.value));
    }
}
