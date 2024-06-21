package com.ecommerce.domain.security.serviceImpl.jwtService;

import com.ecommerce.common.exception.DomainException;
import com.ecommerce.domain.security.model.Member;
import com.ecommerce.domain.security.repository.MemberRepository;
import com.ecommerce.domain.security.serviceImpl.jwtService.UserDetailImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {
    private final MemberRepository memberRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> DomainException.notFound("User with email: " + email + " !"));
        return UserDetailImpl.build(member);
    }
}
