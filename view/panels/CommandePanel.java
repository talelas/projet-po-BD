package view.panels;

import service.PharmacyService;

import javax.swing.*;
import java.awt.*;

public class CommandePanel extends JPanel {
    private PharmacyService service;
    
    public CommandePanel(PharmacyService service) {
        this.service = service;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        JLabel label = new JLabel("📦 Supplier Orders - Coming Soon", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 24));
        label.setForeground(new Color(127, 140, 141));
        add(label, BorderLayout.CENTER);
    }
}
