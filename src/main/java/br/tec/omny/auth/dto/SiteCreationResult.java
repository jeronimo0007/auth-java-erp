package br.tec.omny.auth.dto;

import br.tec.omny.auth.entity.Client;
import br.tec.omny.auth.entity.Site;

public class SiteCreationResult {

    private final Client client;
    private final Site site;

    public SiteCreationResult(Client client, Site site) {
        this.client = client;
        this.site = site;
    }

    public Client getClient() {
        return client;
    }

    public Site getSite() {
        return site;
    }
}

