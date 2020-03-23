package com.github.smartheye.fabric.tests;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;
import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.ContractException;
import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.gateway.Network;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallet.Identity;
import org.hyperledger.fabric.gateway.impl.InMemoryWallet;
import org.hyperledger.fabric.sdk.helper.Config;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;


@RunWith(JUnitPlatform.class)
public class GatewayTest2_AsLocalhost2 {

    Wallet wallet;

    String ADMIN_PRIV_KEY =
            "-----BEGIN PRIVATE KEY-----\nMIGHAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBG0wawIBAQQgoaZx1F3d9SYtNgvC\ntDsbwp6Bz9XQJUq0s+tB/g90GV2hRANCAAS4bSfus7RFU5BygWC0s+dz3qmw7bAD\npoWLxn8DT22c1yH/2sob+yrYcyOhd4VWNX0xykIYu4nHImeG3uZkupFc\n-----END PRIVATE KEY-----";

    String ADMIN_CERT =
            "-----BEGIN CERTIFICATE-----\nMIICKDCCAc+gAwIBAgIQZQ18jrRlHZKZrluyNUy6tzAKBggqhkjOPQQDAjBzMQsw\nCQYDVQQGEwJVUzETMBEGA1UECBMKQ2FsaWZvcm5pYTEWMBQGA1UEBxMNU2FuIEZy\nYW5jaXNjbzEZMBcGA1UEChMQb3JnMS5leGFtcGxlLmNvbTEcMBoGA1UEAxMTY2Eu\nb3JnMS5leGFtcGxlLmNvbTAeFw0yMDAzMTQwNzI3MDBaFw0zMDAzMTIwNzI3MDBa\nMGsxCzAJBgNVBAYTAlVTMRMwEQYDVQQIEwpDYWxpZm9ybmlhMRYwFAYDVQQHEw1T\nYW4gRnJhbmNpc2NvMQ4wDAYDVQQLEwVhZG1pbjEfMB0GA1UEAwwWQWRtaW5Ab3Jn\nMS5leGFtcGxlLmNvbTBZMBMGByqGSM49AgEGCCqGSM49AwEHA0IABLhtJ+6ztEVT\nkHKBYLSz53PeqbDtsAOmhYvGfwNPbZzXIf/ayhv7KthzI6F3hVY1fTHKQhi7icci\nZ4be5mS6kVyjTTBLMA4GA1UdDwEB/wQEAwIHgDAMBgNVHRMBAf8EAjAAMCsGA1Ud\nIwQkMCKAICVq4RQgegiH7lQqPYG1FoCh8dP+HC98Gk4rqqkaiJBLMAoGCCqGSM49\nBAMCA0cAMEQCIFZzGo4mfVLSWagfBFTUiZlQf29NDHfwffYoK3i6txR2AiA4BrlZ\nJJcOLgr7DqrG38TS19OCRKuEhj4w3ov30H6zbA==\n-----END CERTIFICATE-----";

    @BeforeAll
    public static void beforeAll() throws IOException {
        // System.setProperty("org.hyperledger.fabric.sdk.service_discovery.as_localhost", "true");
        Config config = Config.getConfig();
        System.out.println(config.discoverAsLocalhost());
    }

    @BeforeEach
    public void before() throws IOException {
        System.out.println("before testcase");
        wallet = new InMemoryWallet();
        wallet.put("Admin", Identity.createIdentity("Org1MSP", new StringReader(ADMIN_CERT),
                new StringReader(ADMIN_PRIV_KEY)));
    }

    @AfterEach
    public void after() {
        System.out.println("after  testcase");
    }

    @Test
    public void testDiscoveryIT001() throws Exception {
        InputStream in =
                GatewayTest2_AsLocalhost2.class.getResourceAsStream("connection-org1.json");
        Gateway.Builder builder = Gateway.createBuilder().networkConfig(in) //
                .identity(wallet, "Admin") //
                .discovery(true); //
        // Create a gateway connection
        try (Gateway gateway = builder.connect()) {

            // Obtain a smart contract deployed on the network.
            Network network = gateway.getNetwork("mychannel");
            Contract contract = network.getContract("mycc");

            // Submit transactions that store state to the ledger.
            byte[] invokeResults = contract.createTransaction("invoke").submit("a", "b", "1");
            System.out
                    .println("invokeResults=" + new String(invokeResults, StandardCharsets.UTF_8));

            // Evaluate transactions that query state from the ledger.
            byte[] queryResults = contract.evaluateTransaction("query", "a");
            System.out.println("queryResults=" + new String(queryResults, StandardCharsets.UTF_8));

        } catch (ContractException | TimeoutException | InterruptedException e) {
            e.printStackTrace();
            // throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
