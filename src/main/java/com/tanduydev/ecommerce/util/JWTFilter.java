package com.tanduydev.ecommerce.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Collections;

@Component
@Slf4j
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    // Dùng cái này để ném lỗi từ Filter ra GlobalException
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            String authHeader = request.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String jwt = authHeader.substring(7);

                // Lấy toàn bộ claims để không phải parse Token nhiều lần
                Claims claims = jwtUtil.extractAllClaims(jwt);
                String username = claims.getSubject();
                String role = claims.get("role", String.class); // Lấy role đã nhét sẵn trong token

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                    if (jwtUtil.validateToken(jwt)) {
                        // TỐI ƯU CHÍ MẠNG: Không gọi DB nữa, lấy luôn role ép vào Spring Security
                        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role.toUpperCase());

                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(username, null, Collections.singletonList(authority));

                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            }
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException ex) {
            log.warn("Token expired: {}", ex.getMessage());
            // Ném lỗi về cho GlobalException xử lý để chuẩn hóa JSON trả về
            handlerExceptionResolver.resolveException(request, response, null, ex);
        } catch (Exception ex) {
            log.error("Lỗi xác thực JWT: ", ex);
            handlerExceptionResolver.resolveException(request, response, null, ex);
        }
    }
}