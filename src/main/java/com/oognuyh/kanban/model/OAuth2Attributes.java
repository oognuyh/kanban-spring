package com.oognuyh.kanban.model;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OAuth2Attributes {

    private Map<String, Object> attributes;

    private String nameAttributeKey;

    private String name;

    private String email;

    private String imageUrl;

    public static OAuth2Attributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        return ofGithub(userNameAttributeName, attributes);
    }

    private static OAuth2Attributes ofGithub(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuth2Attributes.builder()
            .attributes(attributes)
            .nameAttributeKey(userNameAttributeName)
            .name((String) attributes.get("name"))
            .email((String) attributes.get("email"))
            .imageUrl((String) attributes.get("avatar_url"))
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