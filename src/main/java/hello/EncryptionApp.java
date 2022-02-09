package hello;

import org.springframework.vault.VaultException;
import org.springframework.vault.core.VaultKeyValueOperations;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.Ciphertext;
import org.springframework.vault.support.Plaintext;
import org.springframework.vault.support.VaultResponse;

import java.util.Collections;
import java.util.UUID;

import static org.springframework.vault.core.VaultKeyValueOperationsSupport.KeyValueBackend.KV_2;

public class EncryptionApp {

    private static final String KEY_CHAIN_NAME = "secret";

    private static String encrypt(String text, VaultTemplate vaultTemplate) {
        Plaintext plainText = Plaintext.of(text);
        String cipherText = vaultTemplate.opsForTransit().encrypt(KEY_CHAIN_NAME, plainText).getCiphertext();
        return cipherText;
    }


    private static String decrypt(String text, VaultTemplate vaultTemplate) {
        Ciphertext cipherText = Ciphertext.of(text);
        String plainText = vaultTemplate.opsForTransit().decrypt(KEY_CHAIN_NAME, cipherText).asString();
        return plainText;
    }

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

        System.out.println("Running encryption app with args: " + args);
        VaultTemplate vaultTemplate = VaultTemplateFactory.createConfig(readerConfig, host, port);
        String kvPath = "/secret";
        VaultKeyValueOperations vaultKeyValueOperations = vaultTemplate.opsForKeyValue(kvPath, KV_2);

        System.out.println("Trying to read the secret without permissions:");
        try {
            VaultResponse response = vaultKeyValueOperations.get("kvstore");
            System.out.println("Result: " + response.getData());
        } catch (VaultException ve) {
            System.out.println("Expected exception: ");
            ve.printStackTrace();
        }

        Secrets secrets = new Secrets();
        secrets.key = "randomKey";
        secrets.value = "randomValue";

        System.out.println(String.format("Writing key: `%s` and value: `%s` without permissions", secrets.key, secrets.value));

        try {
            vaultKeyValueOperations.put("kvstore", Collections.singletonMap(secrets.key, secrets.value));
        } catch (VaultException ve) {
            System.out.println("Expected exception: ");
            ve.printStackTrace();
        }

        String secretId = UUID.randomUUID().toString();
        System.out.println("Encrypting the secret id plaintext using vault: " + secretId);
        String cipherText = encrypt(secretId, vaultTemplate);
        System.out.println("Cipher text of secret id: " + cipherText);
        System.out.println("Decrypting the secret id ciphertext using vault: " + cipherText);
        String plainText = decrypt(cipherText, vaultTemplate);
        System.out.println("Decrypted secretId: " + plainText);
    }
}
