package com.benesoft.superposadmin;
//imports
import java.awt.event.KeyEvent;
import java.sql.*;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author benedict
 */
public class AllProducts extends javax.swing.JFrame implements Runnable {
    @Override
    public void run() {
        /*try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }*/
        masterPopulate();
    }

    //global initializations
    ConnectionClass cls = new ConnectionClass();

    /**
     * Creates new form AllProducts
     */
    public AllProducts() {
        initComponents();
    }

    /*--------------------------------
    Master method to popyulate table
    ---------------------------------*/
    public void masterPopulate() {
        if (cls.getConnection() == true) {
            populateProductsTable();
        } else {
            JOptionPane.showMessageDialog(null, "Failed to establish connection to the server");
        }
    }

    /*--------------------------------------------------
    get all products 
    --------------------------------------------------*/
    private void populateProductsTable() {
        DefaultTableModel tb = (DefaultTableModel) catalogtable.getModel();
        try {
            String query = "SELECT * FROM products";
            PreparedStatement ps = cls.conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String code = rs.getString("productCode").toLowerCase();
                String name = rs.getString("productName").toLowerCase();
                String category = rs.getString("productCategory").toLowerCase();
                String quantity = rs.getString("productQuantity").toLowerCase();
                String size = rs.getString("productSize").toLowerCase();
                String price = rs.getString("productPrice").toLowerCase();
                String[] tbdata = {code, name, category, quantity, size, price};
                //populate the table
                tb.addRow(tbdata);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Something went wrong with fetching data");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Oops, something went wrong");
        }
    }

    
    
    /*--------------------------------------------------------------
    Get the product data for the selected item on the catalog table 
    --------------------------------------------------------------*/
    protected String[] getData() {
        //get data from table        
        String prodcode = catalogtable.getValueAt(catalogtable.getSelectedRow(), 0).toString();
        String prodname = catalogtable.getValueAt(catalogtable.getSelectedRow(), 1).toString();
        String prodcategory = catalogtable.getValueAt(catalogtable.getSelectedRow(), 2).toString();
        String prodsize = catalogtable.getValueAt(catalogtable.getSelectedRow(), 4).toString();
        String prodprice = catalogtable.getValueAt(catalogtable.getSelectedRow(), 5).toString();
        try {
            String[] productdata = {prodcode, prodname, prodcategory, prodsize, prodprice};
            return productdata;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Oops, something is wrong in getting productdata\n" + e);
            return null;
        }
    }

    /*----------------------------------------------------------------------------
    Update information on database after editing it from the catalog table cells
    ----------------------------------------------------------------------------*/
    protected void updateProduct() {
        if (cls.getConnection() == true) {
            try {
                catalogtable.getValueAt(catalogtable.getSelectedRow(), 1);
                String query = "UPDATE products SET productName=?, productCategory=?, productSize=?, productPrice=? WHERE productCode=?";
                PreparedStatement pst = cls.conn.prepareStatement(query);
                pst.setString(1, getData()[1].toLowerCase().trim());
                pst.setString(2, getData()[2].toLowerCase().trim());
                pst.setString(3, getData()[3].toLowerCase().trim());
                pst.setInt(4, Integer.parseInt(getData()[4].trim()));
                pst.setString(5, getData()[0].trim());
                //then now update
                pst.executeUpdate();
                JOptionPane.showMessageDialog(null, "Product: "+getData()[1] +"\nInformation about this product has been updated succesfully");
                catalogtable.clearSelection();
            }catch (NumberFormatException ne) {
                JOptionPane.showMessageDialog(null, "Please use numbers between \"0-9\" for the price field");
            }catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Oops, could not update product. please try again\n");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Failed to establish connection to the server");
        }
    }

    /*--------------------------------------------------------
    Update information on database
    -------------------------------------------------------*/
    protected void deleteProduct() {
        DefaultTableModel tb = (DefaultTableModel) catalogtable.getModel();
        if (cls.getConnection() == true) {
            try {
                String query = "DELETE FROM products WHERE productCode=?";
                PreparedStatement pst = cls.conn.prepareStatement(query);
                pst.setString(1, getData()[0].trim());
                //then now delete
                pst.executeUpdate();
                JOptionPane.showMessageDialog(null, "Affected product: "+getData()[1]+ "\nHas been removed from the system succesfully.");

                //then remove that row from table
                tb.removeRow(Integer.parseInt(getData()[5]));
                catalogtable.clearSelection();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Oops, could not update product. please try again\n" + e);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Failed to establish connection to the server");
        }
    }

    //SEARCH THE PRODUCT using either "find buttn" or or clicking on the enter button
    protected void searchProduct() {
        String searched = searcharea.getText();
        DefaultTableModel tbl = (DefaultTableModel) catalogtable.getModel();
        if (cls.getConnection() == true) {
            try {
                String query = "SELECT * FROM products WHERE productCode LIKE ? || productName LIKE ? || productCategory LIKE ?";
                PreparedStatement ps = cls.conn.prepareStatement(query);
                ps.setString(1, "%" + searched.trim() + "%");
                ps.setString(2, "%" + searched.trim() + "%");
                ps.setString(3, "%" + searched.trim() + "%");
                ResultSet rs = ps.executeQuery();
                //check if table has data
                if (catalogtable.getRowCount() > 0) {
                    tbl.setRowCount(0);
                }
                //populate the table now
                while (rs.next()) {     
                    String code = rs.getString("productCode");
                    String name = rs.getString("productName");
                    String category = rs.getString("productCategory");
                    String quantity = rs.getString("productQuantity");
                    String size = rs.getString("productSize");
                    String price = rs.getString("productPrice");
                    String[] tbdata = {code, name, category, quantity, size, price};
                    //populate the table
                    tbl.addRow(tbdata);                    
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Oops, failed to search. something went wrong");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Failed to establish connection to the server");
        }
    }

    //check if search area is empty. if yes, call the populate nmethod
    protected void searchIsEmpty() {
        if (searcharea.getText().isEmpty()) {
            DefaultTableModel tbl = (DefaultTableModel) catalogtable.getModel();
            if (tbl.getRowCount() > 0) {
                tbl.setRowCount(0);
            }
            masterPopulate();
        }
    }

  

    //show help when the help label is clicked
    protected void help() {
        JFrame frame = new JFrame();
        frame.setSize(600, 200);
        frame.setTitle("Help in Usage");
        JList jl = new JList();
        String opt1 = "To edit a product, click on any row/cell.";
        String opt2 = "Double-click on a specific column to allow editing";
        String opt3 = "When you finish editing, click enter to have the row highlited, then";
        String opt4 = "Choose/select an option from actions that you want to perform then click 'Apply' button";
        String opt5 = "Done! Simple right?. Difficulties, call 0708672495";
        String[] listData = {opt1, opt2, opt3, opt4, opt5};
        jl.setListData(listData);
        frame.add(jl);
        frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        frame.setResizable(false);
        frame.setAlwaysOnTop(true);
        frame.setLocation(200, 170);
        frame.requestFocus();
        frame.setVisible(true);
    }
    
     //the table filter method
    protected void productsFilter(String str) {
        DefaultTableModel tbmodel = (DefaultTableModel) catalogtable.getModel();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tbmodel);
        catalogtable.setRowSorter(sorter);
        sorter.setRowFilter(RowFilter.regexFilter(str));      
    }
 
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        catalogtable = new javax.swing.JTable();
        searcharea = new javax.swing.JTextField();
        find = new javax.swing.JButton();
        action = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        options = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        help = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Products catalog");
        setAlwaysOnTop(true);
        setPreferredSize(new java.awt.Dimension(1100, 608));
        setResizable(false);

        catalogtable.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        catalogtable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "CODE", "PROD NAME", "CATEGORY", "INSTOCK", "SIZE ", "PRICE"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, true, false, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        catalogtable.setToolTipText("Click once to select. Double click a specific cell to allow edit");
        catalogtable.setRowHeight(35);
        catalogtable.setShowGrid(true);
        catalogtable.setShowVerticalLines(false);
        catalogtable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                catalogtableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(catalogtable);
        if (catalogtable.getColumnModel().getColumnCount() > 0) {
            catalogtable.getColumnModel().getColumn(0).setResizable(false);
            catalogtable.getColumnModel().getColumn(0).setPreferredWidth(30);
            catalogtable.getColumnModel().getColumn(1).setResizable(false);
            catalogtable.getColumnModel().getColumn(2).setResizable(false);
            catalogtable.getColumnModel().getColumn(2).setPreferredWidth(60);
            catalogtable.getColumnModel().getColumn(2).setCellEditor(null);
            catalogtable.getColumnModel().getColumn(3).setResizable(false);
            catalogtable.getColumnModel().getColumn(3).setPreferredWidth(20);
            catalogtable.getColumnModel().getColumn(4).setResizable(false);
            catalogtable.getColumnModel().getColumn(4).setPreferredWidth(50);
            catalogtable.getColumnModel().getColumn(5).setResizable(false);
            catalogtable.getColumnModel().getColumn(5).setPreferredWidth(30);
        }

        searcharea.setBackground(new java.awt.Color(255, 255, 204));
        searcharea.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 204, 204), 1, true));
        searcharea.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchareaKeyReleased(evt);
            }
        });

        find.setText("Find");
        find.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                findActionPerformed(evt);
            }
        });

        action.setText("Apply");
        action.setEnabled(false);
        action.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                actionActionPerformed(evt);
            }
        });

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Actions");

        jLabel2.setText("Enter product name/code to filter");

        options.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-select option-", "Update Selected", "Delete selected" }));

        jLabel1.setForeground(new java.awt.Color(0, 204, 204));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("What can be edited: Product name, product category, product price, product size");

        help.setFont(new java.awt.Font("Liberation Sans", 0, 20)); // NOI18N
        help.setForeground(new java.awt.Color(0, 102, 255));
        help.setText("Help?");
        help.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                helpMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 780, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(searcharea, javax.swing.GroupLayout.PREFERRED_SIZE, 305, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(find)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(options, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(action))
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 368, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(help)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(searcharea, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(find, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(action, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(options, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(help))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 402, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void actionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_actionActionPerformed
        // Update or delete the selected product
        if (options.getSelectedItem() == "Update Selected") {
            updateProduct();
        } else if (options.getSelectedItem() == "Delete selected") {
            deleteProduct();
        } else {
            JOptionPane.showMessageDialog(null, "Please select the effect to make then reenter");
        }
    }//GEN-LAST:event_actionActionPerformed

    private void catalogtableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_catalogtableMouseClicked
        // TODO add your handling code here:
        action.setEnabled(true);        
    }//GEN-LAST:event_catalogtableMouseClicked

    private void helpMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_helpMouseClicked
        // use the help method
        help();
    }//GEN-LAST:event_helpMouseClicked

    private void searchareaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchareaKeyReleased
        // TODO add your handling code here:
        //search if enter key is pressed        
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            searchProduct();
        }
        searchIsEmpty();
        productsFilter(searcharea.getText().trim());
    }//GEN-LAST:event_searchareaKeyReleased

    private void findActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_findActionPerformed
        // search product
        searchProduct();
    }//GEN-LAST:event_findActionPerformed

    
    
    
    //the main method begins here
    public static void main(String args[]) {
        new ThemeClass().theme();

        
        
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                AllProducts all = new AllProducts();
                all.setVisible(true);
                new Thread(all).start();
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton action;
    private javax.swing.JTable catalogtable;
    private javax.swing.JButton find;
    private javax.swing.JLabel help;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JComboBox<String> options;
    private javax.swing.JTextField searcharea;
    // End of variables declaration//GEN-END:variables
}
