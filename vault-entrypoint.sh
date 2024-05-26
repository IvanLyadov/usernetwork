#!/bin/sh

# Start Vault server in the background
vault server -dev -dev-root-token-id="0000-0000-0000-0000" &

# Wait for Vault to start
sleep 5

# Setting Vault server to HTTP
set VAULT_ADDR=http://127.0.0.1:8200

# Login to Vault
vault login 0000-0000-0000-0000

# Add key-value pairs to Vault
vault kv put secret/demoapi username=default password=cOBY8tbHMMK.E

# Keep the container running
tail -f /dev/null
