package com.multitenant.resourceserver.context;

public class TenantContext {
    
    public static String DEFAULT_TENANT = "public";

    private static final ThreadLocal<String> currentTenant = ThreadLocal.withInitial(() -> TenantContext.DEFAULT_TENANT);

    public static void setCurrentTenant(String tenant) {
        currentTenant.set(tenant);
    }

    public static String getCurrentTenant() {
        return currentTenant.get();
    }

    public static void clear() {
        currentTenant.remove();
    }

}
