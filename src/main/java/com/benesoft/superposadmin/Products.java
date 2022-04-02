package com.benesoft.superposadmin;
//imports
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.nio.file.Files;
import java.sql.*;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author dev
 */

public class Products extends javax.swing.JFrame {
    //Global variables
    int errBtn = JOptionPane.WARNING_MESSAGE;
    int infoBtn = JOptionPane.INFORMATION_MESSAGE;
    int okBtn = JOptionPane.OK_OPTION;
    ConnectionClass cls = new ConnectionClass();

    /**
     * Creates new form Products
     */
    public Products() {
        initComponents();
        updatedDataPanel.setVisible(false);//make the updated recods jpanel invisible by default
    }


 

    //METHOD TO OPEN ALL STOCKS
    protected void openAllStocks() {
        new AllStocks().setVisible(true);
        this.dispose();
    }
    
    //OPEN ALL PRODUCTS
    private void openAllProducts(){        
        AllProducts products = new AllProducts();          
        products.setVisible(true);
        products.run();
    }
    
    //METHOD TO CLEAR ALL ENTRIES ON THE PRODUCT ENTRY
    private void clearEntries(){
        pCode.setText("");
        pName.setText("");
        pPrice.setText("");
        pQuantity.setText("");
        pCategory.setSelectedItem("Electronics");
        pSize.setText("");
        imageName.setText("");
    }
        //CLEAR THE NEW UPDATES INPIYS
    protected void clearNewEntries(){
        productCode.setText("");
        productSize.setText("");
        productQuantity.setText("");
    }
    
    //CLEAR THE INPUT FIELDS FOR THE STOCK PANEL
    protected void clearSearchedEntries(){
        prodCode.setText("");
        prodName.setText("");
        prodSize.setText("");
        prodAdjustment.setText("");
        prodWorth.setText("");        
    }
    
    
    
    
    
    /*===============================================================================
    ADD NEW PRODUCT TO SYSTEM    
    ================================================================================*/
    
    //CHECK FOR EMPTY FIELDS
    protected String checkEmptyField(){
        if(pCode.getText().isEmpty()){
            pCode.requestFocus();
            return "product code cannot be empty. please fill to proceed";
            
        }else if(pName.getText().isEmpty()){
            pName.requestFocus();
            return "product name cannot be empty. please fill to proceed";
            
        }else if(pPrice.getText().isEmpty()){
            pPrice.requestFocus();
            return "product price cannot be empty. please fill to proceed";
            
        }else if(pQuantity.getText().isEmpty()){
            pQuantity.requestFocus();
            return "product quantity cannot be empty. please fill to proceed";
            
        }else if(pSize.getText().isEmpty()){
            pSize.requestFocus();
            return "product size cannot be empty. please fill to proceed";
            
        }else if(imageName.getText().isBlank()){
            return "please choose an image";
                    
        }
        else{ 
            return null;
        }
    }
    
    //CHECK IF PRODUCT CODE IS A NUMBER
    protected boolean codeIsaNumber(){
        try{
            int number = Integer.parseInt(pCode.getText());
            return true;            
        }catch(NumberFormatException n){
            return false;
        }
    }
    //CHECK IF QUANTITY IS A NUMBER
    protected boolean quantityIsaNumber(){
        try{
            int number = Integer.parseInt(pQuantity.getText());
            return true;            
        }catch(NumberFormatException n){
            return false;
        }
    }
    
    //CHECK IF PRICE IS A NUMBER
    protected boolean priceIsaNumber(){
        try{
            int number = Integer.parseInt(pCode.getText());
            return true;            
        }catch(NumberFormatException n){
            return false;
        }
    }
    
    //CHECK THE LENGTH OF PRODUCT CODE
    protected boolean codeLength(){
        if(pCode.getText().length() < 7 || pCode.getText().length() > 9){
            return false;
        }else{
            return true;
        }
    }
    
    //CHECK IF PRODUCT EXISTS IN SYSTEM USING PRODUCT CODE
    protected String productExists(){    
            String productCode = pCode.getText();
            try{
                String existsQuery = "SELECT productCode FROM products WHERE productCode = ?";
                PreparedStatement pst = cls.conn.prepareStatement(existsQuery);
                pst.setInt(1, Integer.parseInt(productCode));
                ResultSet rst = pst.executeQuery();
                if(rst.next()){//if found a match
                    return "Already in system";
                }else{
                    prodCode.setText(""); prodName.setText(""); prodSize.setText("");
                    prodAdjustment.setText(""); prodWorth.setText("");
                    return null;
                }            
            }catch(SQLException ex){
                return "Server related error occured in executing product existence check!";
            }catch(Exception e){
                return "Oops, there was a problem in confirming product existence";
            }
    } 
    
   
    //SET THE IMAGE ICON TO LABEL
    protected void setImageIcon(){
         JFileChooser chooser = new JFileChooser();
         chooser.showOpenDialog(null);
         File f = chooser.getSelectedFile();                
         String filename = f.getAbsolutePath();//get the file path
         ImageIcon imageIcon = new ImageIcon(new ImageIcon(f.toString()).getImage().getScaledInstance(254, 254, Image.SCALE_DEFAULT));
         imageFileView.setIcon(imageIcon);//now set the icon
         imageName.setText(filename);//set the filename inclusive of source directory         
         isActuallyAnImage();
    }
    
    //GET IMAGE EXTENSION
    protected String getExtension(){
       String img = imageName.getText();
        String ext = img.substring(img.lastIndexOf('.') + 1);
        return ext;
    }
    
    private void isActuallyAnImage(){
       if(!(getExtension().equals("jpg") || getExtension().equals("jpeg") || getExtension().equals("png")|| getExtension().equals("webp"))){
           JOptionPane.showMessageDialog(null, "Please choose an image");
           imageName.setText("");
       }
    }
    
    
    
    //ADD IMAGE TO SERVER FOLDER
    protected boolean uploadProductImage(){
        try{
            String filename = imageName.getText();//the name inclusive of the directory
            String newPath = "uploads/productsimages";
            File directory = new File(newPath);
                if(!directory.exists()){
                  directory.mkdirs();
                }            
            File sourceFile = null;
            File destinationFile = null;
            String extension = filename.substring(filename.lastIndexOf('.') + 1);
            sourceFile = new File(filename);
            destinationFile = new File(newPath + "/"+pCode.getText()+"."+extension);  
            Files.copy(sourceFile.toPath(), destinationFile.toPath());  
            String newLocation = newPath + "/"+pCode.getText()+"."+extension;
            return true;            
        }catch(Exception e){
            return false;
        }
    }
    
    
    
    //INSERT THE PRODUCT TO SYSTEM
    protected boolean insertProductData(){
        //initializations
        String uploadBy = upBy.getText(); String prodCode = pCode.getText(); String prodName = pName.getText();
        String prodCategory = (String)pCategory.getSelectedItem(); String prodSize = pSize.getText();
        String prodPrice = pPrice.getText(); 
        String prodQuantity = pQuantity.getText();
        String newImagePath = "uploads/productsimages/"+pCode.getText()+"."+getExtension();
           try{
               String insertQuery = "INSERT INTO products(productCode, productName, productCategory, productQuantity, productSize, productPrice, uploadedBy, image) VALUES(?,?,?,?,?,?,?,?)";
               PreparedStatement pst = cls.conn.prepareStatement(insertQuery);
               pst.setInt(1, Integer.parseInt(prodCode));
               pst.setString(2, prodName.toLowerCase().trim());
               pst.setString(3, prodCategory.toLowerCase().trim());
               pst.setInt(4, Integer.parseInt(prodQuantity));
               pst.setString(5, prodSize.toLowerCase().trim());
               pst.setInt(6, Integer.parseInt(prodPrice));
               pst.setString(7, uploadBy.toLowerCase().trim());               
               pst.setString(8, newImagePath.trim().trim());  
               //execute the upload
               pst.executeUpdate();
               JOptionPane.showMessageDialog(null, "Product data has been succesfully uploaded", "Data saved", infoBtn);
               return true;
           }catch(Exception e){
               JOptionPane.showMessageDialog(null, "Something went wrong in creating product profile\n"+e);
               return false;               
           }
    }
    
    
    //THE MASTER METHOD TO FETCH ALL OTHER METHODS
    private void masterMethod(){
        //check empty
        if(checkEmptyField() != null){//check if there is an empty field
            JOptionPane.showMessageDialog(null, checkEmptyField(), "Code Rejected", errBtn);
            
        }else if(codeIsaNumber() == false){//check if code is a number
            JOptionPane.showMessageDialog(null, "Product code should be in number characters. please try again..", "Code Rejected", errBtn);
            
        }else if(codeLength() == false){//if product code is not of defined length
            JOptionPane.showMessageDialog(null, "Product code must be between 7 to 9 characters long. Try again", "Code Error", errBtn);
            
        }else if(priceIsaNumber() == false){//check if quantity is a number
            JOptionPane.showMessageDialog(null, "Price value must be in number format", "Wrong Quantity", errBtn);
            
        }else if(quantityIsaNumber() == false){//check if quantity is a number
            JOptionPane.showMessageDialog(null, "Please re-enter the quantity using number values - '0-9'", "Wrong Quantity", errBtn);
            
        }else{
            if(cls.getConnection() == true){//check db connection
                if(productExists() != null){//check product existence
                    JOptionPane.showMessageDialog(null, productExists(), "Product exists err", errBtn);
                }else{//if all is right
                      //insert image
                      uploadProductImage();
                      //insert the product details
                      insertProductData();
                      clearEntries();
                }                
            }else{
                JOptionPane.showMessageDialog(null, "Failed to establish connection to the server, please try again", "Connection Error", errBtn);
            }               
        }        
    }
 
    
    
    
    /*======================================================================
    THE MANAGE STOCK TAB
    =======================================================================*/
    
    //CLEAR THE STOCK FIELDS
    protected void clearStockEntries(){
        prodCode.setText(""); prodName.setText(""); prodSize.setText(""); prodWorth.setText(""); prodAdjustment.setText("");
    }
    
    
    //FIND OUT IF THE CODE ENTERED IS OF NUNBER FORMAT
    private boolean searchIsaNumber(){
        try{
            int number = Integer.parseInt(searchArea.getText());
            return true;
        }catch(NumberFormatException n){
            return false;
        }
    }
    
    //FIND OUT IF NO ENTERED TO ADJUST STOCK IS A NUMBER
    private boolean adjustmentIsaNumber(){
        try{
            int number = Integer.parseInt(searchArea.getText());
            return true;
        }catch(NumberFormatException n){
            return false;
        }
    }
    
    
    
    
    //CHECK IF PRODUCT EXISTS IN SYSTEM USING PRODUCT CODE
    protected String productFound(){    
            String searched = searchArea.getText();
            try{
                String existsQuery = "SELECT productCode FROM products WHERE productCode = ?";
                PreparedStatement pst = cls.conn.prepareStatement(existsQuery);
                pst.setInt(1, Integer.parseInt(searched));
                ResultSet rst = pst.executeQuery();
                if(rst.next()){//if found a match
                    return null;
                }else{
                    return "We cant find this item on our server. Please check for typo..";
                }            
            }catch(SQLException ex){
                return "Server related error occured in executing product existence check!";
            }catch(Exception e){
                return "Oops, there was a problem in confirming product existence";
            }
    }
    
    
    //GET THE DATA FROM DB
    protected String[] getProductData(){
        String searched = searchArea.getText();
        try{
            String getData = "SELECT * FROM products WHERE productCode = ?";
            PreparedStatement pst = cls.conn.prepareStatement(getData);
            pst.setInt(1, Integer.parseInt(searched));
            ResultSet rs = pst.executeQuery();
            if(rs.next()){
                String prodCode = rs.getString("productCode");
                String prodName = rs.getString("productName");
                String prodPrice = rs.getString("productPrice");
                String prodQuantity = rs.getString("productQuantity");
                String prodSize = rs.getString("productSize");
                String[] productData = {prodCode, prodName, prodPrice, prodQuantity, prodSize};
                return productData;
            }else{
                return null;
            }
        }catch(SQLException ex){
            JOptionPane.showMessageDialog(null, "Server error occured while fetching products data", "Server Error", errBtn);
            return null;
        }catch(Exception ex){
            JOptionPane.showMessageDialog(null, "Oops, something went wrong, could not fetch products data", "Undefined Error", errBtn);
            return null;
        }
    }
    
 //METHOD TO POPULATE THE TEXTFIELDS
 protected void setFieldValues(){
     prodCode.setText(getProductData()[0]);
     prodName.setText(getProductData()[1]);
     prodSize.setText(getProductData()[4]);     
     //calculate the networth using, quantity*price
     double netWorth = Double.parseDouble(getProductData()[2]) * Integer.parseInt(getProductData()[3]);
     prodWorth.setText(String.valueOf(netWorth));
 }
 
 
 
    
//SPECIAL METHOD TO ALLOW FOR POPULATING TEXTFIELDS WITH DATA
 protected void populateTextFields(){
     if(searchArea.getText().isEmpty()){
         JOptionPane.showMessageDialog(null, "Please enter something on search area", "Empty field", errBtn);
         searchArea.requestFocus();
         clearStockEntries();
         
     }else if(searchIsaNumber() == false){
         JOptionPane.showMessageDialog(null, "Please search using product code", "Code Error", errBtn);
         searchArea.requestFocus();
         clearStockEntries();
         
     }else if(cls.getConnection() == true){
         //first check if product exists on system
         if(productFound() == null){
             setFieldValues();
             
         }else{
              JOptionPane.showMessageDialog(null, "This product was not matched on our system", "Not Found", errBtn);
              clearStockEntries();
         }         
     }else{
         JOptionPane.showMessageDialog(null, "Failed to establish connection to database", "Connection error", errBtn);
     }
 }
 
 
 /*-----------------------------
 ADD STOCK METHOD STARTS HERE
 ------------------------------*/
 
    //return the adjustment value
    private int getAdjustmentValue(){
        return Integer.parseInt(prodAdjustment.getText());
    }
    

    
    
    //method to get product quantity when called
    protected String getQuantity(){
        String pCode = prodCode.getText();
        try{
            String query = "SELECT productQuantity, productCode FROM products WHERE productCode=?";
            PreparedStatement ps = cls.conn.prepareStatement(query);
            ps.setInt(1, Integer.parseInt(pCode));
            ResultSet rst = ps.executeQuery();
            if(rst.next()){
                return rst.getString("productQuantity");
            }else{
                return null;
            }
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "Failed to get quantity"+e);
            return null;
        }
    }
    
  
    //UPDATE ADD STOCK FOR THE SELECTED ITEM
    private void addUpdateStock(){
        try{
            String pCode = prodCode.getText();
            String addQuery = "UPDATE products SET productQuantity = (productQuantity+?) WHERE productCode = ?";
            PreparedStatement pst = cls.conn.prepareStatement(addQuery);
            pst.setInt(1, getAdjustmentValue());
            pst.setInt(2, Integer.parseInt(pCode));            
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Product stock has been adjusted.","Add success",infoBtn);
            //then populate the panel below
            updatedDataPanel.setVisible(true);
            productCode.setText(pCode);
            productSize.setText(prodSize.getText());
            productQuantity.setText(getQuantity());
            
        }catch(SQLException ex){
            JOptionPane.showMessageDialog(null, "Server error in adjusting stock quantity for this item.","Failed",errBtn);            
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "Oops, something went wrong. cannot add stock","Failed",errBtn);
        }
    }
    
    //UPDATE ADD STOCK FOR THE SELECTED ITEM
    private void deductUpdateStock(){
        try{
            String pCode = prodCode.getText();
            String addQuery = "UPDATE products SET productQuantity = (productQuantity-?) WHERE productCode = ?";
            PreparedStatement pst = cls.conn.prepareStatement(addQuery);
            pst.setInt(1, getAdjustmentValue());
            pst.setInt(2, Integer.parseInt(pCode));            
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Product stock has been adjusted.","Add success",infoBtn);
            //then populate the panel below
            updatedDataPanel.setVisible(true);
            productCode.setText(pCode);
            productSize.setText(prodSize.getText());
            productQuantity.setText(getQuantity());
            
        }catch(SQLException ex){
            JOptionPane.showMessageDialog(null, "Server error in adjusting stock quantity for this item.","Failed",errBtn);            
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "Oops, something went wrong. cannot add stock","Failed",errBtn);
        }
    }
        
    
    
    
    
    //MASTER METHOD TO ADJUST THE STOCK
    protected void adjustStock(){
     if(prodCode.getText().isEmpty()){//check if productcode is empty
         JOptionPane.showMessageDialog(null, "Please get the item first", "Search First", errBtn);
         searchArea.requestFocus();
         
     }else if(prodAdjustment.getText().isEmpty()){//check if adjust by is empty
         JOptionPane.showMessageDialog(null, "Please fill in the adjusting value to proceed", "Nothing to adjust", errBtn);
         prodAdjustment.requestFocus();
         
     }else if(adjustmentIsaNumber() == false){//check if etered character is a number
         JOptionPane.showMessageDialog(null, "To update stock, you must use a number", "Not a number", errBtn);
         prodAdjustment.requestFocus();
         
     }else{
         if(cls.getConnection() == true){
            if(selectionMode.getSelectedItem().equals("ADJUST BY ADDING")){
                //add stock for that item
                addUpdateStock();
            }else{
                //deduct stock for that item
                deductUpdateStock();
            }
         }else{
             JOptionPane.showMessageDialog(null, "Failed to establish connection to database", "Connection error", errBtn);
         }
     }
    }
 
    
    

@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        prodTabs = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        pCode = new javax.swing.JTextField();
        pName = new javax.swing.JTextField();
        pPrice = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        pQuantity = new javax.swing.JTextField();
        pSize = new javax.swing.JTextField();
        pCategory = new javax.swing.JComboBox<>();
        jLabel19 = new javax.swing.JLabel();
        imageFileView = new javax.swing.JLabel();
        imageName = new javax.swing.JLabel();
        addProduct = new javax.swing.JButton();
        clearEntries = new javax.swing.JButton();
        allProducts = new javax.swing.JButton();
        openStock = new javax.swing.JButton();
        upBy = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        searchArea = new javax.swing.JTextField();
        findItem = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        prodCode = new javax.swing.JTextField();
        prodName = new javax.swing.JTextField();
        prodSize = new javax.swing.JTextField();
        prodAdjustment = new javax.swing.JTextField();
        prodWorth = new javax.swing.JTextField();
        updatedDataPanel = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        productCode = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        productQuantity = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        productSize = new javax.swing.JTextField();
        allStocks = new javax.swing.JButton();
        selectionMode = new javax.swing.JComboBox<>();
        updateStock = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Inventory panel");
        setPreferredSize(new java.awt.Dimension(900, 537));
        setResizable(false);
        addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            public void windowGainedFocus(java.awt.event.WindowEvent evt) {
            }
            public void windowLostFocus(java.awt.event.WindowEvent evt) {
                formWindowLostFocus(evt);
            }
        });

        jLabel13.setText("Product Code:");

        jLabel14.setText("Product Name:");

        jLabel15.setText("Product Price:");

        pCode.setBackground(new java.awt.Color(255, 255, 204));
        pCode.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        pName.setBackground(new java.awt.Color(255, 255, 204));
        pName.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        pPrice.setBackground(new java.awt.Color(255, 255, 204));
        pPrice.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        jLabel16.setText("Quantity:");

        jLabel17.setText("Product Size:");

        jLabel18.setText("Category:");

        pQuantity.setBackground(new java.awt.Color(255, 255, 204));
        pQuantity.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        pSize.setBackground(new java.awt.Color(255, 255, 204));
        pSize.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        pCategory.setBackground(new java.awt.Color(255, 255, 204));
        pCategory.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Electronics", "House Holds", "Drinks", "Alcoholics", "Beverages", "Fruits" }));
        pCategory.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText("Product Image");

        imageFileView.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        imageFileView.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        imageFileView.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                imageFileViewMouseClicked(evt);
            }
        });

        imageName.setText(" ");
        imageName.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        addProduct.setText("Add Product");
        addProduct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addProductActionPerformed(evt);
            }
        });

        clearEntries.setText("Clear");
        clearEntries.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearEntriesActionPerformed(evt);
            }
        });

        allProducts.setBackground(new java.awt.Color(102, 153, 255));
        allProducts.setForeground(java.awt.Color.white);
        allProducts.setText("All Products");
        allProducts.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                allProductsActionPerformed(evt);
            }
        });

        openStock.setBackground(new java.awt.Color(102, 153, 255));
        openStock.setForeground(java.awt.Color.white);
        openStock.setText("Stock");
        openStock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openStockActionPerformed(evt);
            }
        });

        upBy.setText("*");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(imageFileView, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel19, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(allProducts, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(openStock, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(upBy, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(clearEntries, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(addProduct, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE))
                                    .addComponent(imageName, javax.swing.GroupLayout.DEFAULT_SIZE, 347, Short.MAX_VALUE))
                                .addGap(45, 45, 45))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(pQuantity)
                            .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(pCode)
                            .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(pSize, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel17, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(pCategory, 0, 296, Short.MAX_VALUE)
                                    .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, 268, Short.MAX_VALUE)
                                    .addComponent(pName))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(pPrice)
                                    .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(jLabel14)
                    .addComponent(jLabel15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pCode, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pName, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(jLabel17)
                    .addComponent(jLabel18))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pSize, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(imageFileView, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(imageName)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(upBy)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(addProduct, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(clearEntries, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(17, 17, 17)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(allProducts, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(openStock, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        prodTabs.addTab("                ADD TO INVENTORY     ", jPanel1);

        searchArea.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                searchAreaKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchAreaKeyReleased(evt);
            }
        });

        findItem.setText("FIND");
        findItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                findItemActionPerformed(evt);
            }
        });

        jLabel2.setText("Product Code");

        jLabel3.setText("Product Name");

        jLabel4.setText("Size");

        jLabel5.setText("Adjust By:");

        jLabel6.setText("Current Worth");

        jLabel7.setText("To search item, enter the code or name then press enter. To update fill in new quantity and update");

        prodCode.setEditable(false);
        prodCode.setBackground(new java.awt.Color(255, 255, 204));
        prodCode.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        prodCode.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        prodName.setEditable(false);
        prodName.setBackground(new java.awt.Color(255, 255, 204));
        prodName.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        prodName.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        prodSize.setEditable(false);
        prodSize.setBackground(new java.awt.Color(255, 255, 204));
        prodSize.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        prodSize.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        prodAdjustment.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        prodAdjustment.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        prodWorth.setEditable(false);
        prodWorth.setBackground(new java.awt.Color(255, 255, 204));
        prodWorth.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        prodWorth.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        updatedDataPanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 153, 153), 1, true));

        jLabel8.setFont(new java.awt.Font("Liberation Sans", 1, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(0, 153, 153));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("UPDATED RECORD NOW");

        jLabel9.setText("PRODUCT CODE:");

        productCode.setEditable(false);
        productCode.setBackground(new java.awt.Color(255, 255, 204));
        productCode.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        productCode.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        jLabel10.setText("NEW QUANTITY:");

        productQuantity.setEditable(false);
        productQuantity.setBackground(new java.awt.Color(255, 255, 204));
        productQuantity.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        productQuantity.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        jLabel11.setText("SIZE:");

        productSize.setEditable(false);
        productSize.setBackground(new java.awt.Color(255, 255, 204));
        productSize.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        productSize.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        javax.swing.GroupLayout updatedDataPanelLayout = new javax.swing.GroupLayout(updatedDataPanel);
        updatedDataPanel.setLayout(updatedDataPanelLayout);
        updatedDataPanelLayout.setHorizontalGroup(
            updatedDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(updatedDataPanelLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(updatedDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE)
                    .addComponent(productCode))
                .addGroup(updatedDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(updatedDataPanelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(productSize, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, updatedDataPanelLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(updatedDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)
                    .addComponent(productQuantity))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        updatedDataPanelLayout.setVerticalGroup(
            updatedDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(updatedDataPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(updatedDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jLabel11)
                    .addComponent(jLabel10))
                .addGap(4, 4, 4)
                .addGroup(updatedDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(productCode, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(productSize, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(productQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        allStocks.setText("ALL STOCKS");
        allStocks.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                allStocksActionPerformed(evt);
            }
        });

        selectionMode.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ADJUST BY ADDING", "ADJUST BY DEDUCTING" }));
        selectionMode.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        updateStock.setText("Update");
        updateStock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateStockActionPerformed(evt);
            }
        });

        jButton1.setText("CLEAR");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 657, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(searchArea, javax.swing.GroupLayout.PREFERRED_SIZE, 372, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(findItem, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(allStocks, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(prodCode, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
                                    .addComponent(prodName))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(prodSize, javax.swing.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE)
                                    .addComponent(prodAdjustment))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
                                    .addComponent(prodWorth))))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addComponent(updatedDataPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(60, 60, 60)
                .addComponent(selectionMode, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(updateStock, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel7)
                .addGap(9, 9, 9)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(searchArea, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(findItem, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(allStocks, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(prodCode, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(prodName, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(prodSize, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(prodAdjustment, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(prodWorth, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(46, 46, 46)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(selectionMode, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(updateStock, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 117, Short.MAX_VALUE)
                .addComponent(updatedDataPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        prodTabs.addTab("         MANAGE STOCK        ", jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(prodTabs, javax.swing.GroupLayout.PREFERRED_SIZE, 669, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 16, Short.MAX_VALUE)
                .addComponent(prodTabs, javax.swing.GroupLayout.PREFERRED_SIZE, 521, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void allStocksActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_allStocksActionPerformed
        // OPEN ALL STOCKS
        this.dispose();
        openAllStocks();
    }//GEN-LAST:event_allStocksActionPerformed

    private void openStockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openStockActionPerformed
        // open stocks
        prodTabs.setSelectedIndex(1);
    }//GEN-LAST:event_openStockActionPerformed

    private void imageFileViewMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_imageFileViewMouseClicked
        // open choose image
        setImageIcon();
    }//GEN-LAST:event_imageFileViewMouseClicked

    private void clearEntriesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearEntriesActionPerformed
        // Clear entries
        clearEntries();
    }//GEN-LAST:event_clearEntriesActionPerformed

    private void addProductActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addProductActionPerformed
        //add to system
        masterMethod();
    }//GEN-LAST:event_addProductActionPerformed

    private void allProductsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_allProductsActionPerformed
        // opem all products
        this.dispose();
        openAllProducts();
    }//GEN-LAST:event_allProductsActionPerformed

    private void findItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_findItemActionPerformed
        // call the work with data method
        populateTextFields();
    }//GEN-LAST:event_findItemActionPerformed

    private void searchAreaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchAreaKeyPressed
        // searh by hitting enter key
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            populateTextFields();
        }
    }//GEN-LAST:event_searchAreaKeyPressed

    private void updateStockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateStockActionPerformed
        //call the adjust stock method
        adjustStock();
    }//GEN-LAST:event_updateStockActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        //clear the searcjhed entries
        clearSearchedEntries();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void searchAreaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchAreaKeyReleased
        //monitor if the input has data. if no data, clear the new upsated records
        if(searchArea.getText().isEmpty()){
            clearNewEntries();
            updatedDataPanel.setVisible(false);
        }
    }//GEN-LAST:event_searchAreaKeyReleased

    private void formWindowLostFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowLostFocus
        
    }//GEN-LAST:event_formWindowLostFocus

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        new ThemeClass().theme();
        
        

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Products().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addProduct;
    private javax.swing.JButton allProducts;
    private javax.swing.JButton allStocks;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton clearEntries;
    private javax.swing.JButton findItem;
    private javax.swing.JLabel imageFileView;
    private javax.swing.JLabel imageName;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JButton openStock;
    private javax.swing.JComboBox<String> pCategory;
    private javax.swing.JTextField pCode;
    private javax.swing.JTextField pName;
    private javax.swing.JTextField pPrice;
    private javax.swing.JTextField pQuantity;
    private javax.swing.JTextField pSize;
    private javax.swing.JTextField prodAdjustment;
    private javax.swing.JTextField prodCode;
    private javax.swing.JTextField prodName;
    private javax.swing.JTextField prodSize;
    protected javax.swing.JTabbedPane prodTabs;
    private javax.swing.JTextField prodWorth;
    private javax.swing.JTextField productCode;
    private javax.swing.JTextField productQuantity;
    private javax.swing.JTextField productSize;
    private javax.swing.JTextField searchArea;
    private javax.swing.JComboBox<String> selectionMode;
    private javax.swing.JLabel upBy;
    private javax.swing.JButton updateStock;
    private javax.swing.JPanel updatedDataPanel;
    // End of variables declaration//GEN-END:variables
}
