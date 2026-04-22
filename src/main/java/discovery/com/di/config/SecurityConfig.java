package discovery.com.di.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

//@Configuration
public class SecurityConfig {

    // ✅ Password encoder (for your auth)
    //@Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ✅ Disable Spring default login
    //@Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()   // allow everything
                )
                .formLogin(form -> form.disable())   // ❗ disable login page
                .httpBasic(httpBasic -> httpBasic.disable()); // ❗ disable popup

        return http.build();
    }
}