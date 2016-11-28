<%@ page language="java" import="java.util.*" contentType="text/html; charset=utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML>
<html>
  <head>
    <base href="<%=basePath%>">
	<link rel="stylesheet" href="<%=path %>/statics/css/reset.css">
    <link rel="stylesheet" href="<%=path %>/statics/css/main.css">
    <script type="text/javascript" src="<%=path %>/statics/js/main.js"></script>
    <title>ibeacon_demo2</title>
  </head>

  <body>
  	<div id="header">
    <p>输入id:
    <input type="text" id="beaconid" value="">
    <button id="mybutton">添加Beacon</button>
    </p>
    </div>
    <div id="origin-dot"></div>
    <div id="msg_box">
    </div>
      <div class="picture" id="picture">
    </div>
  </body>
  
</html>
