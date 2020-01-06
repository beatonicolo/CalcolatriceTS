<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" 
    "http://www.w3.org/TR/html4/loose.dtd">
<html>
 
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
 <link href="styles/stile.css" rel="stylesheet" type="text/css">
<title>Calculator</title>
</head>
<body>
 
    <div>
    	 <!-- <form action="CalculatorServlet" method="post">-->
    	<form action="CalculatorServletTS">
    		<table class="table">
    			<tr>
    				<td class="display" colspan="4">
    					<input type="text" name="display" value="${resultStr}" readonly>
    					<!--  <input type="text" name="display" value="${param.resultStr}" readonly>-->
    				</td>
    			</tr>
    			<tr>
   					<td><input type="submit" class="smallButton greyButton" name="button" value="mrc"></td>
 					<td><input type="submit" class="smallButton greyButton" name="button" value="m-"></td>
 					<td><input type="submit" class="smallButton greyButton" name="button" value="m+"></td>
 					<td><input type="submit" class="bigButton redButton" name="button" value="/"></td>
 				</tr>
   				<tr>
   					<td><input type="submit" class="smallButton darkButton" name="button" value="7"></td>
 					<td><input type="submit" class="smallButton darkButton" name="button" value="8"></td>
 					<td><input type="submit" class="smallButton darkButton" name="button" value="9"></td>
 					<td><input type="submit" class="bigButton redButton" name="button" value="*"></td>
 				</tr>
 				<tr>   	
  					<td><input type="submit" class="smallButton darkButton" name="button" value="4"></td>
 					<td><input type="submit" class="smallButton darkButton" name="button" value="5"></td>
 					<td><input type="submit" class="smallButton darkButton" name="button" value="6"></td>
  					<td><input type="submit" class="bigButton redButton" name="button" value="-"></td>
  				</tr>
  				<tr>
  					<td><input type="submit" class="smallButton darkButton" name="button" value="1"></td>
  					<td><input type="submit" class="smallButton darkButton" name="button" value="2"></td>
  					<td><input type="submit" class="smallButton darkButton" name="button" value="3"></td>
  					<td><input type="submit" class="bigButton redButton" name="button" value="+"></td>
  				</tr>
  				<tr>
 					<td><input type="submit" class="smallButton darkButton" name="button" value="0"></td>
 					<td><input type="submit" class="smallButton darkButton" name="button" value="."></td>
 					<td><input type="submit" class="smallButton darkButton" name="button" value="="></td>
 					<td><input type="submit" class="bigButton orangeButton" name="button" value="C"></td>
 				</tr>
 			</table>	
 		</form>
 	
 	
    </div>
 
</body>
</html>