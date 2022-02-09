package hello;

import org.springframework.vault.core.VaultKeyValueOperations;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponse;

import java.util.Collections;

import static org.springframework.vault.core.VaultKeyValueOperationsSupport.KeyValueBackend.KV_2;

public class WriterApp {

    public static void main(String[] args) {
        System.out.println("Running writer app with args: " + args);
        String writerConfig = args[0];
        String host = args[1];
        int port = Integer.parseInt(args[2]);
        String key;
        String value;

        if (args.length == 5) {
            writerConfig = args[0];
            host = args[1];
            port = Integer.parseInt(args[2]);
            key = args[3];
            value = args[4];
        } else {
            throw new IllegalArgumentException("Wrong  number of args. Needed: 5.");
        }

        VaultTemplate vaultTemplate = VaultTemplateFactory.createConfig(writerConfig, host, port);
        String kvPath = "/secret";
        VaultKeyValueOperations vaultKeyValueOperations = vaultTemplate.opsForKeyValue(kvPath, KV_2);

        Secrets secrets = new Secrets();
        secrets.key = key;
        secrets.value = value;

        System.out.println(String.format("Writing key: `%s` and value: `%s`", key, value));
        vaultKeyValueOperations.put("kvstore", Collections.singletonMap(key, value));

        System.out.println("Reading the secret:");
        VaultResponse response = vaultKeyValueOperations.get("kvstore");
        System.out.println("Result: " + response.getData());
    }

}