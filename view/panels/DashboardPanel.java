package view.panels;

import service.PharmacyService;
import view.Theme;

import javax.swing.*;
import java.awt.*;

public class DashboardPanel extends JPanel {
    private PharmacyService pharmacyService;


    public DashboardPanel(PharmacyService service) {
        this.pharmacyService = service;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Theme.BG);

        // Header
        JLabel titleLabel = new JLabel("Dashboard Overview", SwingConstants.CENTER);
        titleLabel.setFont(Theme.FONT_TITLE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Cards Panel
        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        cardsPanel.setBackground(Theme.BG);

        cardsPanel.add(createCard("Total Sales Today", "$ " + pharmacyService.getTotalSalesToday(), Theme.PRIMARY));
        cardsPanel.add(createCard("Low Stock Alerts", String.valueOf(pharmacyService.getLowStockCount(20)), Theme.DANGER));
        cardsPanel.add(createCard("Active Products", String.valueOf(pharmacyService.getAllProducts().size()), Theme.SUCCESS));

        add(cardsPanel, BorderLayout.CENTER);
    }
    
    private JPanel createCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Theme.SURFACE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER, 1),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        JPanel accent = new JPanel();
        accent.setBackground(color);
        accent.setPreferredSize(new Dimension(8, 0));
        
        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setForeground(Theme.MUTED_TEXT);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        
        JLabel lblValue = new JLabel(value, SwingConstants.CENTER);
        lblValue.setForeground(Theme.TEXT);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblValue.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        card.add(accent, BorderLayout.WEST);
        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);
        
        return card;
    }
}
