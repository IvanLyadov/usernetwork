# Usernetwork

## To Run Application

### Install Vault
Follow the instructions to install Vault: [Install Vault](https://developer.hashicorp.com/vault/install)

### Start Vault Dev Server
Start the Vault development server by running the command:
```sh
vault server -dev -dev-root-token-id="0000-0000-0000-0000"
```

### Optional: Set HTTP Server
Set the HTTP server if Vault is running in HTTP instead of HTTPS:
```sh
set VAULT_ADDR=http://127.0.0.1:8200
```
### Login to Your Vault
Login to Vault using the following command:
```sh
vault login 0000-0000-0000-0000
```

### Add Key-Value Pairs
Add key-value pairs to Vault:
```sh
vault kv put secret/demoapi username=default password=cOBY8tbHMMK.E
```

`default` - is a ClickHouse username

`cOBY8tbHMMK.E` - is a ClickHouse password

### Verify the Data
Verify the stored data:
```sh
vault kv get secret/demoapi
```

### Open in browser to verify: 
```http://localhost:8080/api/users```

## Using Docker
### Build the Docker image:

Run the following command in the root of your project directory (where the Dockerfile is located):
```docker build -t user-api```

Start the application with Docker Compose:
```docker-compose up```