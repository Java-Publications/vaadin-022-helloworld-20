/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.rapidpm.microservice;

import java.time.Duration;
import java.time.LocalDateTime;

import org.rapidpm.ddi.DI;
import org.rapidpm.microservice.optionals.ActiveUrlsDetector;
import org.rapidpm.microservice.optionals.header.ActiveUrlPrinter;
import org.rapidpm.microservice.optionals.header.HeaderScreenPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Main {

  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

  public static final String DEFAULT_HOST = "0.0.0.0";
  private static final String DI_PACKAGE_FILE = "microservice.packages";


  private Main() {
  }

  public static void main(String[] args) {
    deploy();
  }


  public static void deploy() {
    final String packages = Main.class.getPackage().getName().replace("." , "/") + "/" + DI_PACKAGE_FILE;
    String property = System.getProperty(DI.ORG_RAPIDPM_DDI_PACKAGESFILE , packages);
    System.setProperty(DI.ORG_RAPIDPM_DDI_PACKAGESFILE , property);


    final LocalDateTime dependencyBootstrapStart = LocalDateTime.now();
    DI.bootstrap();
    final LocalDateTime dependencyBootstrapStop = LocalDateTime.now();

    final LocalDateTime deployStart = LocalDateTime.now();
    MainUndertow.deploy();
    final LocalDateTime deployStop = LocalDateTime.now();

    printFooter(dependencyBootstrapStart , dependencyBootstrapStop , deployStart , deployStop);

  }

  private static void printFooter(LocalDateTime dependencyBootstrapStart , LocalDateTime dependencyBootstrapStop , LocalDateTime deployStart , LocalDateTime deployStop) {

    new HeaderScreenPrinter().printOnScreen();
    new ActiveUrlPrinter().printActiveURLs(new ActiveUrlsDetector().detectUrls());

    final long startupDDI = Duration.between(dependencyBootstrapStart , dependencyBootstrapStop).toMillis();
    final long startupUndertow = Duration.between(deployStart , deployStop).toMillis();
    final long startupComplete = Duration.between(dependencyBootstrapStart , deployStop).toMillis();

    final String ddi = String.format("%1$4s" , startupDDI);
    final String undertow = String.format("%1$4s" , startupUndertow);
    final String complete = String.format("%1$4s" , startupComplete);

    System.out.println("");
    System.out.println("");
    System.out.println(" ############  Startup finished  = " + deployStop + " ############  ");
    System.out.println(" ############  DDI      = " + ddi      + " [ms]                        ############");
    System.out.println(" ############  Undertow = " + undertow + " [ms]                        ############");
    System.out.println(" ############  Complete = " + complete + " [ms]                        ############");
    System.out.println(" ###############################  Enjoy ###############################");
  }

  public static void stop() {
    MainUndertow.stop();
  }
}
