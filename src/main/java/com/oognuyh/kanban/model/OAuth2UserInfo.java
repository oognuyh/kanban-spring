package com.oognuyh.kanban.model;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

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

    public static OAuth2UserInfo of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        if (registrationId.equals("google")) {
            return withGoogle(userNameAttributeName, attributes);
        } else if (registrationId.equals("github")) {
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
            .build();
    }

    private static OAuth2UserInfo withGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuth2UserInfo.builder()
            .attributes(attributes)
            .nameAttributeKey(userNameAttributeName)
            .name((String) attributes.get("name"))
            .email((String) attributes.get("email"))
            .imageUrl((String) attributes.get("picture"))
            .build();
    }

    public User toEntity() {
        return User.builder()
            .email(email)
            .name(name)
            .roles(Arrays.stream(new Role[] {Role.USER})
                .collect(Collectors.toList()))
            .imageUrl(imageUrl)
            .build();
    }
}