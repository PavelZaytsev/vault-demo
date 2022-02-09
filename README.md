### Export vault address and root token
```export VAULT_ADDR=http://127.0.0.2:8200```

```export VAULT_TOKEN=$(cat root_token-vault_2)```

### Enable kv secrets engine

```vault secrets enable -path="secret" kv```

### Verify secrets engines
```vault secrets list```

### Upload `writer` policy
```vault policy write writer policies/write-app-policy.hcl```

### Verify policy is there
```vault policy list```

### Create a token with a policy attached and write it to a file
```vault token create -ttl=1h -use-limit=4 -explicit-max-ttl=120h -policy=writer -format=json | jq -r ".auth.client_token" | awk '{print "token="$1}' > src/main/resources/writer.properties``` 

### Deploy the writer jar on one of the nodes and run the app
```java -cp gs-accessing-vault-0.1.0.jar -Dloader.main=hello.WriterApp org.springframework.boot.loader.PropertiesLauncher <config_path> <host> <port> <key> <value>```

### Upload `reader` policy
```vault policy write reader policies/read-app-policy.hcl```

### Verify policy is there
```vault policy list```

### Create a token with a policy attached and write it to a file
```vault token create -ttl=1h -use-limit=2 -explicit-max-ttl=120h -policy=reader -format=json | jq -r ".auth.client_token" | awk '{print "token="$1}' > src/main/resources/reader.properties```

### Deploy the reader jar on one of the nodes and run the app
```java -cp gs-accessing-vault-0.1.0.jar -Dloader.main=hello.ReaderApp org.springframework.boot.loader.PropertiesLauncher <config_path> <host> <port>```

### Enable transit secrets engine
```vault secrets enable transit```

### Verify secrets engines
```vault secrets list```

### Create encryption key named `secret`
```vault write -f transit/keys/secret```

### Upload `encryption` policy
```vault policy write encryption policies/encrypt-app-policy.hcl```

### Create a token with a policy attached and write it to a file
```vault token create -ttl=1h -use-limit=99 -explicit-max-ttl=120h -policy=encryption -format=json | jq -r ".auth.client_token" | awk '{print "token="$1}' > src/main/resources/encryption.properties```

### Deploy the encryption jar on one of the nodes and run the app
```java -cp gs-accessing-vault-0.1.0.jar -Dloader.main=hello.EncryptionApp org.springframework.boot.loader.PropertiesLauncher <config_path> <host> <port>```







