package beato.calculatorTS.test;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import beato.calc.util.Expression;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * 
 * Implementazione Servlet di una clacolatrice base
 * @author Nicolo Beato
 *
 */
public class CalculatorServletTS extends HttpServlet {
	
	
	private static final long serialVersionUID = 1L;
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	static final String DB_URL = "jdbc:mysql://localhost/HISTORYCALC?useLegacyDatetimeCode=false&serverTimezone=CET";
	//  Database credentials
	static final String USER = "username";
	static final String PASS = "password";
	//queries
	static final String query = "INSERT INTO storico (op1,op2,operator,result,stamp)VALUES(?,?,?,?,?)";
	
	private String REST_SERVICE_URL = "http://localhost:8080/CalcolatriceRest/rest/CalcolatriceService/calcola";
	private Client client = ClientBuilder.newClient();
	
	public CalculatorServletTS() {};
	
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession currentSession=request.getSession();
		
		double op1=0.0;//variabile contenente un risultato parziale
		double op2=0.0;//operando che si sta inserendo o è appena stato inserito
		double valueToDisplay=0.0;//varibile contenete il valore che voglio mostrare nel display
		char suspendedOperator=' ';//variabile contenente l'ultimo operatore inserito in attesa del secondo operando
		boolean consecutiveOp=false;//guardia atta a controllare che non vengano valutati consecutivamente due operatori
		String caller=request.getParameter("button");
		
		//if(currentSession.getCreationTime()>25min) invalidarla
		
		currentSession.setMaxInactiveInterval(15);
		
		if(!currentSession.isNew()) {
			if (currentSession.getAttribute("op1") != null)
				op1= (double) currentSession.getAttribute("op1");
			if (currentSession.getAttribute("op2") != null)
				op2= (double) currentSession.getAttribute("op2");
			if ((currentSession.getAttribute("suspendedOperator") != null))
				suspendedOperator= (char) currentSession.getAttribute("suspendedOperator");
			if ((currentSession.getAttribute("consecutiveOp") != null))
				consecutiveOp= (boolean) currentSession.getAttribute("consecutiveOp");
			}
		
		//controllo se il pulsante premuto è un numero o un operatore 
		if ((caller.equalsIgnoreCase("+"))||caller.equalsIgnoreCase("-")||caller.equalsIgnoreCase("*")||caller.equalsIgnoreCase("/")||caller.equalsIgnoreCase("=")) 
			{
			//se vengo premuti consecutivamente 2 operatori scarto il meno recente, altrimenti eseguo operazione
			if(!consecutiveOp) {
				//op1=enter(op1,op2,suspendedOperator);
				op1=getRes(op1,op2,suspendedOperator);
				valueToDisplay=op1;
				op2=0.0;
				}
			suspendedOperator=caller.charAt(0);
			consecutiveOp=true;
				}
		//se viene premuto il tasto c resetto la calcolatrice
		else if (caller.equalsIgnoreCase("C")||caller.equalsIgnoreCase("mrc")||caller.equalsIgnoreCase("m-")||caller.equalsIgnoreCase("m+")||caller.equalsIgnoreCase(".")) {
			op1=0.0;
			op2=0.0;
			suspendedOperator=' ';
			consecutiveOp=false;
		}
		//altrimenti vuol dire che è stato premuto un pulsante numerico
		else
			{
				op2=digit(op2,caller);
				consecutiveOp=false;
				valueToDisplay=op2;
			}
		
		//salvo all'interno della sessione le variabili
		currentSession.setAttribute("resultStr",valueToDisplay);
		currentSession.setAttribute("op1",op1);
		currentSession.setAttribute("op2",op2);
		currentSession.setAttribute("suspendedOperator",suspendedOperator);
		currentSession.setAttribute("consecutiveOp",consecutiveOp);
		
		request.getRequestDispatcher("/index.jsp").forward(request, response);
	}
	
	/**
	 * metodo che processa l'inserimento di cifre per formare il numero richiesto
	 * @param number
	 */
	private double digit(double op2,String caller) {
		double n= Double.valueOf(caller);
		op2*=10;
		op2+=n;
	
		return op2;
	}
	
	
	public void insertRecordDBetter(double op1, double op2, char operator, double result) {
		Connection conn = null;
		PreparedStatement prpstmt = null;
		
		java.util.Date date=new java.util.Date();
		Timestamp timestamp = new Timestamp( date.getTime());
		
		try {
			Class.forName(JDBC_DRIVER);
			conn=DriverManager.getConnection( DB_URL, USER,PASS);
			
			prpstmt=conn.prepareStatement(query);
			prpstmt.setDouble(1, op1);
			prpstmt.setDouble(2, op2);
			prpstmt.setString(3, ""+operator);
			prpstmt.setDouble(4, result);
			prpstmt.setTimestamp(5, timestamp);
			
			prpstmt.executeUpdate();
		}
		catch (ClassNotFoundException e) {e.printStackTrace();}
		catch  ( SQLException sqle ) {sqle.printStackTrace();}
		finally {
			try{
		         if(prpstmt!=null)
		            prpstmt.close();}
			catch(SQLException se2){se2.printStackTrace();}
			try {
				if(conn!=null)
					conn.close();}
			catch(SQLException se){
		         se.printStackTrace();}
		}
	}

	
	private double getRes(double op1,double op2,char suspendedOperator){
		//sostituisco il carattere '/' con 'd' poichè dava un errore in quanto interferiva con l'URI
		//per inviare la richiesta al service
		if (suspendedOperator=='/')
			suspendedOperator='d';
		Expression userExp=new Expression(op1,op2,suspendedOperator);

		Gson gson = new GsonBuilder().create();
		String jsonExp = gson.toJson(userExp);
		String output=client.target(REST_SERVICE_URL).path("/{exp}").resolveTemplate("exp", jsonExp).request().get(String.class);

		double result= ((Expression)gson.fromJson(output, Expression.class)).getResult();
		insertRecordDBetter(op1,op2,suspendedOperator,result);
		return result;
	}
}
