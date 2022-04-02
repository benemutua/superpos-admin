package com.benesoft.superposadmin;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.UIManager;

/**
 *
 * @author dev
 */
public class ThemeClass {
    
    void theme(){
    try {
        UIManager.setLookAndFeel( new FlatLightLaf() );
    } catch(Exception ex ) {
        System.err.println( "Failed to initialize LaF" );
    }        
}
}
