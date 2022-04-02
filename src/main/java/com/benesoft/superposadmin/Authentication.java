package com.benesoft.superposadmin;

//imports
import com.lambdaworks.crypto.SCryptUtil;
import java.awt.event.KeyEvent;
import java.sql.*;
import java.text.DateFormat;
import javax.swing.JOptionPane;

//imports for mail
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


/**
 *
 * @author dev
 */
public class Authentication extends javax.swing.JFrame implements Runnable{
    @Override
    public void run(){               
        Properties prop = new Properties();
           prop.put("mail.smtp.host", "smtp.gmail.com");
           prop.put("mail.smtp.port", "587");
           prop.put("mail.smtp.auth", "true");
           prop.put("mail.smtp.starttls.enable", "true"); //TLS           
           Session session = Session.getInstance(prop, new javax.mail.Authenticator() {
           protected PasswordAuthentication getPasswordAuthentication() {
           return new PasswordAuthentication(cls.emailuser, cls.emailpass);
           }
           });
           
           
            //now send email if it has been authenticated 
            try{
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(cls.emailuser));
                message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(emailto));//email of the recipient gotten from input
                message.setSubject("OTP AUTH");
                message.setText("Please use the below code for login.\n\nSafety tips!\nPlease do not share this code with anyone. Also make sure your email is not open to friends.\n"+ theotp);
                Transport.send(message);
                
            }catch(MessagingException e) {
                JOptionPane.showMessageDialog(null, "OTP email has not been send. Possible reasons: Email was not fetched or it was wrong");
            }//END SEND9NG EMAIL TO THE USER*/
    }
    //the above run method runs on a separate thread so that email sending will not cause lag
           
           
    
    
    
//globals
ConnectionClass cls = new ConnectionClass();
static String theotp;
static String emailto;
static String userrole;
static String pwd;
    
    /**
     * Creates new form Authentication
     */
    public Authentication() {
        initComponents();
        mfawindow.setVisible(false);
    }
    
    
    
    //exit the system
    protected void exit(){
        System.exit(0);
    }
    
    
    
    /*
    Documentation
        THE WHOLE PROCESS WILL BE DONE AFTER CALLING THE masterlogin METHOD, below is the process
        NB: user must be ACTIVE and EXISTING  to be allowed login

        What happens during login
        check if username and password fields are filled
        check if the user exists
        check if username entered matches the saved pass
        if matches, open the MFA window and ask for code
        when the code entred is correct, get the session username and log them in
        NB: the user exists will return the username, password in hash and email

        What happens 
        The code is auto generated using random 4 digit number
        the code is saved to db, when saved, now send an email to the user with the same code.
        we are going to have a separate method to send the email    
    */
    
    
    //the master method
    protected void masterlogin(){
        /*
        We begin with chcking for empty fields
        Then check if db conection is established
        then login user
        update the last login, 
        check mfa
        */
        if(usernameempty() == true){//check username
            JOptionPane.showMessageDialog(null, "The username field cant be empty");
            
        }else if(passwordempty() == true){//check password
            JOptionPane.showMessageDialog(null, "The password field cant be empty");
            
        }else{//check connection and login
            if(cls.getConnection() == true){//call login user method
                loginuser();
            }else{
                JOptionPane.showMessageDialog(null, "Failed to establish connection to the server");
            }
        }
    }
    
    
    
    //return true if username is empty
    private boolean usernameempty(){
        String user = username.getText();
        if(user.isEmpty()){            
            return true;
        }else{
            return false;
        }
    }
    
    //return true if password is empty
    private boolean passwordempty(){
        String pass = password.getText();
        if(pass.isEmpty()){            
            return true;
        }else{
            return false;
        }
    }
    
    //check if user exists
    private String[] userexists(){
        String[] nulluser = {"This user does not exist or has not yet been activated. Please contact admin"};  
        String[] queryerror = {"There was a problem in getting user data. The server may be down. try again"};
               try{
                    String query = "SELECT userName,password, emailAddress, usertype,status FROM users WHERE userName = ? && status=?";
                    PreparedStatement ps = cls.conn.prepareStatement(query);
                    ps.setString(1, username.getText().trim());
                    ps.setString(2, "active");
                    ResultSet rs = ps.executeQuery();
                    if(rs.next()){ 
                        emailto = rs.getString("emailAddress");
                        userrole = rs.getString("usertype");
                        pwd = rs.getString("password");
                        
                        String[] data = {"exists", emailto, userrole, pwd};
                        return data;
                    }else{
                        return nulluser;
                        }            
               }catch(Exception e){
                    return queryerror;
              }
    }
    
    
    
    //Method to log user in if the user exists and passwords match
    /*-----------------------------------------
    Check if user exists, if so,
    check if passwords match
    if they match, open the mfa, 
    if mfa works, then open the index window.
    if mfa fails, error out
    ----------------------------------------*/
    protected void loginuser(){
        //check if user exists
        if(userexists()[0].equals("exists")){//check if user exists and compare the password
            //put items in variables
            String enteredpassword = password.getText().trim();
            String databasepassword = pwd;            
            
            //now compare to see if the passwords match
            boolean matched = SCryptUtil.check(enteredpassword, databasepassword);
            /*
            Steps that follow now
            Generate an OTP and send email to user using a different thread 
            Open the code area and allow user insert the code                            
            */
                if(matched == true){//if the passwords match, proceed to login activities that follow
                    switch(userrole){//start switch user role for redirection instructions
                        case "admin":
                            savecode();//save the otp to database, then                            
                            //send the user an email
                            new Thread(new Authentication()).start();
                            
                            //Manipulate the mfa entry panel values before showing it
                            String mymail = emailto;
                            String firstcharofemail = String.valueOf(mymail.charAt(0)).toUpperCase();//first charactyer in the email                            
                            String emaildomain = mymail.substring(mymail.lastIndexOf("@"));//get the email domain used                            
                            emailarea.setText("Check email on: "+firstcharofemail+"******"+emaildomain);
                            //now set the mfa entry are to visible by using the opencodearea method
                            opencodearea();//open the mfa code area                            
                            break;
                            
                        default:
                            JOptionPane.showMessageDialog(null, "This user does not have administrative priviledges. you can request the admin to grant permision");
                            break;
                            
                        
                    }//end switch                    
                }else{//if the passwords did not match
                    JOptionPane.showMessageDialog(null, "Sorry, you entered a wrong password. Try again");
                    password.setText("");
                    password.requestFocus();
                }            
        }else{//if the index of return is not #exists, then its some sort of error or null return. show it
            JOptionPane.showMessageDialog(null, userexists());
        }        
    }
    
    
    
    
    
    
    /*------------------------------
    Open the code entry part
    ------------------------------*/
    protected void opencodearea(){
        mfawindow.setVisible(true);
        login.setEnabled(false);
        username.setEditable(false);
        password.setEditable(false);
        otpcodearea.requestFocus();
    }
    
    
    
   
    
    
    
    /*---------------------------------------------------------------
    The otp codes generation & client email begin here
    ---------------------------------------------------------------*/
    //generates 4 digit random code
    public final String otpcode(){
        double x = Math.random();
        String code = String.valueOf(x);
        theotp = code.substring(2,6);
        return theotp;
    }
    
    
    
    //save the code to db
    protected void savecode(){
      String user = username.getText().toLowerCase().trim();
        try{
            String save = "INSERT INTO otpcodes(username, code) VALUES(?,?)";
            PreparedStatement ps = cls.conn.prepareStatement(save);
            ps.setString(1, user);
            ps.setString(2, otpcode().trim());
            ps.executeUpdate();//save the code to database            
            
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "Could not refresh OTP code generator. Please retry");
        }
}


    
    
    //send otp email to the user
    
    
    //then deactivate the code wen the person logs in
    protected void deactivateotp(){
        String user = username.getText().toLowerCase().trim();
        try{
            String update = "UPDATE otpcodes SET status=? WHERE userName=? && status=?";
            PreparedStatement pst = cls.conn.prepareStatement(update);
            pst.setString(1, "inactive");
            pst.setString(2, user);
            pst.setString(3, "active");
            pst.executeUpdate(); //confirm updates now            
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "Error in server. you can ignore this message and proceed");
        }
    }
    
    
    /*-------------------------------------------
    set the last login
    -------------------------------------------*/
    protected void updatelogin(){
        String user = username.getText().toLowerCase().trim();      
        try{
            PreparedStatement apt = cls.conn.prepareStatement("UPDATE users SET loggedin=? WHERE userName=?");
            apt.setInt(1, 1);
            apt.setString(2, user);
            apt.executeUpdate();//update the last login
            
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "Something may not be right. you can howver ignore this message");
        }
    }
    
    
  
     
    /*----------------------------------------------
    Check if entered otp is okay
    ----------------------------------------------*/
    protected void codematched(){
        String user = username.getText().toLowerCase().trim();
        String mycode = otpcodearea.getText().trim();
        if(cls.getConnection() == true){
            try{
                String query = "SELECT * FROM otpcodes WHERE userName = ? && status=? && code=?";
                PreparedStatement pst = cls.conn.prepareStatement(query);
                pst.setString(1, user);
                pst.setString(2, "active");
                pst.setString(3, mycode);
                ResultSet rst = pst.executeQuery();

                if(rst.next()){                
                    deactivateotp();//deactivate the otp
                    updatelogin();//update last login 
                    //Then open the homepage
                    Home hm = new Home();
                    hm.loggedinuser.setText(user);
                    hm.setVisible(true);
                    this.dispose();//then close this page                
                    new Thread(hm).start();//allow the thread run now

                }else{
                    JOptionPane.showMessageDialog(null, "Wrong OTP code. Click resend now and try again");
                }
            }catch(Exception e){
               JOptionPane.showMessageDialog(null, "Oops, an error occured in getting OTP code. Please try again"); 
            }
        }else{
            JOptionPane.showMessageDialog(null, "Server connection was lost");
        }        
    }
    
    
    
    
    
    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        username = new javax.swing.JTextField();
        password = new javax.swing.JPasswordField();
        login = new javax.swing.JButton();
        cancel = new javax.swing.JButton();
        forgotpass = new javax.swing.JLabel();
        mfawindow = new javax.swing.JPanel();
        emailarea = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        otpcodearea = new javax.swing.JTextField();
        proceedlogin = new javax.swing.JButton();
        resendcode = new javax.swing.JLabel();
        gotoregister = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Admin Login");
        setAlwaysOnTop(true);
        setResizable(false);

        jLabel1.setText("Enter Username:");

        jLabel2.setText("Password:");

        username.setNextFocusableComponent(password);
        username.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                usernameKeyReleased(evt);
            }
        });

        password.setNextFocusableComponent(login);
        password.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                passwordKeyReleased(evt);
            }
        });

        login.setText("Login");
        login.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loginActionPerformed(evt);
            }
        });

        cancel.setText("Cancel");
        cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelActionPerformed(evt);
            }
        });

        forgotpass.setForeground(new java.awt.Color(255, 0, 51));
        forgotpass.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        forgotpass.setText("Forgot password? Reset here..");
        forgotpass.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        mfawindow.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 204, 0), 1, true));

        emailarea.setFont(new java.awt.Font("Laksaman", 0, 15)); // NOI18N
        emailarea.setForeground(new java.awt.Color(0, 102, 255));
        emailarea.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        emailarea.setText("*");

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Enter code then click proceed to login");

        otpcodearea.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 204, 0), 1, true));
        otpcodearea.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                otpcodeareaKeyReleased(evt);
            }
        });

        proceedlogin.setBackground(new java.awt.Color(51, 204, 0));
        proceedlogin.setForeground(new java.awt.Color(255, 255, 255));
        proceedlogin.setText("Proceed");
        proceedlogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                proceedloginActionPerformed(evt);
            }
        });

        resendcode.setFont(new java.awt.Font("Laksaman", 0, 15)); // NOI18N
        resendcode.setForeground(new java.awt.Color(255, 102, 153));
        resendcode.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        resendcode.setText("Didnt receive code? resend ");
        resendcode.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        javax.swing.GroupLayout mfawindowLayout = new javax.swing.GroupLayout(mfawindow);
        mfawindow.setLayout(mfawindowLayout);
        mfawindowLayout.setHorizontalGroup(
            mfawindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mfawindowLayout.createSequentialGroup()
                .addGroup(mfawindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(resendcode, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mfawindowLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(mfawindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(emailarea, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE)
                            .addGroup(mfawindowLayout.createSequentialGroup()
                                .addComponent(otpcodearea)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(proceedlogin, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(13, 13, 13)))))
                .addContainerGap())
        );
        mfawindowLayout.setVerticalGroup(
            mfawindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mfawindowLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(emailarea, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mfawindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(otpcodearea, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(proceedlogin, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
                .addComponent(resendcode)
                .addContainerGap())
        );

        gotoregister.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        gotoregister.setText("Do not have an account? Create one now");
        gotoregister.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                gotoregisterMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(10, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(forgotpass, javax.swing.GroupLayout.PREFERRED_SIZE, 304, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(login, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cancel, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(username, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(password, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 295, Short.MAX_VALUE)))
                        .addGap(29, 29, 29))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(gotoregister, javax.swing.GroupLayout.PREFERRED_SIZE, 319, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(mfawindow, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(14, 14, 14))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(username, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(password, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(login, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cancel, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(forgotpass)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(gotoregister)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(mfawindow, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    
    
    
    private void cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelActionPerformed
        //Call the logout method
        exit();
    }//GEN-LAST:event_cancelActionPerformed

    private void loginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loginActionPerformed
        //call the master login method
        masterlogin();
    }//GEN-LAST:event_loginActionPerformed

    private void usernameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_usernameKeyReleased
        //focus on password if pressed key is enter
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            password.requestFocus();            
        }
    }//GEN-LAST:event_usernameKeyReleased

    private void passwordKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_passwordKeyReleased
        //login method is called if enter key is pressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            masterlogin();
        }
    }//GEN-LAST:event_passwordKeyReleased

    private void proceedloginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_proceedloginActionPerformed
        // call the get code method
        codematched();
    }//GEN-LAST:event_proceedloginActionPerformed

    private void otpcodeareaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_otpcodeareaKeyReleased
        // TODO add your handling code here:
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            codematched();
        }
    }//GEN-LAST:event_otpcodeareaKeyReleased

    private void gotoregisterMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gotoregisterMouseClicked
        //open user registration page
        new Registeruser().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_gotoregisterMouseClicked

    
    
    
    
    
   //main method goes here
    public static void main(String args[]) {
       new ThemeClass().theme();
       

       
       
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Authentication().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancel;
    private javax.swing.JLabel emailarea;
    private javax.swing.JLabel forgotpass;
    private javax.swing.JLabel gotoregister;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JButton login;
    private javax.swing.JPanel mfawindow;
    private javax.swing.JTextField otpcodearea;
    private javax.swing.JPasswordField password;
    private javax.swing.JButton proceedlogin;
    private javax.swing.JLabel resendcode;
    private javax.swing.JTextField username;
    // End of variables declaration//GEN-END:variables
}
