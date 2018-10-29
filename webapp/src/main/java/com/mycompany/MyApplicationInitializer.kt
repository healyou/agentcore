package com.mycompany

import org.apache.wicket.protocol.http.WicketFilter
import org.springframework.web.WebApplicationInitializer
import org.springframework.web.context.ContextLoaderListener
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext

import javax.servlet.*
import java.util.EnumSet

/**
 * @author Nikita Gorodilov
 */
class MyApplicationInitializer : WebApplicationInitializer {

    @Throws(ServletException::class)
    override fun onStartup(servletContext: ServletContext) {
        val rootContext = AnnotationConfigWebApplicationContext()
        rootContext.register(JdbcConfig::class.java)
        rootContext.register(ApplicationConfig::class.java)
        servletContext.addListener(ContextLoaderListener(rootContext))

        val wicketFilter = object : WicketFilter(MyAuthenticatedWebApplication()) {
            @Throws(ServletException::class)
            override fun init(isServlet: Boolean, filterConfig: FilterConfig) {
                filterPath = ""
                super.init(isServlet, filterConfig)
            }
        }
        val registration = servletContext.addFilter("wicketFilter", wicketFilter)
        registration.setInitParameter("wicketFilter", "/")
        registration.setInitParameter("applicationClassName", MyAuthenticatedWebApplication::class.java.name)
        registration.addMappingForUrlPatterns(null, true, "*")
    }
}
