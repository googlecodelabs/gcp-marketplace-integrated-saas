<%--
Copyright 2016-2020 Google LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<!-- [START base] -->
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<html lang="en">
<head>
    <title>Marketplace Sample</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
</head>
<body>
    <h1 align="center">Marketplace Sample</h1>
    <c:if test="${not empty account}">
        <h2>Procurement Account Id: ${account}</h2>
    </c:if>

    <c:if test="${not empty error}">
        <h2 style="color:red">${error}</h2>
    </c:if>

    <c:if test="${not empty response}">
      <p>${response}</p>
    </c:if>

</body>
</html>
<!-- [END base]-->
