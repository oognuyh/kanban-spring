package com.oognuyh.kanban.service;

import java.util.stream.Collectors;

import com.oognuyh.kanban.model.OAuth2Attributes;
import com.oognuyh.kanban.model.User;
import com.oognuyh.kanban.repository.UserRepository;
import com.oognuyh.kanban.service.impl.UserService;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultReactiveOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
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
                // 깃헙 로그인, 구글 로그인 등을 구별하기 위한 정보를 가져온다.
                String registrationId = userRequest
                    .getClientRegistration()
                    .getRegistrationId();

                // 로그인 진행 시 키가 되는 필드값
                String userNameAttributeName = userRequest
                    .getClientRegistration()
                    .getProviderDetails()
                    .getUserInfoEndpoint()
                    .getUserNameAttributeName();
                
                OAuth2Attributes oAuth2Attributes = OAuth2Attributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

                return this.saveOrUpdate(oAuth2Attributes.toEntity())
                    .map(user -> new DefaultOAuth2User(
                        user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.toString())).collect(Collectors.toList()), 
                        oAuth2Attributes.getAttributes(), 
                        oAuth2Attributes.getNameAttributeKey())
                    ).log();
            });
    }

    @Override
    public Flux<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Mono<User> findById(String id) {
        return userRepository.findById(id);
    }

    @Override
    public Mono<User> saveOrUpdate(User user) {
        return userRepository.findByEmail(user.getEmail())
            .defaultIfEmpty(user)
            .flatMap(entity -> {
                entity.update(user);

                return userRepository.save(entity);
            });
    }    
}