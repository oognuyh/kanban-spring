package com.oognuyh.kanban.model;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import com.oognuyh.kanban.security.OAuth2Provider;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OAuth2UserInfo {

    private Map<String, Object> attributes;

    private String nameAttributeKey;

    private String name;

    private String email;

    private String imageUrl;

    private String provider;

    public static OAuth2UserInfo of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        if (registrationId.equalsIgnoreCase(OAuth2Provider.GOOGLE)) {
            return withGoogle(userNameAttributeName, attributes);
        } else if (registrationId.equalsIgnoreCase(OAuth2Provider.GITHUB)) {
            return withGithub(userNameAttributeName, attributes);
        } else {
            return null;
        }
    }

    private static OAuth2UserInfo withGithub(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuth2UserInfo.builder()
            .attributes(attributes)
            .nameAttributeKey(userNameAttributeName)
            .name((String) attributes.get("name"))
            .email((String) attributes.get("email"))
            .imageUrl((String) attributes.get("avatar_url"))
            .provider(OAuth2Provider.GITHUB)
            .build();
    }

    private static OAuth2UserInfo withGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuth2UserInfo.builder()
            .attributes(attributes)
            .nameAttributeKey(userNameAttributeName)
            .name((String) attributes.get("name"))
            .email((String) attributes.get("email"))
            .imageUrl((String) attributes.get("picture"))
            .provider(OAuth2Provider.GOOGLE)
            .build();
    }

    public User toEntity() {
        return User.builder()
            .email(email)
            .name(name)
            .roles(Arrays.stream(new Role[] {Role.USER})
                .collect(Collectors.toList()))
            .imageUrl(imageUrl)
            .provider(provider)
            .build();
    }
}