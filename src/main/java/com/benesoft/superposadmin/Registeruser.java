package com.benesoft.superposadmin;

//imports
import com.lambdaworks.crypto.SCrypt;
import com.lambdaworks.crypto.SCryptUtil;
import java.sql.*;
import javax.swing.JOptionPane;

/**
 *
 * @author dev
 */
public class Registeruser extends javax.swing.JFrame {
ConnectionClass cls = new ConnectionClass();
    
    
    /**
     * Creates new form Register user
     */
    public Registeruser() {
        initComponents();
    }

    
    //exit sustem
    protected void exitsystem(){
        System.exit(0);
    }
    
    
    /*
    Documentation
    RUN THE PROJECT VIA masterregister METHOD    
    check for empty inputs
    Check password match
    check if user exists
    now register user
    */
    
    /*
    public class ScryptPasswordHashingDemo
{
	public static void main(String[] args) {
		String originalPassword = "password";
		String generatedSecuredPasswordHash = SCryptUtil.scrypt(originalPassword, 16, 16, 16);
		System.out.println(generatedSecuredPasswordHash);

		boolean matched = SCryptUtil.check("password", generatedSecuredPasswordHash);
		System.out.println(matched);

		matched = SCryptUtil.check("passwordno", generatedSecuredPasswordHash);
		System.out.println(matched);
	}
}
    */
    
   
    
    
    
    //master register method
    protected void mastercreateaccount(){
        if(emptyinput() != null){
            JOptionPane.showMessageDialog(null, emptyinput());
            
        }else if(emailfield() != null){//check if email is of the right format
            JOptionPane.showMessageDialog(null, emailfield());
            email.setText("");
            email.requestFocus();
            
        }else if(phonenumber() != null){
            JOptionPane.showMessageDialog(null, phonenumber());
            
        }else if(passwordmatch() != "matched"){//if password did not match
            JOptionPane.showMessageDialog(null, passwordmatch());
            
        }else{//now that nothing is wrong, create account
            if(cls.getConnection() == true){
                createuser();
            }else{
                JOptionPane.showMessageDialog(null, "Failed to establish connection to the server");
            }
        }
    }
    
    
    
    
    
    
    
    
    //check for empty inputs
    protected String emptyinput(){
        if(firstname.getText().isEmpty()){
            firstname.requestFocus();
            return "Please fill in your first name to proceed";
        }else if(surname.getText().isEmpty()){
            surname.requestFocus();
            return "Please enter your surname to proceed";
        }else if(phone.getText().isEmpty()){
            phone.requestFocus();
            return "Please enter your phone number to proceed";
        }else if(email.getText().isEmpty()){
            email.requestFocus();
            return "Please enter your email to proceed";
        }else if(username.getText().isEmpty()){
            username.requestFocus();
            return "Please enter username to proceed";
        }else if(password.getText().isEmpty()){
            password.requestFocus();
            return "Please enter first password to proceed";
        }else if(confirmpass.getText().isEmpty()){
            confirmpass.requestFocus();
            return "Please enter confirmation password to proceed";
        }else{
            return null;
        }
    }
    
    //check if email contains nsome characters 
    protected String emailfield(){
        String mail = email.getText().toLowerCase().trim();
        if(!mail.contains("@")){
            return "Email must contain \"@\" between it. Eg 123@example.com";
        }else if(!mail.contains(".")){
            return "Email must contain \".\" after \"@\". Eg 123@example.com";
        }else{
            return null;
        }
    }
    //check if phone is a number and length
    protected String phonenumber(){
        String number = phone.getText().trim();
        if(number.length() > 10){
            phone.requestFocus();
            return "Invalid phone number. use \"07XXXXXXXX\"";
        }
        try{
            int phone = Integer.parseInt(number);            
            return null;
        }catch(NumberFormatException ne){
            phone.requestFocus();
            return "Phone number should be a number between \"0-9\" ";            
        }
    }
    
    
    //check if passwords match
    protected String passwordmatch(){        
        String pass = password.getText().trim();
        String confirm = confirmpass.getText().trim();
        if(pass.equals(confirm)){
            return "matched";
        }else{
            password.setText(""); confirmpass.setText(""); password.requestFocus();
            return "The two entered passwords did not match";
        }
    }
    
    
    //Return true if the username or email is existing in the system already
    private String userexists(){     
       String user = username.getText().trim();  
       String mail = email.getText().trim();
                try{
                    String query = "SELECT username, emailAddress FROM users WHERE userName = ? || emailAddress=?";
                    PreparedStatement ps = cls.conn.prepareStatement(query);
                    ps.setString(1, user);
                    ps.setString(2, mail);
                    ResultSet rs = ps.executeQuery();
                    if(rs.next()){//if any of the details is taken(true), deny creating account
                        return "Check your email or username. Seems we have a matching account";
                    }else{
                        return null;
                        }            
                }catch(Exception e){
                      return "Oops, something went wrong in getting userdata\n"+e;
                }    
    }
    
    //launch a new user account
    protected void createuser(){
        //initialize
        String fname = firstname.getText().toLowerCase().trim();
        String lname = surname.getText().toLowerCase().trim();
        String mobile = phone.getText().trim();
        String uname = username.getText().toLowerCase().trim();
        String mail = email.getText().toLowerCase().trim();
        String pass = password.getText().toLowerCase().trim();        
        String hashedpw = SCryptUtil.scrypt(pass, 16, 16, 16);        
        if(userexists() == null){
            try{
                String insertquery = "INSERT INTO users(firstName, lastName,emailAddress, userName, password, phoneNumber) VALUES(?,?,?,?,?,?)";
                PreparedStatement pst = cls.conn.prepareStatement(insertquery);
                pst.setString(1, fname);
                pst.setString(2, lname);
                pst.setString(3, mail);
                pst.setString(4, uname);
                pst.setString(5, hashedpw);
                pst.setInt(6, Integer.parseInt(mobile));
                //insert now 
                pst.executeUpdate();
                JOptionPane.showMessageDialog(null, "Congratulations @"+fname+". Your account has been created succesfully. The account will be activated for you to start using it");
                clearinputs();            
            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "Oops, failed to create the new user. Try again");
            } 
        }else{
            JOptionPane.showMessageDialog(null, userexists());
        }
    }

     //clear inputs
    protected void clearinputs(){
        firstname.setText(""); surname.setText(""); username.setText(""); password.setText("");
        confirmpass.setText(""); email.setText(""); phone.setText("");
    }
    
    
    
    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        firstname = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        surname = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        email = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        username = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        password = new javax.swing.JPasswordField();
        confirmpass = new javax.swing.JPasswordField();
        register = new javax.swing.JButton();
        cancel = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        phone = new javax.swing.JTextField();
        gotologin = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("User Registration");
        setResizable(false);
        setSize(new java.awt.Dimension(0, 0));

        jLabel1.setText("First name:");

        jLabel2.setText("Surname:");

        jLabel3.setText("Email:");

        jLabel4.setText("Username:");

        jLabel5.setText("Password:");

        jLabel6.setText("Confirm pass:");

        register.setText("Register");
        register.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                registerActionPerformed(evt);
            }
        });

        cancel.setText("Cancel");
        cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelActionPerformed(evt);
            }
        });

        jLabel7.setText("Mobile:");

        gotologin.setForeground(new java.awt.Color(0, 102, 255));
        gotologin.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        gotologin.setText("Have an account? Login now");
        gotologin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                gotologinMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(firstname)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(email, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(surname)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(phone)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 206, Short.MAX_VALUE)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(username, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addComponent(password, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(confirmpass, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(61, 61, 61)
                                .addComponent(register, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cancel, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 16, Short.MAX_VALUE))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(gotologin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(firstname, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(surname, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(email, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(phone, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(username, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(password, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(confirmpass, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(register, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cancel, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(gotologin)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelActionPerformed
        //call exit ,ethod
        exitsystem();
    }//GEN-LAST:event_cancelActionPerformed

    private void registerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_registerActionPerformed
        //reguister account by calling master create account
        mastercreateaccount();
    }//GEN-LAST:event_registerActionPerformed

    private void gotologinMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gotologinMouseClicked
        //Open login page
        new Authentication().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_gotologinMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        new ThemeClass().theme();
        
        

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Registeruser().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancel;
    private javax.swing.JPasswordField confirmpass;
    private javax.swing.JTextField email;
    private javax.swing.JTextField firstname;
    private javax.swing.JLabel gotologin;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPasswordField password;
    private javax.swing.JTextField phone;
    private javax.swing.JButton register;
    private javax.swing.JTextField surname;
    private javax.swing.JTextField username;
    // End of variables declaration//GEN-END:variables
}
