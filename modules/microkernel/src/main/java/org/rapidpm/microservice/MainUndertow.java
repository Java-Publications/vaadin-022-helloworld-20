package org.rapidpm.microservice;

import static io.undertow.UndertowOptions.ENABLE_HTTP2;
import static io.undertow.servlet.Servlets.defaultContainer;
import static io.undertow.servlet.Servlets.deployment;
import static io.undertow.servlet.Servlets.servlet;
import static javax.servlet.DispatcherType.ERROR;
import static javax.servlet.DispatcherType.FORWARD;
import static javax.servlet.DispatcherType.INCLUDE;
import static javax.servlet.DispatcherType.REQUEST;

import java.util.EventListener;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

import org.apache.shiro.web.env.EnvironmentLoaderListener;
import org.apache.shiro.web.servlet.ShiroFilter;
import org.rapidpm.ddi.DI;
import org.rapidpm.ddi.reflections.ReflectionUtils;
import org.rapidpm.microservice.servlet.ServletInstanceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.Undertow.Builder;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.FilterInfo;
import io.undertow.servlet.api.ListenerInfo;
import io.undertow.servlet.api.ServletContainer;
import io.undertow.servlet.api.ServletInfo;

/**
 * Copyright (C) 2010 RapidPM
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 * Created by RapidPM - Team on 08.08.16.
 */
public class MainUndertow {

  private MainUndertow() {
  }

  private static final Logger LOGGER = LoggerFactory.getLogger(MainUndertow.class);

  public static final String DEFAULT_FILTER_MAPPING = "/*";
  public static final String DEFAULT_SHIRO_FILTER_NAME = "ShiroFilter";
  public static final String MYAPP = "/microservice";
  public static final int DEFAULT_SERVLET_PORT = 7080;
  public static final String SERVLET_PORT_PROPERTY = "org.rapidpm.microservice.servlet.port";
  public static final String SERVLET_HOST_PROPERTY = "org.rapidpm.microservice.servlet.host";
  public static final String SHIRO_ACTIVE_PROPERTY = "org.rapidpm.microservice.security.shiro.active";


  private static Undertow undertowServer;

  public static void deploy() {

    final Builder builder = Undertow.builder()
                                    .setDirectBuffers(true)
                                    .setServerOption(ENABLE_HTTP2 , true);

    deployServlets(builder);
    undertowServer = builder.build();
    undertowServer.start();
  }


  private static DeploymentInfo createServletDeploymentInfos() {

    final Set<Class<?>> typesAnnotatedWith = DI.getTypesAnnotatedWith(WebServlet.class , true);

    final List<ServletInfo> servletInfos = typesAnnotatedWith
        .stream()
        .filter(s -> new ReflectionUtils().checkInterface(s , HttpServlet.class))
        .map(c -> {
          Class<HttpServlet> servletClass = (Class<HttpServlet>) c;
          final ServletInfo servletInfo = servlet(c.getSimpleName() , servletClass , new ServletInstanceFactory<>(servletClass));
          if (c.isAnnotationPresent(WebInitParam.class)) {
            final WebInitParam[] annotationsByType = c.getAnnotationsByType(WebInitParam.class);
            for (WebInitParam webInitParam : annotationsByType) {
              final String value = webInitParam.value();
              final String name = webInitParam.name();
              servletInfo.addInitParam(name , value);
            }
          }
          final WebServlet annotation = c.getAnnotation(WebServlet.class);
          final String[] urlPatterns = annotation.urlPatterns();
          for (String urlPattern : urlPatterns) {
            servletInfo.addMapping(urlPattern);
          }
          servletInfo.setAsyncSupported(annotation.asyncSupported());
          servletInfo.setLoadOnStartup(annotation.loadOnStartup());
          return servletInfo;
        })
        .filter(servletInfo -> ! servletInfo.getMappings().isEmpty())
        .collect(Collectors.toList());

    final List<ListenerInfo> listenerInfos = DI.getTypesAnnotatedWith(WebListener.class)
                                         .stream()
                                         .map(c -> new ListenerInfo((Class<? extends EventListener>) c))
                                         .collect(Collectors.toList());

    final DeploymentInfo deploymentInfo = deployment()
        .setClassLoader(Main.class.getClassLoader())
        .setContextPath(MYAPP)
        .setDeploymentName("ROOT" + ".war")
        .setDefaultEncoding("UTF-8");

    final Boolean shiroActive = Boolean.valueOf(System.getProperty(SHIRO_ACTIVE_PROPERTY , "true"));
    if (shiroActive) addShiroFilter(deploymentInfo , DEFAULT_SHIRO_FILTER_NAME , DEFAULT_FILTER_MAPPING);

    return deploymentInfo
        .addListeners(listenerInfos)
        .addServlets(servletInfos);
  }

  static void deployServlets(final Builder builder) {
    final ServletContainer servletContainer = defaultContainer();

    final DeploymentManager manager = servletContainer.addDeployment(createServletDeploymentInfos());
    manager.deploy();

    try {
      final HttpHandler servletHandler = manager.start();

      final PathHandler pathServlet = Handlers
          .path(Handlers.redirect(MYAPP))
          .addPrefixPath(MYAPP , servletHandler);

      final String realServletPort = System.getProperty(SERVLET_PORT_PROPERTY , DEFAULT_SERVLET_PORT + "");
      final String realServletHost = System.getProperty(SERVLET_HOST_PROPERTY , Main.DEFAULT_HOST);

      builder.addHttpListener(Integer.parseInt(realServletPort) , realServletHost , pathServlet);

    } catch (ServletException e) {
      e.printStackTrace();
    }

  }

  static DeploymentInfo addShiroFilter(DeploymentInfo deploymentInfo ,
                                       String shiroFilterName ,
                                       String shiroShiroFilterMappin) {
    return deploymentInfo.addListener(new ListenerInfo(EnvironmentLoaderListener.class))
                         .addFilter(new FilterInfo(shiroFilterName , ShiroFilter.class))
                         .addFilterUrlMapping(shiroFilterName , shiroShiroFilterMappin , REQUEST)
                         .addFilterUrlMapping(shiroFilterName , shiroShiroFilterMappin , FORWARD)
                         .addFilterUrlMapping(shiroFilterName , shiroShiroFilterMappin , INCLUDE)
                         .addFilterUrlMapping(shiroFilterName , shiroShiroFilterMappin , ERROR);
  }


  public static void stop() {

    if (undertowServer != null) {
      try {
        undertowServer.stop();
      } catch (Exception e) {
        LOGGER.error("undertowServer.stop()" , e);
      }
    }
  }
}
