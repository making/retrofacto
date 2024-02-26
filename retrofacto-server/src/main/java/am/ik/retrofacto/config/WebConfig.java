package am.ik.retrofacto.config;

import am.ik.retrofacto.auth.RetroAuthInterceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration(proxyBeanMethods = false)
public class WebConfig implements WebMvcConfigurer {

	private final RetroAuthInterceptor retroAuthInterceptor;

	public WebConfig(RetroAuthInterceptor retroAuthInterceptor) {
		this.retroAuthInterceptor = retroAuthInterceptor;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(this.retroAuthInterceptor).addPathPatterns("/boards/**");
	}

}
