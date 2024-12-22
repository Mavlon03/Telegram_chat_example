package uz.pdp;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
public class LoginFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getRequestURI().contains("/login")) {
            filterChain.doFilter(request, response);
        } else {
            Object currentUser = request.getSession().getAttribute("currentUser");

            if (currentUser == null) {
                response.sendRedirect("/login");
                return;
            }
            filterChain.doFilter(request, response);
        }
    }
}
