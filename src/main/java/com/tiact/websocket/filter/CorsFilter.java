package com.tiact.websocket.filter;

import com.tiact.websocket.utils.util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *  * Cors跨域配置
 * @author Tia_ct
 */
@Slf4j
@Component
public class CorsFilter implements Filter {

	/**
	 * URL拦截
	 */
	private static String[] intercept = {"disk","upload"};
	
	@Override
	public void destroy() {
		log.info("CorsFilter销毁...");
	}


	
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletResponse response = (HttpServletResponse) res;
		HttpServletRequest request=(HttpServletRequest) req;
		String servletPath = request.getServletPath();

		response.addHeader("Access-Control-Allow-Credentials", "true");
		response.addHeader("Access-Control-Allow-Methods", "POST,GET,OPTIONS,DELETE,PUT,HEAD,PATCH");
		response.addHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
		response.addHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"));

		String user = util.getUser();
		log.info(user);
		int router = 0;
		for(String val:intercept){
			if(servletPath.contains(val)){
				if ("12306".equals(user)) {
					router = 0;
				}else{
					router = 1;
				}
				break;
			}
		}
		log.info("--- pass status!! ---"+router);
		//chain.doFilter(req,res);
		switch (router){
			case 0:
				chain.doFilter(req,res);
				break;
			case 1:
				response.sendRedirect("/weChat/");
				break;
		}
	}
	
	@Override
	public void init(FilterConfig arg0){
		log.info("CorsFilter初始化...");
	}


}