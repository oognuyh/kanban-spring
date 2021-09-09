package com.oognuyh.kanban.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.oognuyh.kanban.model.OAuth2UserInfo;
import com.oognuyh.kanban.model.Tokens;
import com.oognuyh.kanban.model.User;
import com.oognuyh.kanban.repository.UserRepository;
import com.oognuyh.kanban.security.JwtUtils;
import com.oognuyh.kanban.security.VerifiedResult;
import com.oognuyh.kanban.service.UserService;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultReactiveOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public Mono<OAuth2User> loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        ReactiveOAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultReactiveOAuth2UserService();        

        return delegate.loadUser(userRequest)
            .flatMap(oAuth2User -> {
                String registrationId = userRequest
                    .getClientRegistration()
                    .getRegistrationId();

                String userNameAttributeName = userRequest
                    .getClientRegistration()
                    .getProviderDetails()
                    .getUserInfoEndpoint()
                    .getUserNameAttributeName();
                
                OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfo.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());
                
                return this.saveOrUpdate(oAuth2UserInfo.toEntity())
                    .map(user -> {
                        Map<String, Object> attributes = new HashMap<>(oAuth2UserInfo.getAttributes());

                        attributes.put("user_id", user.getId());

                        return new DefaultOAuth2User(
                            user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.toString())).collect(Collectors.toList()), 
                            attributes, 
                            oAuth2UserInfo.getNameAttributeKey()
                        );
                    }).log();
            });
    }

    @Override
    public Mono<User> findById(String id) {
        return userRepository.findById(id);
    }

    @Override
    public Mono<User> saveOrUpdate(User user) {
        return userRepository.findByEmailAndProvider(user.getEmail(), user.getProvider())
            .defaultIfEmpty(user)
            .flatMap(entity -> {
                entity.update(user);

                return userRepository.save(entity);
            });
    }

    @Override
    public Mono<Tokens> refreshTokens(String refreshToken) {
        return Mono.just(refreshToken)
            .map(JwtUtils::verify)
            .onErrorReturn(VerifiedResult.ERROR())
            .filter(verifiedResult -> verifiedResult.isSuccessful())
            .flatMap(verifiedResult -> userRepository.findById(verifiedResult.getId()))
            .map(user -> {
                String id = user.getId();
                String[] authorities = user.getRoles().stream()
                    .map(role -> role.name())
                    .toArray(String[]::new);

                return Tokens.builder()
                    .authToken(JwtUtils.generateAuthToken(id, authorities))
                    .refreshToken(JwtUtils.generateRefreshToken(id, authorities))
                    .build();
            });
    }
}