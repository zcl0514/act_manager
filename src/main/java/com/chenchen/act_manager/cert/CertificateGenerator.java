package com.chenchen.act_manager.cert;

import javax.swing.*;
import java.awt.*;
import java.awt.Font;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

public class CertificateGenerator extends JFrame {

    // 组件声明
    private JTextField recipientField, titleField, issuerField;
    private JTextArea contentArea;
    private JComboBox<String> templateCombo;
    private JPanel previewPanel;
    private JSpinner dateSpinner;
    private JCheckBox borderCheck, sealCheck;
    private JColorChooser colorChooser;
    private JLabel previewLabel;
    private BufferedImage currentTemplate;

    // 模板图像路径
    private String[] templatePaths = {
            "classic", "modern", "elegant", "academic", "corporate"
    };

    // 字体选项
    private String[] fontNames = {"Serif", "SansSerif", "Monospaced"};
    private JComboBox<String> fontCombo;
    private JSpinner fontSizeSpinner;

    public CertificateGenerator() {
        setTitle("奖状生成器 v1.0");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        initComponents();
        pack();
        setLocationRelativeTo(null);
        setSize(1000, 700);
        setVisible(true);
    }

    private void initComponents() {
        // 左侧控制面板
        JPanel controlPanel = new JPanel(new GridBagLayout());
        controlPanel.setBorder(BorderFactory.createTitledBorder("奖状内容"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 模板选择
        gbc.gridx = 0; gbc.gridy = 0;
        controlPanel.add(new JLabel("模板样式:"), gbc);
        gbc.gridx = 1;
        String[] templates = {"经典样式", "现代样式", "优雅样式", "学术样式", "企业样式"};
        templateCombo = new JComboBox<>(templates);
        templateCombo.addActionListener(e -> updatePreview());
        controlPanel.add(templateCombo, gbc);

        // 获奖者姓名
        gbc.gridx = 0; gbc.gridy = 1;
        controlPanel.add(new JLabel("获奖者姓名:"), gbc);
        gbc.gridx = 1;
        recipientField = new JTextField(20);
        recipientField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
        });
        controlPanel.add(recipientField, gbc);

        // 奖状标题
        gbc.gridx = 0; gbc.gridy = 2;
        controlPanel.add(new JLabel("奖状标题:"), gbc);
        gbc.gridx = 1;
        titleField = new JTextField(20);
        titleField.setText("优秀员工奖");
        titleField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
        });
        controlPanel.add(titleField, gbc);

        // 奖状内容
        gbc.gridx = 0; gbc.gridy = 3;
        controlPanel.add(new JLabel("奖状内容:"), gbc);
        gbc.gridx = 1;
        contentArea = new JTextArea(4, 20);
        contentArea.setText("在2023年度工作中表现突出，业绩优异，\n特发此证，以资鼓励。");
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(contentArea);
        contentArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
        });
        gbc.fill = GridBagConstraints.BOTH;
        controlPanel.add(scrollPane, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 颁发机构
        gbc.gridx = 0; gbc.gridy = 4;
        controlPanel.add(new JLabel("颁发机构:"), gbc);
        gbc.gridx = 1;
        issuerField = new JTextField(20);
        issuerField.setText("公司人力资源部");
        issuerField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
        });
        controlPanel.add(issuerField, gbc);

        // 日期选择
        gbc.gridx = 0; gbc.gridy = 5;
        controlPanel.add(new JLabel("颁发日期:"), gbc);
        gbc.gridx = 1;
        SpinnerDateModel dateModel = new SpinnerDateModel();
        dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy年MM月dd日");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.addChangeListener(e -> updatePreview());
        controlPanel.add(dateSpinner, gbc);

        // 字体设置
        gbc.gridx = 0; gbc.gridy = 6;
        controlPanel.add(new JLabel("字体:"), gbc);
        gbc.gridx = 1;
        fontCombo = new JComboBox<>(fontNames);
        fontCombo.addActionListener(e -> updatePreview());
        controlPanel.add(fontCombo, gbc);

        // 字体大小
        gbc.gridx = 0; gbc.gridy = 7;
        controlPanel.add(new JLabel("字体大小:"), gbc);
        gbc.gridx = 1;
        SpinnerNumberModel fontSizeModel = new SpinnerNumberModel(24, 12, 48, 1);
        fontSizeSpinner = new JSpinner(fontSizeModel);
        fontSizeSpinner.addChangeListener(e -> updatePreview());
        controlPanel.add(fontSizeSpinner, gbc);

        // 颜色选择
        gbc.gridx = 0; gbc.gridy = 8;
        controlPanel.add(new JLabel("文字颜色:"), gbc);
        gbc.gridx = 1;
        colorChooser = new JColorChooser(Color.BLACK);
        colorChooser.setPreviewPanel(new JPanel()); // 隐藏预览面板
        colorChooser.getSelectionModel().addChangeListener(e -> updatePreview());
        controlPanel.add(colorChooser, gbc);

        // 选项
        gbc.gridx = 0; gbc.gridy = 9;
        gbc.gridwidth = 2;
        JPanel optionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        borderCheck = new JCheckBox("显示边框");
        borderCheck.setSelected(true);
        borderCheck.addActionListener(e -> updatePreview());
        sealCheck = new JCheckBox("显示印章");
        sealCheck.setSelected(true);
        sealCheck.addActionListener(e -> updatePreview());
        optionPanel.add(borderCheck);
        optionPanel.add(sealCheck);
        controlPanel.add(optionPanel, gbc);

        // 按钮面板
        gbc.gridx = 0; gbc.gridy = 10;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton saveImageBtn = new JButton("保存为图片");
        saveImageBtn.addActionListener(e -> saveAsImage());
        JButton savePdfBtn = new JButton("保存为PDF");
        savePdfBtn.addActionListener(e -> saveAsPDF());
        JButton printBtn = new JButton("打印");
        printBtn.addActionListener(e -> printCertificate());
        buttonPanel.add(saveImageBtn);
        buttonPanel.add(savePdfBtn);
        buttonPanel.add(printBtn);
        controlPanel.add(buttonPanel, gbc);

        // 右侧预览面板
        previewPanel = new JPanel(new BorderLayout());
        previewPanel.setBorder(BorderFactory.createTitledBorder("奖状预览"));
        previewLabel = new JLabel();
        previewLabel.setHorizontalAlignment(JLabel.CENTER);
        previewLabel.setVerticalAlignment(JLabel.CENTER);
        previewPanel.add(new JScrollPane(previewLabel), BorderLayout.CENTER);

        // 添加到主窗口
        add(controlPanel, BorderLayout.WEST);
        add(previewPanel, BorderLayout.CENTER);

        // 初始化预览
        updatePreview();
    }

    private void updatePreview() {
        // 创建预览图像
        int width = 600;
        int height = 400;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // 抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // 绘制背景
        int templateIndex = templateCombo.getSelectedIndex();
        Color bgColor;
        switch (templateIndex) {
            case 0: bgColor = new Color(255, 250, 240); break; // 经典米色
            case 1: bgColor = new Color(240, 248, 255); break; // 现代浅蓝
            case 2: bgColor = new Color(255, 245, 250); break; // 优雅浅粉
            case 3: bgColor = new Color(250, 255, 250); break; // 学术浅绿
            case 4: bgColor = new Color(255, 255, 255); break; // 企业白色
            default: bgColor = Color.WHITE;
        }
        g2d.setColor(bgColor);
        g2d.fillRect(0, 0, width, height);

        // 绘制边框
        if (borderCheck.isSelected()) {
            g2d.setColor(new Color(139, 69, 19)); // 棕色边框
            g2d.setStroke(new BasicStroke(3));
            g2d.drawRect(10, 10, width - 20, height - 20);

            // 装饰性边角
            g2d.setStroke(new BasicStroke(2));
            int cornerSize = 20;
            g2d.drawLine(10, 10, 10 + cornerSize, 10);
            g2d.drawLine(10, 10, 10, 10 + cornerSize);
            g2d.drawLine(width - 10, 10, width - 10 - cornerSize, 10);
            g2d.drawLine(width - 10, 10, width - 10, 10 + cornerSize);
            g2d.drawLine(10, height - 10, 10 + cornerSize, height - 10);
            g2d.drawLine(10, height - 10, 10, height - 10 - cornerSize);
            g2d.drawLine(width - 10, height - 10, width - 10 - cornerSize, height - 10);
            g2d.drawLine(width - 10, height - 10, width - 10, height - 10 - cornerSize);
        }

        // 设置字体和颜色
        String fontName = (String) fontCombo.getSelectedItem();
        int fontSize = (Integer) fontSizeSpinner.getValue();
        Font font = new Font(fontName, Font.BOLD, fontSize);
        g2d.setFont(font);
        g2d.setColor(colorChooser.getColor());

        // 绘制标题
        String title = titleField.getText().trim();
        if (!title.isEmpty()) {
            Font titleFont = font.deriveFont(Font.BOLD, fontSize + 10);
            g2d.setFont(titleFont);
            drawCenteredString(g2d, title, width, 80);
        }

        // 绘制"授予"
        g2d.setFont(font.deriveFont(Font.PLAIN, fontSize));
        drawCenteredString(g2d, "授予", width, 120);

        // 绘制获奖者姓名
        String recipient = recipientField.getText().trim();
        if (!recipient.isEmpty()) {
            Font nameFont = font.deriveFont(Font.BOLD, fontSize + 8);
            g2d.setFont(nameFont);
            drawCenteredString(g2d, recipient, width, 160);
        }

        // 绘制内容
        String content = contentArea.getText().trim();
        if (!content.isEmpty()) {
            g2d.setFont(font.deriveFont(Font.PLAIN, fontSize));
            drawWrappedText(g2d, content, width, 200, 200);
        }

        // 绘制颁发机构
        String issuer = issuerField.getText().trim();
        if (!issuer.isEmpty()) {
            g2d.setFont(font.deriveFont(Font.PLAIN, fontSize));
            drawCenteredString(g2d, issuer, width, height - 80);
        }

        // 绘制日期
        Date date = (Date) dateSpinner.getValue();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        String dateStr = sdf.format(date);
        g2d.setFont(font.deriveFont(Font.PLAIN, fontSize - 2));
        drawCenteredString(g2d, dateStr, width, height - 40);

        // 绘制印章
        if (sealCheck.isSelected()) {
            drawSeal(g2d, width, height);
        }

        g2d.dispose();

        // 更新预览
        currentTemplate = image;
        previewLabel.setIcon(new ImageIcon(image));
    }

    private void drawCenteredString(Graphics2D g2d, String text, int width, int y) {
        FontMetrics fm = g2d.getFontMetrics();
        int x = (width - fm.stringWidth(text)) / 2;
        g2d.drawString(text, x, y);
    }

    private void drawWrappedText(Graphics2D g2d, String text, int width, int startY, int maxWidth) {
        FontMetrics fm = g2d.getFontMetrics();
        String[] lines = text.split("\n");
        int y = startY;

        for (String line : lines) {
            if (fm.stringWidth(line) <= maxWidth) {
                drawCenteredString(g2d, line, width, y);
                y += fm.getHeight() + 5;
            } else {
                // 需要换行
                StringBuilder currentLine = new StringBuilder();
                for (String word : line.split(" ")) {
                    String testLine = currentLine + (currentLine.length() > 0 ? " " : "") + word;
                    if (fm.stringWidth(testLine) <= maxWidth) {
                        currentLine.append(currentLine.length() > 0 ? " " : "").append(word);
                    } else {
                        if (currentLine.length() > 0) {
                            drawCenteredString(g2d, currentLine.toString(), width, y);
                            y += fm.getHeight() + 5;
                        }
                        currentLine = new StringBuilder(word);
                    }
                }
                if (currentLine.length() > 0) {
                    drawCenteredString(g2d, currentLine.toString(), width, y);
                    y += fm.getHeight() + 5;
                }
            }
        }
    }

    private void drawSeal(Graphics2D g2d, int width, int height) {
        int sealX = width - 120;
        int sealY = height - 120;
        int sealSize = 80;

        // 绘制圆形印章
        g2d.setColor(Color.RED);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawOval(sealX, sealY, sealSize, sealSize);

        // 绘制五角星
        int starSize = 15;
        int centerX = sealX + sealSize/2;
        int centerY = sealY + sealSize/2;
        drawStar(g2d, centerX, centerY, starSize);

        // 绘制文字
        g2d.setFont(new Font("Serif", Font.BOLD, 10));
        String sealText = "荣誉证书";
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(sealText);
        g2d.drawString(sealText, centerX - textWidth/2, centerY + 5);
    }

    private void drawStar(Graphics2D g2d, int x, int y, int size) {
        int[] xPoints = new int[10];
        int[] yPoints = new int[10];

        for (int i = 0; i < 10; i++) {
            double angle = Math.PI / 2 + i * Math.PI / 5;
            int radius = (i % 2 == 0) ? size : size / 2;
            xPoints[i] = (int) (x + radius * Math.cos(angle));
            yPoints[i] = (int) (y - radius * Math.sin(angle));
        }

        g2d.fillPolygon(xPoints, yPoints, 10);
    }

    private void saveAsImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("保存奖状为图片");
        fileChooser.setSelectedFile(new File("certificate.png"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".png")) {
                fileToSave = new File(filePath + ".png");
            }

            try {
                // 创建高分辨率图像用于保存
                int saveWidth = 1200;
                int saveHeight = 800;
                BufferedImage saveImage = new BufferedImage(saveWidth, saveHeight, BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = saveImage.createGraphics();

                // 抗锯齿
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                // 重新绘制奖状到高分辨率图像
                // 这里简化处理，实际应该重新绘制所有内容
                g2d.drawImage(currentTemplate, 0, 0, saveWidth, saveHeight, null);
                g2d.dispose();

                // 保存图像
                ImageIO.write(saveImage, "PNG", fileToSave);
                JOptionPane.showMessageDialog(this, "奖状已保存为图片: " + fileToSave.getName(),
                        "保存成功", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "保存图片时出错: " + ex.getMessage(),
                        "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveAsPDF() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("保存奖状为PDF");
        fileChooser.setSelectedFile(new File("certificate.pdf"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".pdf")) {
                fileToSave = new File(filePath + ".pdf");
            }

            try {
                // 创建PDF文档
                Document document = new Document(PageSize.A4.rotate()); // 横向A4
                PdfWriter.getInstance(document, new FileOutputStream(fileToSave));
                document.open();

                // 添加标题
                com.itextpdf.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24);
                Paragraph title = new Paragraph(titleField.getText(), titleFont);
                title.setAlignment(Element.ALIGN_CENTER);
                document.add(title);

                // 添加内容
                document.add(new Paragraph(" ")); // 空行

                com.itextpdf.text.Font contentFont = FontFactory.getFont(FontFactory.HELVETICA, 18);
                Paragraph content = new Paragraph("授予: " + recipientField.getText(), contentFont);
                content.setAlignment(Element.ALIGN_CENTER);
                document.add(content);

                document.add(new Paragraph(" ")); // 空行

                Paragraph details = new Paragraph(contentArea.getText(), contentFont);
                details.setAlignment(Element.ALIGN_CENTER);
                document.add(details);

                document.add(new Paragraph(" ")); // 空行
                document.add(new Paragraph(" ")); // 空行

                // 添加颁发机构和日期
                Paragraph issuer = new Paragraph(issuerField.getText(), contentFont);
                issuer.setAlignment(Element.ALIGN_RIGHT);
                document.add(issuer);

                Date date = (Date) dateSpinner.getValue();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
                Paragraph dateParagraph = new Paragraph(sdf.format(date), contentFont);
                dateParagraph.setAlignment(Element.ALIGN_RIGHT);
                document.add(dateParagraph);

                document.close();

                JOptionPane.showMessageDialog(this, "奖状已保存为PDF: " + fileToSave.getName(),
                        "保存成功", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "保存PDF时出错: " + ex.getMessage(),
                        "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void printCertificate() {
        JOptionPane.showMessageDialog(this, "打印功能需要连接打印机。\n请确保打印机已正确设置。",
                "打印", JOptionPane.INFORMATION_MESSAGE);

        // 实际打印功能需要更多实现
        // 这里只是演示
    }

    public static void main(String[] args) {
        // 设置外观
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 启动程序
        SwingUtilities.invokeLater(() -> new CertificateGenerator());
    }
}