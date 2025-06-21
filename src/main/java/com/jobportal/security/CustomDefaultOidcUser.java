package com.jobportal.security;

import com.jobportal.entity.User;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Collection;

@Getter
public class CustomDefaultOidcUser extends DefaultOidcUser {

    private final User user;
    public CustomDefaultOidcUser(Collection<? extends GrantedAuthority> authorities,
                                 OidcIdToken idToken, OidcUserInfo userInfo, User user) {
        super(authorities, idToken, userInfo);
        this.user = user;
    }
}
