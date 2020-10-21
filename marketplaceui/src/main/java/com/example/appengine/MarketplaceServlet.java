/*
 * Copyright 2016-2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.appengine;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.interfaces.RSAPublicKey;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

@SuppressWarnings("serial")
// With @WebServlet annotation the webapp/WEB-INF/web.xml is no longer required.
@WebServlet(
    name = "MarketPlaceUi",
    description = "MarketPlaceUi: login",
    urlPatterns = "/marketplacelogin")
public class MarketplaceServlet extends HttpServlet {
  private static final String X_GCP_MARKETPLACE_TOKEN_PARAM = "x-gcp-marketplace-token";
  private static final Logger logger = Logger.getLogger(MarketplaceServlet.class.getName());

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws IOException, ServletException {

    String gcpAccountId = "";
    try {
      String gcpJwtToken = getGcpJwtToken(req);
      gcpAccountId = verifyGcpMarketplaceToken(gcpJwtToken);
      logger.info(gcpAccountId);

    } catch (Exception e) {
      logger.log(Level.SEVERE, "verify token error: {0}", e.getMessage());
      gcpAccountId = "";
    }

    if ((gcpAccountId != null) && !gcpAccountId.isEmpty()) {
      req.setAttribute("account", gcpAccountId);
    } else {
      req.setAttribute("account", "Not present");
    }

    req.getRequestDispatcher("/urlfetchresult.jsp").forward(req, resp);
  }

  private String verifyGcpMarketplaceToken(String gcpJwtToken)
      throws IOException, CertificateException {
    if (gcpJwtToken == null) {
      return null;
    }

    DecodedJWT jwt = JWT.decode(gcpJwtToken);

    // load gcp certificate
    URL url = new URL(jwt.getIssuer());
    BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
    StringBuffer json = new StringBuffer();
    String line;

    while ((line = reader.readLine()) != null) {
      json.append(line);
    }
    reader.close();
    String certsJson = json.toString();
    JSONObject jo = new JSONObject(certsJson);
    String certificateStr = jo.getString(jwt.getKeyId());

    // Get public key from certificate
    InputStream is = new ByteArrayInputStream(certificateStr.getBytes());
    CertificateFactory cf = CertificateFactory.getInstance("X.509");
    Certificate cert = cf.generateCertificate(is);
    RSAPublicKey publicKey = (RSAPublicKey) cert.getPublicKey();

    // Verify token with public key, leave private key as null
    Algorithm algorithm = Algorithm.RSA256(publicKey, null);
    JWTVerifier verifier =
        JWT.require(algorithm)
            .withIssuer(
                "https://www.googleapis.com/robot/v1/metadata/x509/cloud-commerce-partner@system.gserviceaccount.com")
            .withAudience(System.getProperty("jwt.audience"))
            .build();
    jwt = verifier.verify(gcpJwtToken);
    return jwt.getSubject();
  }

  private String getGcpJwtToken(HttpServletRequest request) {
    return request.getParameter(X_GCP_MARKETPLACE_TOKEN_PARAM);
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    req.getRequestDispatcher("/info.jsp").forward(req, resp);
  }
}
