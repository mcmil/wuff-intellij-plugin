package pl.cmil.wuff.plugin.diagnostic.rest;

import pl.cmil.wuff.plugin.diagnostic.RawDiagnosticDataProvider;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

public class RestDiagnosticDataProvider implements RawDiagnosticDataProvider {
    private final String url;
    private final String username;
    private final String password;

    private Client client;


    public RestDiagnosticDataProvider(String url, String username, String password) {

        this.url = url;
        this.username = username;
        this.password = password;

        client = ClientBuilder.newClient();
        client.register(new Authenticator(username, password));
    }

    @Override
    public String getData() {
        WebTarget target = client.target(url+"bundles.json");

        return target.request(MediaType.APPLICATION_JSON).get().readEntity(String.class);
    }
}
