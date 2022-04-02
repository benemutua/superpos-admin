package com.benesoft.superposadmin;

//imports
import java.util.Date;
import java.text.SimpleDateFormat;
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.EventQueue;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.*;
import java.text.DateFormat;
import javax.swing.JOptionPane;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import javax.swing.table.DefaultTableModel;


/**
 *
 * @author dev
 */
public class Salessummary extends javax.swing.JFrame {
  
    //global initialization
    ConnectionClass cls = new ConnectionClass();
    int infoBtn = JOptionPane.INFORMATION_MESSAGE;
    /**
     * Creates new form Sales
     */
    public Salessummary() {        
        initComponents();        
        jScrollPane1.setVisible(false);//the table for sales summary
    }
    
    
    
    
    /*----------------------------------------------------
    THE SALES TREND GRAPHICAL VIEW PAGE
    ----------------------------------------------------*/
    /*--------------------------------------
    the overal graph
    --------------------------------------*/
    protected void generateGraph(){     
        if(cls.getConnection() == true){
                try{
                    String query = "SELECT count(productCode)productCode, saleDate FROM sales WHERE status=?  GROUP BY saleDate ";//ORDER BY count(saleDate) ASC  WHERE productCode=123456789
                    PreparedStatement ps = cls.conn.prepareStatement(query);
                    ps.setString(1, "completed");
                    ResultSet rst = ps.executeQuery();
                    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
                 //fetch db data using a loop
                 while(rst.next()){            
                    dataset.addValue(rst.getInt("productCode"), "", rst.getString("saleDate"));
                  }//end loop for fetching database data
                    //now go ahead to plot the chart
                    JFreeChart chart = ChartFactory.createLineChart("Sales Performance graph", // Chart title  
                    "Date", // X-Axis Label  
                    "sales made", // Y-Axis Label  
                    dataset,  PlotOrientation.VERTICAL, false, true,true );  
                    BarRenderer renderer = null;
                    CategoryPlot plot = null;
                    renderer = new BarRenderer();
                    ChartFrame frame = new ChartFrame("Benesoft supermarkets sales", chart);//the calibrations
                    frame.setVisible(true);//show the chart                       
                    frame.setSize(1200, 600);//size of chart   
                    frame.setResizable(false);
                }catch(Exception e){
                    JOptionPane.showMessageDialog(null, "Failed to generate chart\n"+e);
                }    
        }else{
              JOptionPane.showMessageDialog(null, "Failed to establish connection to the server");
        }
    }
    /*--------------------------------------
    end the overal graph
    --------------------------------------*/
    
    
    
    /*--------------------------------------
    Start the custom graph
    --------------------------------------*/
        protected void generateCustomGraph(){          
        DateFormat dateFormat = new SimpleDateFormat("y-MM-dd");
        Date fromdate = datefrom.getDate();  
        Date todate = dateto.getDate();
        String fdate = dateFormat.format(fromdate);//what will be used
        String tdate = dateFormat.format(todate);//what will be used
       
            if(cls.getConnection() == true){
                try{
                    String query = "SELECT count(productCode)productCode, saleDate FROM sales WHERE status=? && saleDate BETWEEN ? AND ? GROUP BY saleDate ";
                    PreparedStatement ps = cls.conn.prepareStatement(query);
                    ps.setString(1, "completed");
                    ps.setString(2, fdate);
                    ps.setString(3, tdate);
                    ResultSet rst = ps.executeQuery();
                    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
                 //fetch db data using a loop
                 while(rst.next()){            
                    dataset.addValue(rst.getInt("productCode"), "", rst.getString("saleDate"));
                  }//end loop for fetching database data
                    //now go ahead to plot the chart
                    JFreeChart chart = ChartFactory.createLineChart("Sales Between "+fdate +" & "+tdate, // Chart title  
                    "Date", // X-Axis Label  
                    "sales made", // Y-Axis Label  
                    dataset,  PlotOrientation.VERTICAL, false, true,true );  
                    BarRenderer renderer = null;
                    CategoryPlot plot = null;
                    renderer = new BarRenderer();
                    ChartFrame frame = new ChartFrame("Benesoft supermarkets sales", chart);//the calibrations
                    frame.setVisible(true);//show the chart                       
                    frame.setSize(1200, 600);//size of chart   
                    frame.setResizable(false);
                }catch(Exception e){
                    JOptionPane.showMessageDialog(null, "Failed to generate chart\n"+e);
                }    
            }else{
                  JOptionPane.showMessageDialog(null, "Failed to establish connection to the server");
            }
    }
    /*--------------------------------------
    End the custom graph
    --------------------------------------*/    
    /*----------------------------------------------------
    END SALES TREND GRAPHICAL VIEW PAGE
    ----------------------------------------------------*/
    
        
        
        
        
        
        
    
    /*----------------------------------------------------
    START THE SALES TAB METHODS
    ----------------------------------------------------*/
   
    /*------------------------------------------------------------------------------
    POPULATE THE TABLE WITH CUSTOM SALES ORDERS/RESULTS BASED ON CHOSEN PERIOD
    ------------------------------------------------------------------------------*/
    //this is the master password that determines the method to run depending on the jcombobox selected
    protected void masterPopulateSales(){
        String selecteditem = (String)transactionmodel.getSelectedItem();
        if(selecteditem == "All transactions"){
            populateCompletedSales();
        }else{
            populateReversedSales();
        }
    }
    
    
    
    //GET ALL TRANSACTIONS IRREGARDLESS OF THE STATUS AND DISPLAY THEM ON SALES SUMMARY TABLE
    protected void populateCompletedSales(){
     if(cls.getConnection() == true){
        String yer = String.valueOf(year.getValue());
        String mon = (String)month.getSelectedItem();
        DefaultTableModel tb = (DefaultTableModel)periodsalestable.getModel();
        try{
            String queryfetch = "SELECT transactionCode, status, saleDate FROM  sales WHERE  status= ? && saleDate LIKE ? GROUP BY transactionCode";
                    
            PreparedStatement ps = cls.conn.prepareStatement(queryfetch);
            ps.setString(1, "completed");
            ps.setString(2, yer+"-"+mon+"%");            
            ResultSet rs = ps.executeQuery();
            //below follows steps to populate the table with data
            //check if the table had data, clear it then readd a fresh
                if(tb.getRowCount() > 0){
                   tb.setRowCount(0);
                }
            //start populating the table
            while(rs.next()){                                     
                //the table values
                String[] tbdata = {rs.getString("transactionCode"),rs.getString("saleDate"), "Unspecified"};
                //add row to table                
                tb.addRow(tbdata);   
                jScrollPane1.setVisible(true);
                if(tb.getRowCount()==0){
                    JOptionPane.showMessageDialog(null, "");
                }
            }
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "An error occured in fetching the sales data\n"+e);
        }
     }else{
         JOptionPane.showMessageDialog(null, "Failed to establish connection to the server");
     }
    }
   
    
    //FETCH THE REVERSED TRANSACTIONS ONLY AND DISPLAY THEM ON SALES SUMMARY TABLE
    protected void populateReversedSales(){
     if(cls.getConnection() == true){
        String yer = String.valueOf(year.getValue());
        String mon = (String)month.getSelectedItem();
        DefaultTableModel tb = (DefaultTableModel)periodsalestable.getModel();
        try{
            String queryfetch = "SELECT transactionCode, status, saleDate, soldBy FROM  sales WHERE  status= ? && saleDate LIKE ?  ORDER BY saleDate DESC";
                    
            PreparedStatement ps = cls.conn.prepareStatement(queryfetch);
            ps.setString(1, "reversed");
            ps.setString(2, yer+"-"+mon+"%");            
            ResultSet rs = ps.executeQuery();
            //below follows steps to populate the table with data
            //check if the table had data, clear it then readd a fresh
                if(tb.getRowCount() > 0){
                   tb.setRowCount(0);
                }
            //start populating the table
            while(rs.next()){                                     
                //the table values
                String[] tbdata = {rs.getString("transactionCode"),rs.getString("saleDate"), "reversed: By -"+rs.getString("soldBy")};
                //add row to table                
                tb.addRow(tbdata);
                jScrollPane1.setVisible(true);
                 if(tb.getRowCount()==0){
                    JOptionPane.showMessageDialog(null, "");
                }
            }
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "An error occured in fetching the sales data\n"+e);
        }
     }else{
         JOptionPane.showMessageDialog(null, "Failed to establish connection to the server");
     }
    }
    
    

    /*--------------------------------------------------------------------------------------------------------
    WHEN YOU CLICK A ROW ON THE SALESSUMMARY TABLE, LET IT FETCH THE DATA TO POPULATE THE ORDER DETAILS PAGE 
    --------------------------------------------------------------------------------------------------------*/
    
    //Get and return the transaction number(code) on the selected row
    protected String getSelectedOrder(){
        DefaultTableModel tb = (DefaultTableModel)periodsalestable.getModel();
        int selectedrow = periodsalestable.getSelectedRow();
        String transcode = tb.getValueAt(selectedrow,0).toString();
        return transcode;
    }
    
    //Get and return the date on the selected row
    protected String getSelectedDate(){
        DefaultTableModel tb = (DefaultTableModel)periodsalestable.getModel();    
        int selectedrow = periodsalestable.getSelectedRow();
        String date = tb.getValueAt(selectedrow, 1).toString();
        return date;
    }
    
    
    //Methd to get and return sum of the selected order completed options
    protected String getCompletedSaleSum(){
        try{
            String myquery = "SELECT SUM(productPrice)productPrice, transactionCode, status FROM sales WHERE transactionCode = ? && status=?";
            PreparedStatement pst = cls.conn.prepareStatement(myquery);
            pst.setString(1, getSelectedOrder());
            pst.setString(2, "completed");
            ResultSet rs = pst.executeQuery();
            if(rs.next()){
                return rs.getString("productPrice");
            }else{
                return "0";
            }   
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "There was an error processing sum\n"+e);
            return "0";
        }
    }
    
    //Methd to get and return sum of the selected order reversed options
        protected String getReversedSaleSum(){
        try{
            String myquery = "SELECT SUM(productPrice)productPrice, transactionCode, status FROM sales WHERE transactionCode = ? && status=?";
            PreparedStatement pst = cls.conn.prepareStatement(myquery);
            pst.setString(1, getSelectedOrder());
            pst.setString(2, "reversed");
            ResultSet rs = pst.executeQuery();
            if(rs.next()){
                return rs.getString("productPrice");
            }else{
                return "0";
            }   
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "There was an error processing sum\n"+e);
            return "0";
        }
    }
    
    
    
    //Fetch the order details including all it contains and populate on the next page
    protected void orderDetails(){
        //initialize
        Ordertracking ord = new Ordertracking();
        DefaultTableModel tbl = (DefaultTableModel)ord.orderdata.getModel();            
            try{
            String query = "SELECT productCode, productPrice, soldBy, saleDate, saleCode, transactionCode FROM sales WHERE transactionCode = ? && saleDate = ?";
            PreparedStatement pst = cls.conn.prepareStatement(query);
            pst.setString(1, getSelectedOrder());
            pst.setString(2, getSelectedDate());
            ResultSet rst = pst.executeQuery();
                while(rst.next()){
                    String tcode = rst.getString("transactionCode");
                    String sku = rst.getString("saleCode");
                    String code = rst.getString("productCode");
                    String soldby = rst.getString("soldBy");
                    String soldon = rst.getString("saleDate");
                    String price = rst.getString("productPrice");
                    //table data
                    ord.setVisible(true);
                    String[] orddata = {sku, code, price};
                    //add table row
                    tbl.addRow(orddata);
                    String[] d = {"T CODE: "+tcode, " ", "SALE DATE: "+soldon, "SOLD BY: "+soldby,"COMPLETED WORTH: "+getCompletedSaleSum(), "REVERSED WORTH: "+getReversedSaleSum()};
                    ord.otherorderdata.setListData(d);
                }
            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "An error occured. please try again\n"+e);
            }
    }
    
    //This is the master method that runs the get order details and populates it on the next page
    protected void loopOrderInformation(){
        if(cls.getConnection() == true){
            orderDetails();
        }else{
            JOptionPane.showMessageDialog(null, "Failed to establish connection to the server");
        }
    }
    
    
    
    
      //GENERATE PDF REPORT
    protected void generatePdf(){        
        try{
            String filename = "benedictsales.pdf";//the filename
            String path = "../reports/";//the residing folder
            File directory = new File(path);
            if(!directory.exists()){
              directory.mkdirs();//if the directory is not there, set it
            } 
            //create a document object
            Document document = new Document();
            //set the page size
            document.setPageSize(PageSize.A7);
            document.setMargins(1, 1, 1, 1);
            PdfWriter.getInstance((com.itextpdf.text.Document) document, new FileOutputStream(path+filename));    
            document.open();//open the doc to add details to it            
            //contents show from here
            document.addTitle("This document contains reports for data ...");
            document.add(new Paragraph("QuickMart Supermarkets"));//title
            document.add(new Paragraph("**************************************"));
            document.add(new Paragraph("Sales Receipt"));
            document.add(new Paragraph("**************************************"));
            
            //SET THE TABLE HEADERS
             PdfPTable table = new PdfPTable(3);//the table header row should have 3 columns
             PdfPCell cl = new PdfPCell(new Phrase("Code"));//first column
             table.addCell(cl);            
             cl = new PdfPCell(new Phrase("Product Name"));//second column
             table.addCell(cl);            
             cl = new PdfPCell(new Phrase("Product Size"));//third column
             table.addCell(cl); 
            //THE TABLE DATA BY TAKING THEM FROM DATABASE
            String query = "SELECT * FROM products";
            PreparedStatement pst = cls.conn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            while(rs.next()){
                 //create a pdf table to show data using pdf table object
                 table.addCell(rs.getString("productCode"));
                 table.addCell(rs.getString("productName"));
                 table.addCell(rs.getString("productSize"));                 
            }
            document.add(table);//add th table and its data            
            document.close();//close the document                       
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "Error "+e);
        }finally{
            JOptionPane.showMessageDialog(null, "Please Tear receipt from printer");
        }
    }           
    /*----------------------------------------------------
        END THE SALES TAB METHODS
    ----------------------------------------------------*/
    

    
    
    
    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        salesTab = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        jLabel10 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        optionselected = new javax.swing.JComboBox<>();
        graphaction = new javax.swing.JButton();
        period = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        customgraph = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        datefrom = new com.toedter.calendar.JDateChooser();
        dateto = new com.toedter.calendar.JDateChooser();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        filter = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        periodsalestable = new javax.swing.JTable();
        month = new javax.swing.JComboBox<>();
        transactionmodel = new javax.swing.JComboBox<>();
        year = new com.toedter.calendar.JYearChooser();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        productsTable = new javax.swing.JTable();
        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Sales page");
        setResizable(false);
        setSize(new java.awt.Dimension(0, 0));

        jPanel1.setPreferredSize(new java.awt.Dimension(1100, 531));

        jLabel11.setForeground(new java.awt.Color(0, 153, 204));
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("INFORMATION HERE IS PRESENTED IN GRAPHS");

        jList1.setBackground(new java.awt.Color(242, 242, 242));
        jList1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));
        jList1.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "You can check sales trend by selecting a range of period to get graph of how many sales were made", "You can click on \"Overal\" to plot a trend of all sales ever done", "If filling dates manually", "use this format \"yyyy-MM-dd\" else it won't work eg \"2022-02-02\"", "" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jList1.setToolTipText("Takeaway notes to help in navigation");
        jScrollPane3.setViewportView(jList1);

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("IMPORTANT NAVIGATION NOTES");

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("What do you want to do?");

        optionselected.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Get performance within a set period", "Get the overal sales performance" }));
        optionselected.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        optionselected.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                optionselectedItemStateChanged(evt);
            }
        });

        graphaction.setText("Get Overal sales");
        graphaction.setEnabled(false);
        graphaction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                graphactionActionPerformed(evt);
            }
        });

        jLabel12.setText("Trend From:");

        jLabel13.setText("Trend as at:");

        customgraph.setText("GENERATE");
        customgraph.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                customgraphActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Liberation Sans", 0, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 153, 204));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Choose period between which to get the trend");

        datefrom.setDateFormatString("y-MM-dd");

        dateto.setDateFormatString("y-MM-dd");

        javax.swing.GroupLayout periodLayout = new javax.swing.GroupLayout(period);
        period.setLayout(periodLayout);
        periodLayout.setHorizontalGroup(
            periodLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(periodLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(periodLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(periodLayout.createSequentialGroup()
                        .addGroup(periodLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
                            .addComponent(datefrom, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(periodLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(periodLayout.createSequentialGroup()
                                .addComponent(dateto, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(customgraph, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 355, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(208, Short.MAX_VALUE))
        );
        periodLayout.setVerticalGroup(
            periodLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, periodLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(periodLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(periodLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(datefrom, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dateto, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(customgraph, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(68, 68, 68)
                        .addComponent(optionselected, javax.swing.GroupLayout.PREFERRED_SIZE, 356, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(graphaction, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(period, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 817, Short.MAX_VALUE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(optionselected, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(graphaction, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(43, 43, 43)
                .addComponent(period, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 75, Short.MAX_VALUE)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        salesTab.addTab("      SALES TREND     ", jPanel1);

        jLabel1.setText("Filter By Year:");

        jLabel5.setText("Month:");

        filter.setText("Filter");
        filter.setToolTipText("This will produce all transactions regardless of their status");
        filter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterActionPerformed(evt);
            }
        });

        periodsalestable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "TRANSACTION NO:", "DATE SOLD", "ORDER STATUS"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        periodsalestable.setToolTipText("Click to open order tracking window");
        periodsalestable.setRowHeight(35);
        periodsalestable.setShowGrid(true);
        periodsalestable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                periodsalestableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(periodsalestable);

        month.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" }));

        transactionmodel.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All transactions", "Reversed" }));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 829, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
                    .addComponent(year, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(month, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(transactionmodel, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(filter, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(filter, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(month, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(transactionmodel, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(year, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 455, Short.MAX_VALUE))
        );

        salesTab.addTab("             SALES DATA       ", jPanel2);

        productsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "CODE:", "ITEM NAME:", "COMPLETED:", "WORTH:"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        productsTable.setRowHeight(35);
        productsTable.setShowGrid(true);
        jScrollPane2.setViewportView(productsTable);

        jButton1.setText("Find");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 527, Short.MAX_VALUE)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(0, 16, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 433, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("Monitor product performance. Be relevant in purchases");

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("Select Product to view the sales trend ");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addGap(12, 12, 12)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );

        salesTab.addTab("            PRODUCT PERFORMANCE      ", jPanel3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(salesTab)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 14, Short.MAX_VALUE)
                .addComponent(salesTab, javax.swing.GroupLayout.PREFERRED_SIZE, 564, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void customgraphActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_customgraphActionPerformed
        //print the custom graph
        generateCustomGraph();
    }//GEN-LAST:event_customgraphActionPerformed

    private void filterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterActionPerformed
        // call the populate sales        
       EventQueue.invokeLater(new Runnable(){ 
         @Override
         public void run()
            { 
              masterPopulateSales();
            }
        });
    }//GEN-LAST:event_filterActionPerformed

    private void periodsalestableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_periodsalestableMouseClicked
        // open the order details page
        if(evt.getButton() == 1){            
            loopOrderInformation();
        }
    }//GEN-LAST:event_periodsalestableMouseClicked

    private void graphactionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_graphactionActionPerformed
        // TODO add your handling code here:          
            EventQueue.invokeLater(new Runnable(){ 
            @Override
                public void run(){ 
                    generateGraph();
                }
            });
    }//GEN-LAST:event_graphactionActionPerformed

    private void optionselectedItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_optionselectedItemStateChanged
        // TODO add your handling code here:
        if(optionselected.getSelectedItem() == "Get performance within a set period"){
            period.setVisible(true);
            graphaction.setEnabled(false);
        }else{
            graphaction.setEnabled(true);
            period.setVisible(false);            
        }
    }//GEN-LAST:event_optionselectedItemStateChanged

    //THE MAIN METHOD
    public static void main(String args[]) {
        new ThemeClass().theme();
        
        
        

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Salessummary ssl = new Salessummary();
                ssl.setVisible(true);  
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton customgraph;
    private com.toedter.calendar.JDateChooser datefrom;
    private com.toedter.calendar.JDateChooser dateto;
    protected javax.swing.JButton filter;
    private javax.swing.JButton graphaction;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JList<String> jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JComboBox<String> month;
    private javax.swing.JComboBox<String> optionselected;
    private javax.swing.JPanel period;
    private javax.swing.JTable periodsalestable;
    private javax.swing.JTable productsTable;
    public javax.swing.JTabbedPane salesTab;
    private javax.swing.JComboBox<String> transactionmodel;
    private com.toedter.calendar.JYearChooser year;
    // End of variables declaration//GEN-END:variables
}