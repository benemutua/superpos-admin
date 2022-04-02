package com.benesoft.superposadmin;
import java.sql.*;


public class ConnectionClass {
    String user = "benedictAdmin";
    String pass = "22"; 
    Connection conn;
    String dbUrl = "jdbc:mysql://localhost:3306/testingdb";
    //email credentials
    String emailuser = "22";
    String emailpass = "22";
    
  
public boolean getConnection(){    
    
        try{
            conn = DriverManager.getConnection(dbUrl, user, pass);
            return true;            
        }catch(Exception e){
            return false;
        }
    }
}