package com.rackspacecloud.blueflood.kafkametricsmetadataconsumer.provider;

public interface IAuthTokenProvider {
    String getTenantId();
    String getAuthToken();
}
