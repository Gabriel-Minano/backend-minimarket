package com.springboot.app.apiminimarket.config;

import com.springboot.app.apiminimarket.security.JwtAuthFilter;
import com.springboot.app.apiminimarket.security.UserDetailsServiceImpl;
import com.springboot.app.apiminimarket.wrapper.ApiResponse;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final JsonMapper jsonMapper;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter, UserDetailsServiceImpl userDetailsServiceImpl, JsonMapper jsonMapper) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
        this.jsonMapper = jsonMapper;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable) // Desactivamos CSRF ya que usamos API REST sin estado
                .authorizeHttpRequests(auth -> auth
                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers("/api/usuarios", "/api/usuarios/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,
                                "/api/categorias",
                                "/api/categorias/{id}",
                                "/api/productos",
                                "/api/productos/{id}",
                                "/api/ventas",
                                "/api/ventas/{id}",
                                "/api/roles"
                        ).hasAnyRole("ADMIN", "CAJERO")
                        .requestMatchers(HttpMethod.POST, "/api/ventas").hasAnyRole("ADMIN", "CAJERO")
                        .requestMatchers(HttpMethod.DELETE, "/api/ventas/{id}").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/categorias", "/api/productos").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.PUT, "/api/categorias/{id}", "/api/productos/{id}").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.DELETE, "/api/categorias/{id}", "/api/productos/{id}").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.PATCH,
                                "/api/categorias/{id}/reactivar",
                                "/api/productos/{id}/reactivar"
                        ).hasRole("ADMIN")

                        .anyRequest().denyAll()
                ).exceptionHandling(ex -> ex

                        //Usuario sin autenticación válida 401
                        .authenticationEntryPoint((request, response, exception) ->
                                writeErrorResponse(
                                        response,
                                        HttpServletResponse.SC_UNAUTHORIZED,
                                        "No autenticado"
                                )
                        )

                        //Usuario autenticado sin permisos 403
                        .accessDeniedHandler((request, response, exception) ->
                                writeErrorResponse(
                                        response,
                                        HttpServletResponse.SC_FORBIDDEN,
                                        "No tienes permisos para realizar esta operación"
                                )
                        )
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsServiceImpl);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Sincronizado con el encriptador que usamos en UsuarioService
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    //CORS
    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of(
                "http://localhost:4200"
        ));

        config.setAllowedMethods(List.of(
                "GET",
                "POST",
                "PUT",
                "PATCH",
                "DELETE",
                "OPTIONS"
        ));

        config.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type"
        ));

        config.setAllowCredentials(false);

        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/api/**", config);

        return source;
    }

    private void writeErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        ApiResponse<Void> body = new ApiResponse<>(
                false,
                message,
                null
        );
        response.getWriter().write(
                jsonMapper.writeValueAsString(body)
        );
    }
}
