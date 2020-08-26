package com.eorionsolution.microservices.employeemonitor.monitor.domain;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Data
@Document(value = "admin")
@Accessors(chain = true)
public class Admin implements UserDetails {
    @Id
    private String username;
    private String password;

    private boolean active = true;
    private Set<GrantedAuthority> roles = new HashSet<>();

    private LocalDateTime pwdLastSetTime;
    private LocalDateTime pwdExpireDateTime;
    private boolean sysPwd = true;

    @Builder
    public Admin(String username, String password) {
        this.username = username;
        this.password = password;
        roles.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return active;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !sysPwd;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return LocalDateTime.now().isBefore(this.pwdExpireDateTime);
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}
