package com.w3lsolucoes.dscatalog.projections;

public interface UserDetailProjection {
    String getUserName();
    String getPassword();
    Long getRoleId();
    String getAuthority();
}
