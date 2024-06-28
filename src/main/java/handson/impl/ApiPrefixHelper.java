package handson.impl;

public enum ApiPrefixHelper {
    API_DEV_CLIENT_PREFIX("ctp.");
    private final String prefix;

    ApiPrefixHelper(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return this.prefix;
    }
}