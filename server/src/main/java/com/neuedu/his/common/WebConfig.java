package com.neuedu.his.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;

/**
 * Web 配置：跨域放行 + JWT 登录校验
 *
 * <p>开发期为了便于用浏览器 / Apifox 直接调试，登录拦截默认关闭；
 * 如需开启，把 {@link #addInterceptors} 里注册拦截器的代码取消注释即可。</p>
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        // 核心闭环接口默认不拦截，方便本地联调。
        // 生产环境请放开下面注释，仅放行白名单路径。
        //
        // registry.addInterceptor(authInterceptor)
        //         .addPathPatterns("/**")
        //         .excludePathPatterns(
        //                 "/auth/login",
        //                 "/h2-console/**",
        //                 "/error"
        //         );
    }

    /**
     * JWT 登录拦截器
     */
    @Configuration
    public static class AuthInterceptor implements HandlerInterceptor {

        @Override
        public boolean preHandle(@NonNull HttpServletRequest request,
                                 @NonNull HttpServletResponse response,
                                 @NonNull Object handler) throws Exception {
            // 预检请求直接放行
            if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
                return true;
            }

            String token = request.getHeader("token");
            if (token == null || token.isBlank()) {
                String auth = request.getHeader("Authorization");
                if (auth != null && auth.startsWith("Bearer ")) {
                    token = auth.substring(7);
                }
            }

            if (JwtUtils.verify(token)) {
                return true;
            }

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(new String(
                    JsonResult.fail("登录已失效，请重新登录").toString().getBytes(StandardCharsets.UTF_8),
                    StandardCharsets.UTF_8));
            return false;
        }
    }
}
