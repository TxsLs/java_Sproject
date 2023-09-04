package org.teach.study.boot.config;

import javax.servlet.http.HttpServletRequest;

import org.quincy.rock.core.cache.ObjectCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.teach.study.boot.AppUtils;

/**
 * <b>SecurityConfig。</b>
 * <p><b>详细说明：</b></p>
 * <!-- 在此添加详细说明 -->
 * 无。
 * 
 * @version 1.0
 * @author mex2000
 * @since 1.0
 */
@Configuration
@SuppressWarnings({ "unchecked", "rawtypes" })
public class SecurityConfig {

	@Bean
	public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,
			PasswordEncoder passwordEncoder) {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setHideUserNotFoundExceptions(false);
		provider.setUserDetailsService(userDetailsService);
		provider.setPasswordEncoder(passwordEncoder);
		return provider;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(@Nullable HttpSecurity http) throws Exception {
		if (http == null)
			return null;
		http.csrf().disable().headers().frameOptions().sameOrigin().and().addFilterBefore((req, resp, chain) -> {
			HttpServletRequest request = ((HttpServletRequest) req);
			AntPathRequestMatcher ant = new AntPathRequestMatcher("/login");
			if (AppUtils.useCaptcha && ant.matches(request)) {
				String captcha = req.getParameter("captcha");
				ObjectCache<String> oc = (ObjectCache) request.getSession()
						.getAttribute(AppUtils.PUBLIC_LAST_CAPTCHA_KEY);
				String verifyCode = oc == null ? null : oc.get();
				if (verifyCode == null || !verifyCode.equals(captcha)) {
					request.getRequestDispatcher("/error").forward(req, resp);
				}
			}
			chain.doFilter(req, resp);
		}, UsernamePasswordAuthenticationFilter.class).authorizeRequests()
				.antMatchers("/css/**", "/fonts/**", "/images/**", "/js/**", "/pagecss/**", "/pagejs/**", "/index.html",
						"/loginUser", "/captcha.jpg")
				.permitAll().antMatchers("/dept/**").hasRole("ADMIN").anyRequest().authenticated().and().formLogin()
				.loginPage("/login.html").loginProcessingUrl("/login").defaultSuccessUrl("/success", true)
				.failureUrl("/fail").permitAll().and().logout().permitAll().and().exceptionHandling()
				.accessDeniedPage("/denied.html");
		return http.build();
	}
}
