### Architecture

* vault_1 (http://127.0.0.1:8200) is initialized and unsealed. The root token creates a transit key that enables the other Vaults auto-unseal. This Vault does not join the cluster.
* vault_2 (http://127.0.0.2:8200) is initialized and unsealed. This Vault starts as the cluster leader. An example K/V-V2 secret is created.
* vault_3 (http://127.0.0.3:8200) is only started. You will join it to the cluster.
* vault_4 (http://127.0.0.4:8200) is only started. You will join it to the cluster.

### Create network
```./cluster.sh create network```

### Create config for each vault
```./cluster.sh create config```

### Setup vault_1
```./cluster.sh setup vault_1```

### Setup vault_2
```./cluster.sh setup vault_2```

### Setup vault_3
```./cluster.sh setup vault_3```

### Setup vault_4
```./cluster.sh setup vault_4```

### Verify status
```./cluster.sh status```

### List peers
```export VAULT_ADDR="http://127.0.0.2:8200"```

```export VAULT_TOKEN=$(cat root_token-vault_2)```

```vault operator raft list-peers```

### Join nodes to the cluster

```Open new terminal```

```export VAULT_ADDR="http://127.0.0.3:8200"```

```export VAULT_TOKEN=$(cat root_token-vault_2)```

```vault operator raft join http://127.0.0.2:8200```

```vault operator raft list-peers```

### If the connection of the nodes are known beforehand - use retry-join

```
  retry_join {    leader_api_addr = "http://127.0.0.2:8200"  }
  retry_join {    leader_api_addr = "http://127.0.0.3:8200"  }
```

```
./cluster.sh stop vault_4
```

```Open new terminal```

```Change stanza```

```./cluster.sh start vault_4```

```export VAULT_TOKEN=$(cat root_token-vault_2)```

```export VAULT_ADDR="http://127.0.0.4:8200"```

```vault operator raft list-peers```

### Remove node 4 (for reader test)
```vault operator raft remove-peer vault_4```

```vault operator raft list-peers```

```./cluster.sh stop vault_4```

```rm -r raft-vault_4```

```mkdir raft-vault_4```

```./cluster.sh start vault_4```

```vault operator raft list-peers```

### Change the address to vault_4 and test the reader again (verify the availability)

### Cleanup

```./cluster.sh clean```