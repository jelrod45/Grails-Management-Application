<html>
<head>
  <style type='text/css' media='screen'>
    #login {
      margin: 15px 0px;
      padding: 0px;
      text-align: center;
    }
    #formTable {
      margin-left: auto;
      margin-right: auto;
      font-weight: bold;
      font-family: monospace;
      font-size: 13px;
    }
  </style>
</head>


<body style='background-color: #CCEEFF'>
<div id='login'>
  <form action='${postUrl}' method='POST' id='loginForm' class='cssform' autocomplete='off'>
    <table id='formTable'>
      <tr colspan=2 style='height: 16px'><img src='http://rapidcommittee.com/static/kJC8yvlXeMw0SeONV3a4WfTNBuXAGvqDmXfwuHYZAJ.png'></tr>
      <tr>
	<td style='width :100px; height: 40px;'><label for='username'><g:message code="springSecurity.login.username.label"/>:</label></td>
	<td><input type='text' class='text_' name='j_username' id='username'/></td>
      </tr>
      <tr>
	<td style='width: 100px; height: 40px'><label for='password'><g:message code="springSecurity.login.password.label"/>:</label></td>
	<td><input type='password' class='text_' name='j_password' id='password'/></td>
      </tr>
      <tr>
	<td colspan=2 style='text-align: center'><input type='submit' id="submit" value='${message(code: "springSecurity.login.button")}' style='background-color: #FFFFFF; font-weight: bold'/></td>
      </tr>
    </table>
  </form>
</div>
<script type='text/javascript'>
	(function() {
		document.forms['loginForm'].elements['j_username'].focus();
	})();
</script>
</body>
</html>
