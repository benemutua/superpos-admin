package com.benesoft.superposadmin;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;

/**
 *
 * @author dev
 */
public class Home extends javax.swing.JFrame implements Runnable{
    @Override
    public void run(){
        //runlater();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            JOptionPane.showMessageDialog(null, "Failed to populate tally information"+ex);
        }
       masterMethod(); 
    }
    
    
    
    //GLOBAL DECLARATIONS
    Products pds = new Products();
    Salessummary sl = new Salessummary();
    ConnectionClass cls = new ConnectionClass();
    int infoBtn = JOptionPane.INFORMATION_MESSAGE;
    static final Home hm = new Home();
    
    /**
     * Creates new form Home
     */
    public Home() {
        initComponents();
        //maximize 
         this.setExtendedState(this.MAXIMIZED_BOTH);
    }

    
    
    
    
    /*================================================
    THE BANNER COUNT ITEMS AT THE BEGINNING OF LAYOUT
    ================================================*/
    protected void salesToday(){
        DateFormat dateFormat = new SimpleDateFormat("y-MM-dd");
        Date date = new Date();
        String todaysdate = dateFormat.format(date);
            try{
                String query = "SELECT count(transactioncode)transactioncode,status, saleDate FROM sales WHERE saleDate = ? && status=?";
                PreparedStatement ps = cls.conn.prepareStatement(query);
                ps.setString(1, todaysdate);
                ps.setString(2, "completed");
                ResultSet rs = ps.executeQuery();
                while(rs.next()){
                    salestoday.setText(rs.getString("transactionCode"));
                }
            }catch(Exception e){
                JOptionPane.showMessageDialog(null, e);
            }
    }
     
    //GET THE MONTLY SALE
    protected void monthSale(){
        DateFormat dateFormat = new SimpleDateFormat("y-MM-dd");
        Date date = new Date();
        String todaysdate = dateFormat.format(date);
        String thismonth = todaysdate.substring(0,7);
                
        try{
                String query = "SELECT count(transactioncode)transactioncode,status, saleDate FROM sales WHERE saleDate LIKE ? && status=?";
                PreparedStatement ps = cls.conn.prepareStatement(query);
                ps.setString(1, thismonth+"%");
                ps.setString(2, "completed");
                ResultSet rs = ps.executeQuery();
                while(rs.next()){
                    monthlysales.setText(rs.getString("transactionCode"));
                }
            }catch(Exception e){
                JOptionPane.showMessageDialog(null, e);
            }
    }    
    
    
    //the products low in stock. Generally this will show where products quantity is less than 50 pieces
    protected void lowStock(){
        try{
            String countlowstock = "SELECT count(productCode)productCode, productQuantity FROM products WHERE productQuantity < ?";
            PreparedStatement ps = cls.conn.prepareStatement(countlowstock);
            ps.setInt(1, 50);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                itemslowinstock.setText(rs.getString("productCode"));
            }            
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "Error calculating items low in stock\n"+e);
        }
    }
    
    
    //this method counts all products in existence on catalog
    protected void productsCatalog(){
        try{
            String prodcatalog = "SELECT count(productCode)productCode FROM products";
            PreparedStatement pst = cls.conn.prepareStatement(prodcatalog);
            ResultSet rs = pst.executeQuery();
            while(rs.next()){
                allproducts.setText(rs.getString("productCode"));
            }            
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "Error populating products catalog tally\n"+e);
        }
    }
    
    
    
    //logged in users
    protected void loggedinusers(){
        try{
            String query = "SELECT COUNT(userName)userName, loggedin FROM users WHERE loggedin=?";
            PreparedStatement ps = cls.conn.prepareStatement(query);
            ps.setInt(1, 1);
            ResultSet rst = ps.executeQuery();
            while(rst.next()){
                loggeininusers.setText(rst.getString("userName"));
            }            
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "Failed to get the logged in users");
        }
    }
   
    
    //THE MASTER METHOD TO POPULATE VALUES
    public void masterMethod(){
         if(cls.getConnection() == true){
             salesToday();
             monthSale();
             lowStock();
             productsCatalog();
             loggedinusers();
         }else{
             JOptionPane.showMessageDialog(null, "Failed to establish connection to the server");
         }
    }
   /*====================================
    END POPULATING THE BANNER 
    ===================================*/
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    //OPENS THE MANAGE STOCK PAGE
    public void openManageStock(){        
        pds.setVisible(true);
        pds.prodTabs.setSelectedIndex(1);
    }
    
    private void openInventory(){      
        pds.setVisible(true);
        pds.prodTabs.setSelectedIndex(0);
    }
    private void openProducts(){
        AllProducts products = new AllProducts();   
        products.setVisible(true);
        products.run();
    }
    
    private void openSales(){
        sl.setVisible(true);
    }
    
    private void openSalesTrend(){
       sl.setVisible(true);
    }
    
    
    /*------------------------------------
    the file menu methods
    -------------------------------------*/
    protected void exitsystem(){
        String user = loggedinuser.getText().trim();
        if(cls.getConnection() == true){
            try{
                PreparedStatement ps = cls.conn.prepareStatement("UPDATE users SET loggedin = ? WHERE userName=?");
                ps.setInt(1, 0);
                ps.setString(2, user);
                ps.executeUpdate();//transact db                
                System.exit(0);//exit system if succesful
                
            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "Failed to logout");
            }            
        }else{
            JOptionPane.showMessageDialog(null, "Failed to establish connection to server");
        }
        System.exit(0);
    }
    
    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        salestoday = new javax.swing.JTextField();
        monthlysales = new javax.swing.JTextField();
        itemslowinstock = new javax.swing.JTextField();
        allproducts = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        productsBtn = new javax.swing.JButton();
        sales = new javax.swing.JButton();
        stockBtn = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        loggeininusers = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        salesTrend = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        inventory = new javax.swing.JButton();
        loggedinuser = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu2 = new javax.swing.JMenu();
        changepass = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        logout = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Nimbus Sans", 1, 26)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 153, 204));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("BENESOFT SUPERMARKETS MANAGEMENT");

        salestoday.setEditable(false);
        salestoday.setBackground(new java.awt.Color(255, 255, 204));
        salestoday.setFont(new java.awt.Font("Liberation Sans", 1, 35)); // NOI18N
        salestoday.setForeground(new java.awt.Color(0, 153, 0));
        salestoday.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        salestoday.setText("0");
        salestoday.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        monthlysales.setEditable(false);
        monthlysales.setBackground(new java.awt.Color(255, 255, 204));
        monthlysales.setFont(new java.awt.Font("Liberation Sans", 1, 35)); // NOI18N
        monthlysales.setForeground(new java.awt.Color(0, 153, 0));
        monthlysales.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        monthlysales.setText("0");
        monthlysales.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        itemslowinstock.setEditable(false);
        itemslowinstock.setBackground(new java.awt.Color(255, 255, 204));
        itemslowinstock.setFont(new java.awt.Font("Liberation Sans", 1, 35)); // NOI18N
        itemslowinstock.setForeground(new java.awt.Color(0, 153, 0));
        itemslowinstock.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        itemslowinstock.setText("0");
        itemslowinstock.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        allproducts.setEditable(false);
        allproducts.setBackground(new java.awt.Color(255, 255, 204));
        allproducts.setFont(new java.awt.Font("Liberation Sans", 1, 35)); // NOI18N
        allproducts.setForeground(new java.awt.Color(0, 153, 0));
        allproducts.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        allproducts.setText("0");
        allproducts.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("TODAYS SALE");

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("This Months sale");

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("ITEMS LOW IN STOCK");

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("PRODUCT VARIETIES");

        jLabel6.setFont(new java.awt.Font("Liberation Sans", 1, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 0, 51));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Incoming Requests");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Message From", "Date", "Urgency", "Message"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setRowHeight(35);
        jTable1.setShowGrid(true);
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel7.setFont(new java.awt.Font("Liberation Sans", 1, 17)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(0, 153, 204));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Control Plane");

        productsBtn.setBackground(new java.awt.Color(255, 255, 204));
        productsBtn.setFont(new java.awt.Font("Liberation Sans", 0, 18)); // NOI18N
        productsBtn.setForeground(new java.awt.Color(0, 153, 153));
        productsBtn.setText("PRODUCTS MANAGER");
        productsBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                productsBtnActionPerformed(evt);
            }
        });

        sales.setBackground(new java.awt.Color(255, 255, 204));
        sales.setFont(new java.awt.Font("Liberation Sans", 0, 18)); // NOI18N
        sales.setForeground(new java.awt.Color(0, 153, 153));
        sales.setText("SALES");
        sales.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                salesActionPerformed(evt);
            }
        });

        stockBtn.setBackground(new java.awt.Color(255, 255, 204));
        stockBtn.setFont(new java.awt.Font("Liberation Sans", 0, 18)); // NOI18N
        stockBtn.setForeground(new java.awt.Color(0, 153, 153));
        stockBtn.setText("STOCK");
        stockBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stockBtnActionPerformed(evt);
            }
        });

        jButton4.setBackground(new java.awt.Color(255, 255, 204));
        jButton4.setFont(new java.awt.Font("Liberation Sans", 0, 18)); // NOI18N
        jButton4.setForeground(new java.awt.Color(0, 153, 153));
        jButton4.setText("USERS");

        jLabel8.setFont(new java.awt.Font("Liberation Sans", 0, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(0, 153, 153));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("SignedIn  Users");

        loggeininusers.setFont(new java.awt.Font("Liberation Sans", 1, 32)); // NOI18N
        loggeininusers.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        loggeininusers.setText("0");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(productsBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(sales, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(stockBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE))
                .addContainerGap())
            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(loggeininusers, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addGap(18, 18, 18)
                .addComponent(productsBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(sales, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(stockBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(loggeininusers, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(27, Short.MAX_VALUE))
        );

        jLabel10.setFont(new java.awt.Font("Liberation Sans", 1, 18)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(0, 204, 0));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("Quick Links");

        salesTrend.setText("Monitor Trend");
        salesTrend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                salesTrendActionPerformed(evt);
            }
        });

        jButton6.setText("User Sales");

        inventory.setText("Inventory");
        inventory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inventoryActionPerformed(evt);
            }
        });

        loggedinuser.setText("*");

        jMenu2.setText("Account");

        changepass.setText("Change pass");
        jMenu2.add(changepass);

        jMenuItem3.setText("Change infor");
        jMenu2.add(jMenuItem3);

        logout.setText("Logout");
        logout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logoutActionPerformed(evt);
            }
        });
        jMenu2.add(logout);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(salestoday, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(monthlysales, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(itemslowinstock, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(allproducts, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(loggedinuser, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(salesTrend, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(inventory, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(salestoday, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(monthlysales, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(itemslowinstock, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(allproducts, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel10)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(salesTrend, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(inventory, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(loggedinuser))
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(145, 145, 145))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void stockBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stockBtnActionPerformed
        // call the open stock method
        openManageStock();
    }//GEN-LAST:event_stockBtnActionPerformed

    private void productsBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_productsBtnActionPerformed
        // open the products page
        openProducts();
    }//GEN-LAST:event_productsBtnActionPerformed

    private void inventoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inventoryActionPerformed
        // open inventory
        openInventory();
    }//GEN-LAST:event_inventoryActionPerformed

    private void salesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_salesActionPerformed
        //open sales page
        openSales();
    }//GEN-LAST:event_salesActionPerformed

    private void salesTrendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_salesTrendActionPerformed
        //open sales trend poage
        openSalesTrend();
    }//GEN-LAST:event_salesTrendActionPerformed

    private void logoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logoutActionPerformed
        //call the logout method
        exitsystem();
    }//GEN-LAST:event_logoutActionPerformed

   
    
    
    //main method
    public static void main(String args[]) {
       new ThemeClass().theme();

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                //Home hm = new Home();
                hm.setVisible(true);                
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField allproducts;
    protected javax.swing.JMenuItem changepass;
    private javax.swing.JButton inventory;
    private javax.swing.JTextField itemslowinstock;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    public javax.swing.JLabel loggedinuser;
    private javax.swing.JLabel loggeininusers;
    private javax.swing.JMenuItem logout;
    private javax.swing.JTextField monthlysales;
    private javax.swing.JButton productsBtn;
    private javax.swing.JButton sales;
    private javax.swing.JButton salesTrend;
    private javax.swing.JTextField salestoday;
    private javax.swing.JButton stockBtn;
    // End of variables declaration//GEN-END:variables
}
