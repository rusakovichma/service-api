package com.epam.ta.reportportal.auth;

import com.epam.ta.reportportal.dao.OAuth2AccessTokenRepository;
import com.epam.ta.reportportal.entity.user.StoredAccessToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter.ACCESS_TOKEN_ID;

@ExtendWith(MockitoExtension.class)
class CombinedTokenStoreSecurityTest {

    @Mock
    private OAuth2AccessTokenRepository oAuth2AccessTokenRepository;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private JwtAccessTokenConverter jwtTokenEnhancer;

    private CombinedTokenStore tokenStore;

    private void setCombinedTokenStoreProperty(String propertyName, Object propertyValue) {
        Field property = ReflectionUtils.findField(CombinedTokenStore.class, propertyName);
        ReflectionUtils.makeAccessible(property);
        ReflectionUtils.setField(property, tokenStore, propertyValue);
    }

    @BeforeEach
    public void setup() {
        tokenStore = new CombinedTokenStore(jwtTokenEnhancer);

        setCombinedTokenStoreProperty("oAuth2AccessTokenRepository", oAuth2AccessTokenRepository);
        setCombinedTokenStoreProperty("userDetailsService", userDetailsService);

    }

    @Test
    void readAccessTokenDeserializationRcePocTest() {
        Map<String, Object> additionalInformation = new HashMap<>();
        additionalInformation.put(ACCESS_TOKEN_ID, new Object());

        DefaultOAuth2AccessToken defaultToken = new DefaultOAuth2AccessToken("default_token");
        defaultToken.setAdditionalInformation(additionalInformation);

        when(jwtTokenEnhancer.extractAccessToken(anyString(), anyMap())).thenReturn(defaultToken);
        when(jwtTokenEnhancer.isRefreshToken(defaultToken)).thenReturn(true);

        byte[] payload = Base64.getDecoder().decode("rO0ABXNyACpjb20uZXBhbS50YS5yZXBvcnRwb3J0YWwuYXV0aC5TaW1wbGVHYWRnZXTz9R8HgvOZ+wIAAHhw");

        StoredAccessToken storedAccessToken = new StoredAccessToken();
        storedAccessToken.setToken(payload);
        when(oAuth2AccessTokenRepository.findByTokenId(anyString())).thenReturn(storedAccessToken);

        Exception exception = assertThrows(ClassCastException.class, () -> {
            tokenStore.readAccessToken("token_value");
        });
    }
}