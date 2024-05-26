import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultConfig;
import com.bettercloud.vault.response.LogicalResponse;

public class VaultIntegration {
    public static void main(String[] args) throws Exception {
        final VaultConfig config = new VaultConfig()
            .address(System.getenv("VAULT_ADDR"))
            .token(System.getenv("VAULT_TOKEN"))
            .build();
        final Vault vault = new Vault(config);

        LogicalResponse response = vault.logical().write("secret/hello", new HashMap<String, Object>() {{
            put("value", "world");
        }});

        String value = vault.logical().read("secret/hello").getData().get("value");
        System.out.println("Value: " + value);
    }
}
