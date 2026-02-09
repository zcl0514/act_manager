package com.chenchen.act_manager.cert;

import javax.swing.*;
import java.awt.*;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;
import javax.swing.border.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

public class FamilyCertificateGenerator extends JFrame {

    // 组件声明
    private JTextField recipientField, titleField, issuerField;
    private JTextArea contentArea, reasonArea;
    private JComboBox<String> templateCombo, awardTypeCombo;
    private JPanel previewPanel;
    private JSpinner dateSpinner;
    private JCheckBox sealCheck, ribbonCheck, goldenEdgeCheck;
    private JColorChooser colorChooser;
    private JLabel previewLabel;
    private BufferedImage currentTemplate;

    // 奖状类型
    private String[] awardTypes = {"荣誉证书", "奖状", "表扬信", "嘉奖令", "进步奖", "优秀奖"};

    // 传统字体
    private String[] fontNames = {"宋体", "楷体", "黑体", "隶书", "华文行楷", "方正姚体"};
    private JComboBox<String> fontCombo;
    private JSpinner fontSizeSpinner, borderWidthSpinner;

    // 奖状颜色方案
    private Color[] certificateColors = {
            new Color(255, 248, 220),  // 米黄色 - 传统
            new Color(240, 248, 255),  // 淡蓝色 - 典雅
            new Color(255, 250, 250),  // 雪白色 - 正式
            new Color(245, 245, 220),  // 米白色 - 温馨
            new Color(255, 253, 240)   // 乳白色 - 荣誉
    };

    public FamilyCertificateGenerator() {
        setTitle("家庭荣誉奖状生成系统");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // 设置窗口图标
        try {
            setIconImage(createIconImage());
        } catch (Exception e) {
            // 如果创建图标失败，使用默认
        }

        initComponents();
        pack();
        setLocationRelativeTo(null);
        setSize(1200, 800);
        setVisible(true);
    }

    private Image createIconImage() {
        // 创建一个简单的奖状图标
        BufferedImage icon = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = icon.createGraphics();
        g2d.setColor(new Color(218, 165, 32)); // 金色
        g2d.fillRect(0, 0, 32, 32);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("宋体", Font.BOLD, 24));
        g2d.drawString("奖", 8, 25);
        g2d.dispose();
        return icon;
    }

    private void initComponents() {
        // 主面板
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(248, 248, 255));

        // 左侧控制面板 - 传统风格
        JPanel controlPanel = new JPanel(new GridBagLayout());
        controlPanel.setBackground(new Color(255, 253, 250));
        controlPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(139, 69, 19), 2),
                        "奖状设计面板"
                ),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // 奖状类型选择
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel typeLabel = new JLabel("奖状类型:");
        typeLabel.setFont(new Font("宋体", Font.BOLD, 14));
        controlPanel.add(typeLabel, gbc);
        gbc.gridx = 1;
        awardTypeCombo = new JComboBox<>(awardTypes);
        awardTypeCombo.setFont(new Font("宋体", Font.PLAIN, 14));
        awardTypeCombo.addActionListener(e -> updatePreview());
        controlPanel.add(awardTypeCombo, gbc);

        // 模板选择
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel templateLabel = new JLabel("模板样式:");
        templateLabel.setFont(new Font("宋体", Font.BOLD, 14));
        controlPanel.add(templateLabel, gbc);
        gbc.gridx = 1;
        String[] templates = {"传统经典", "现代典雅", "家庭温馨", "学校荣誉", "商务正式"};
        templateCombo = new JComboBox<>(templates);
        templateCombo.setFont(new Font("宋体", Font.PLAIN, 14));
        templateCombo.addActionListener(e -> updatePreview());
        controlPanel.add(templateCombo, gbc);

        // 获奖者姓名
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel nameLabel = new JLabel("获奖人:");
        nameLabel.setFont(new Font("宋体", Font.BOLD, 14));
        controlPanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        recipientField = new JTextField(20);
        recipientField.setFont(new Font("楷体", Font.PLAIN, 16));
        recipientField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(184, 134, 11), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        recipientField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
        });
        controlPanel.add(recipientField, gbc);

        // 奖状标题
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel titleTextLabel = new JLabel("奖状标题:");
        titleTextLabel.setFont(new Font("宋体", Font.BOLD, 14));
        controlPanel.add(titleTextLabel, gbc);
        gbc.gridx = 1;
        titleField = new JTextField(20);
        titleField.setFont(new Font("楷体", Font.PLAIN, 16));
        titleField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(184, 134, 11), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        titleField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
        });
        controlPanel.add(titleField, gbc);

        // 获奖理由
        gbc.gridx = 0; gbc.gridy = 4;
        JLabel reasonLabel = new JLabel("获奖理由:");
        reasonLabel.setFont(new Font("宋体", Font.BOLD, 14));
        controlPanel.add(reasonLabel, gbc);
        gbc.gridx = 1;
        reasonArea = new JTextArea(3, 20);
        reasonArea.setFont(new Font("楷体", Font.PLAIN, 14));
        reasonArea.setText("在家庭生活中表现优异，体现了良好的家庭责任感");
        reasonArea.setLineWrap(true);
        reasonArea.setWrapStyleWord(true);
        JScrollPane reasonScroll = new JScrollPane(reasonArea);
        reasonScroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(184, 134, 11), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        reasonArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
        });
        controlPanel.add(reasonScroll, gbc);

        // 奖状内容
        gbc.gridx = 0; gbc.gridy = 5;
        JLabel contentLabel = new JLabel("奖状正文:");
        contentLabel.setFont(new Font("宋体", Font.BOLD, 14));
        controlPanel.add(contentLabel, gbc);
        gbc.gridx = 1;
        contentArea = new JTextArea(4, 20);
        contentArea.setFont(new Font("楷体", Font.PLAIN, 14));
        contentArea.setText("特发此证，以资鼓励。\n希望继续发扬优良品质，为家庭做出更大贡献。");
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        JScrollPane contentScroll = new JScrollPane(contentArea);
        contentScroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(184, 134, 11), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        contentArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
        });
        controlPanel.add(contentScroll, gbc);

        // 颁发机构
        gbc.gridx = 0; gbc.gridy = 6;
        JLabel issuerLabel = new JLabel("颁发单位:");
        issuerLabel.setFont(new Font("宋体", Font.BOLD, 14));
        controlPanel.add(issuerLabel, gbc);
        gbc.gridx = 1;
        issuerField = new JTextField(20);
        issuerField.setFont(new Font("楷体", Font.PLAIN, 16));
        issuerField.setText("家庭委员会");
        issuerField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(184, 134, 11), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        issuerField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
        });
        controlPanel.add(issuerField, gbc);

        // 日期选择
        gbc.gridx = 0; gbc.gridy = 7;
        JLabel dateLabel = new JLabel("颁发日期:");
        dateLabel.setFont(new Font("宋体", Font.BOLD, 14));
        controlPanel.add(dateLabel, gbc);
        gbc.gridx = 1;
        SpinnerDateModel dateModel = new SpinnerDateModel();
        dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy年MM月dd日");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.addChangeListener(e -> updatePreview());
        controlPanel.add(dateSpinner, gbc);

        // 字体设置
        gbc.gridx = 0; gbc.gridy = 8;
        JLabel fontLabel = new JLabel("正文字体:");
        fontLabel.setFont(new Font("宋体", Font.BOLD, 14));
        controlPanel.add(fontLabel, gbc);
        gbc.gridx = 1;
        fontCombo = new JComboBox<>(fontNames);
        fontCombo.addActionListener(e -> updatePreview());
        controlPanel.add(fontCombo, gbc);

        // 字体大小
        gbc.gridx = 0; gbc.gridy = 9;
        JLabel sizeLabel = new JLabel("字体大小:");
        sizeLabel.setFont(new Font("宋体", Font.BOLD, 14));
        controlPanel.add(sizeLabel, gbc);
        gbc.gridx = 1;
        SpinnerNumberModel fontSizeModel = new SpinnerNumberModel(20, 12, 36, 1);
        fontSizeSpinner = new JSpinner(fontSizeModel);
        fontSizeSpinner.addChangeListener(e -> updatePreview());
        controlPanel.add(fontSizeSpinner, gbc);

        // 边框宽度
        gbc.gridx = 0; gbc.gridy = 10;
        JLabel borderLabel = new JLabel("边框宽度:");
        borderLabel.setFont(new Font("宋体", Font.BOLD, 14));
        controlPanel.add(borderLabel, gbc);
        gbc.gridx = 1;
        SpinnerNumberModel borderModel = new SpinnerNumberModel(3, 1, 10, 1);
        borderWidthSpinner = new JSpinner(borderModel);
        borderWidthSpinner.addChangeListener(e -> updatePreview());
        controlPanel.add(borderWidthSpinner, gbc);

        // 颜色选择
        gbc.gridx = 0; gbc.gridy = 11;
        gbc.gridwidth = 2;
        JPanel colorPanel = new JPanel(new BorderLayout());
        colorPanel.setBorder(BorderFactory.createTitledBorder("文字颜色"));
        colorChooser = new JColorChooser(new Color(139, 0, 0)); // 深红色
        colorChooser.setPreviewPanel(new JPanel());
        colorChooser.getSelectionModel().addChangeListener(e -> updatePreview());
        colorPanel.add(colorChooser, BorderLayout.CENTER);
        controlPanel.add(colorPanel, gbc);

        // 装饰选项面板
        gbc.gridx = 0; gbc.gridy = 12;
        gbc.gridwidth = 2;
        JPanel optionPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        optionPanel.setBorder(BorderFactory.createTitledBorder("装饰选项"));

        sealCheck = new JCheckBox("显示印章");
        sealCheck.setSelected(true);
        sealCheck.addActionListener(e -> updatePreview());

        ribbonCheck = new JCheckBox("显示绶带");
        ribbonCheck.setSelected(true);
        ribbonCheck.addActionListener(e -> updatePreview());

        goldenEdgeCheck = new JCheckBox("烫金边框");
        goldenEdgeCheck.setSelected(true);
        goldenEdgeCheck.addActionListener(e -> updatePreview());

        optionPanel.add(sealCheck);
        optionPanel.add(ribbonCheck);
        optionPanel.add(goldenEdgeCheck);
        controlPanel.add(optionPanel, gbc);

        // 按钮面板
        gbc.gridx = 0; gbc.gridy = 13;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 10, 10));

        // 保存为图片按钮
        JButton saveImageBtn = new JButton("保存为图片");
        saveImageBtn.setFont(new Font("宋体", Font.BOLD, 14));
        saveImageBtn.setBackground(new Color(70, 130, 180));
        saveImageBtn.setForeground(Color.WHITE);
        saveImageBtn.addActionListener(e -> saveAsImage());

        // 保存为PDF按钮
        JButton savePdfBtn = new JButton("保存为PDF");
        savePdfBtn.setFont(new Font("宋体", Font.BOLD, 14));
        savePdfBtn.setBackground(new Color(60, 179, 113));
        savePdfBtn.setForeground(Color.WHITE);
        savePdfBtn.addActionListener(e -> saveAsPDF());

        // 打印按钮
        JButton printBtn = new JButton("打印奖状");
        printBtn.setFont(new Font("宋体", Font.BOLD, 14));
        printBtn.setBackground(new Color(218, 165, 32));
        printBtn.setForeground(Color.WHITE);
        printBtn.addActionListener(e -> printCertificate());

        // 高级设置按钮
        JButton advancedBtn = new JButton("高级设置");
        advancedBtn.setFont(new Font("宋体", Font.BOLD, 14));
        advancedBtn.setBackground(new Color(138, 43, 226));
        advancedBtn.setForeground(Color.WHITE);
        advancedBtn.addActionListener(e -> showAdvancedSettings());

        buttonPanel.add(saveImageBtn);
        buttonPanel.add(savePdfBtn);
        buttonPanel.add(printBtn);
        buttonPanel.add(advancedBtn);
        controlPanel.add(buttonPanel, gbc);

        // 右侧预览面板
        previewPanel = new JPanel(new BorderLayout());
        previewPanel.setBackground(new Color(240, 248, 255));
        previewPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(139, 69, 19), 3),
                        "奖状预览"
                ),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        previewLabel = new JLabel();
        previewLabel.setHorizontalAlignment(JLabel.CENTER);
        previewLabel.setVerticalAlignment(JLabel.CENTER);

        // 添加滚动条以适应大尺寸预览
        JScrollPane previewScroll = new JScrollPane(previewLabel);
        previewScroll.setBorder(null);
        previewPanel.add(previewScroll, BorderLayout.CENTER);

        // 状态栏
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBackground(new Color(240, 240, 240));
        JLabel statusLabel = new JLabel("就绪");
        statusLabel.setFont(new Font("宋体", Font.PLAIN, 12));
        statusPanel.add(statusLabel);

        // 添加到主窗口
        mainPanel.add(controlPanel, BorderLayout.WEST);
        mainPanel.add(previewPanel, BorderLayout.CENTER);
        mainPanel.add(statusPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // 初始化预览
        updatePreview();
    }

    private void showAdvancedSettings() {
        JOptionPane.showMessageDialog(this,
                "高级设置功能：\n" +
                        "1. 自定义背景纹理\n" +
                        "2. 添加水印文字\n" +
                        "3. 设置纸张大小\n" +
                        "4. 调整打印边距\n" +
                        "5. 导入自定义印章\n\n" +
                        "这些功能将在后续版本中实现。",
                "高级设置",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void updatePreview() {
        // 创建预览图像 - 使用更大的尺寸
        int width = 800;
        int height = 600;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // 抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // 根据模板绘制背景
        int templateIndex = templateCombo.getSelectedIndex();
        Color bgColor = certificateColors[templateIndex % certificateColors.length];
        g2d.setColor(bgColor);
        g2d.fillRect(0, 0, width, height);

        // 绘制传统边框
        int borderWidth = (Integer) borderWidthSpinner.getValue();
        drawTraditionalBorder(g2d, width, height, borderWidth, templateIndex);

        // 绘制烫金边框
        if (goldenEdgeCheck.isSelected()) {
            drawGoldenEdge(g2d, width, height);
        }

        // 绘制绶带
        if (ribbonCheck.isSelected()) {
            drawRibbon(g2d, width, height, templateIndex);
        }

        // 设置字体
        String fontName = (String) fontCombo.getSelectedItem();
        int fontSize = (Integer) fontSizeSpinner.getValue();

        // 绘制奖状类型
        String awardType = (String) awardTypeCombo.getSelectedItem();
        Font awardFont = new Font(fontName, Font.BOLD, fontSize + 16);
        g2d.setFont(awardFont);
        g2d.setColor(new Color(178, 34, 34)); // 深红色
        drawCenteredString(g2d, awardType, width, 80);

        // 绘制标题
        String title = titleField.getText().trim();
        if (!title.isEmpty()) {
            Font titleFont = new Font(fontName, Font.BOLD, fontSize + 8);
            g2d.setFont(titleFont);
            g2d.setColor(colorChooser.getColor());
            drawCenteredString(g2d, title, width, 140);
        }

        // 绘制"授予"
        g2d.setFont(new Font(fontName, Font.PLAIN, fontSize));
        g2d.setColor(new Color(139, 69, 19)); // 棕色
        drawCenteredString(g2d, "授予", width, 180);

        // 绘制获奖者姓名
        String recipient = recipientField.getText().trim();
        if (!recipient.isEmpty()) {
            Font nameFont = new Font(fontName, Font.BOLD, fontSize + 12);
            g2d.setFont(nameFont);
            g2d.setColor(new Color(0, 0, 139)); // 深蓝色
            drawCenteredString(g2d, recipient + " 同志", width, 230);
        }

        // 绘制获奖理由
        String reason = reasonArea.getText().trim();
        if (!reason.isEmpty()) {
            g2d.setFont(new Font(fontName, Font.PLAIN, fontSize));
            g2d.setColor(new Color(47, 79, 79)); // 深灰色
            drawCenteredString(g2d, "在" + reason + "方面", width, 280);
        }

        // 绘制内容
        String content = contentArea.getText().trim();
        if (!content.isEmpty()) {
            g2d.setFont(new Font(fontName, Font.PLAIN, fontSize));
            g2d.setColor(new Color(47, 79, 79));
            drawWrappedText(g2d, content, width, 320, 500);
        }

        // 绘制颁发机构
        String issuer = issuerField.getText().trim();
        if (!issuer.isEmpty()) {
            g2d.setFont(new Font(fontName, Font.BOLD, fontSize + 2));
            g2d.setColor(new Color(0, 100, 0)); // 深绿色
            drawRightAlignedString(g2d, issuer, width - 100, height - 120);
        }

        // 绘制日期
        Date date = (Date) dateSpinner.getValue();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        String dateStr = sdf.format(date);
        g2d.setFont(new Font(fontName, Font.PLAIN, fontSize));
        g2d.setColor(new Color(105, 105, 105)); // 灰色
        drawRightAlignedString(g2d, dateStr, width - 100, height - 80);

        // 绘制编号（模拟）
        g2d.setFont(new Font("宋体", Font.PLAIN, fontSize - 4));
        g2d.setColor(new Color(160, 82, 45)); // 赭色
        drawLeftAlignedString(g2d, "编号: " + generateSerialNumber(), 100, height - 50);

        // 绘制印章
        if (sealCheck.isSelected()) {
            drawSeal(g2d, width, height, templateIndex);
        }

        g2d.dispose();

        // 更新预览
        currentTemplate = image;
        previewLabel.setIcon(new ImageIcon(image));
    }

    private String generateSerialNumber() {
        // 生成模拟的奖状编号
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String datePart = sdf.format(new Date());
        int randomPart = (int)(Math.random() * 1000);
        return "FAM-" + datePart + "-" + String.format("%03d", randomPart);
    }

    private void drawTraditionalBorder(Graphics2D g2d, int width, int height, int borderWidth, int style) {
        // 绘制外层边框
        g2d.setColor(new Color(139, 69, 19)); // 棕色
        g2d.setStroke(new BasicStroke(borderWidth));
        g2d.drawRect(30, 30, width - 60, height - 60);

        // 绘制内层边框
        g2d.setColor(new Color(184, 134, 11)); // 金色
        g2d.setStroke(new BasicStroke(borderWidth / 2.0f));
        g2d.drawRect(50, 50, width - 100, height - 100);

        // 绘制角装饰
        int cornerSize = 40;
        g2d.setStroke(new BasicStroke(2));

        // 左上角
        drawCornerDecoration(g2d, 30, 30, cornerSize, 0);
        // 右上角
        drawCornerDecoration(g2d, width - 30, 30, cornerSize, 1);
        // 左下角
        drawCornerDecoration(g2d, 30, height - 30, cornerSize, 2);
        // 右下角
        drawCornerDecoration(g2d, width - 30, height - 30, cornerSize, 3);

        // 根据样式添加装饰线
        if (style == 0) { // 传统经典
            // 添加花纹
            drawPatternDecoration(g2d, width, height);
        } else if (style == 1) { // 现代典雅
            // 简单的点线装饰
            drawDottedDecoration(g2d, width, height);
        }
    }

    private void drawGoldenEdge(Graphics2D g2d, int width, int height) {
        // 绘制烫金效果边框
        GradientPaint goldenGradient = new GradientPaint(
                0, 0, new Color(255, 215, 0),
                width, height, new Color(218, 165, 32)
        );

        g2d.setPaint(goldenGradient);
        g2d.setStroke(new BasicStroke(4, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                10.0f, new float[]{10.0f, 5.0f}, 0.0f));
        g2d.drawRect(25, 25, width - 50, height - 50);
    }

    private void drawRibbon(Graphics2D g2d, int width, int height, int style) {
        // 绘制绶带效果
        int ribbonY = 120;
        int ribbonHeight = 40;

        // 绶带颜色
        Color ribbonColor;
        switch (style) {
            case 0: ribbonColor = new Color(178, 34, 34); break; // 红色
            case 1: ribbonColor = new Color(70, 130, 180); break; // 蓝色
            case 2: ribbonColor = new Color(60, 179, 113); break; // 绿色
            case 3: ribbonColor = new Color(218, 165, 32); break; // 金色
            default: ribbonColor = new Color(138, 43, 226); // 紫色
        }

        // 绘制绶带主体
        g2d.setColor(ribbonColor);
        int ribbonWidth = 400;
        int ribbonX = (width - ribbonWidth) / 2;

        // 创建绶带形状
        Polygon ribbon = new Polygon();
        ribbon.addPoint(ribbonX, ribbonY);
        ribbon.addPoint(ribbonX + ribbonWidth, ribbonY);
        ribbon.addPoint(ribbonX + ribbonWidth - 50, ribbonY + ribbonHeight);
        ribbon.addPoint(ribbonX + 50, ribbonY + ribbonHeight);

        g2d.fill(ribbon);

        // 添加绶带阴影
        g2d.setColor(ribbonColor.darker());
        g2d.setStroke(new BasicStroke(2));
        g2d.draw(ribbon);

        // 添加绶带文字
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("黑体", Font.BOLD, 18));
        String ribbonText = "家庭荣誉";
        drawCenteredString(g2d, ribbonText, width, ribbonY + ribbonHeight/2 + 6);
    }

    private void drawCornerDecoration(Graphics2D g2d, int x, int y, int size, int corner) {
        Color[] cornerColors = {
                new Color(178, 34, 34),  // 红色
                new Color(0, 100, 0),    // 深绿
                new Color(70, 130, 180), // 钢蓝
                new Color(218, 165, 32)  // 金色
        };

        g2d.setColor(cornerColors[corner % cornerColors.length]);

        switch (corner) {
            case 0: // 左上角
                drawFlourish(g2d, x, y, size, 0);
                break;
            case 1: // 右上角
                drawFlourish(g2d, x, y, size, 1);
                break;
            case 2: // 左下角
                drawFlourish(g2d, x, y, size, 2);
                break;
            case 3: // 右下角
                drawFlourish(g2d, x, y, size, 3);
                break;
        }
    }

    private void drawFlourish(Graphics2D g2d, int x, int y, int size, int direction) {
        // 绘制装饰性花纹
        int[] xPoints, yPoints;

        switch (direction) {
            case 0: // 左上
                xPoints = new int[]{x, x + size/2, x, x + size/4};
                yPoints = new int[]{y, y + size/4, y + size/2, y};
                break;
            case 1: // 右上
                xPoints = new int[]{x, x - size/2, x, x - size/4};
                yPoints = new int[]{y, y + size/4, y + size/2, y};
                break;
            case 2: // 左下
                xPoints = new int[]{x, x + size/2, x, x + size/4};
                yPoints = new int[]{y, y - size/4, y - size/2, y};
                break;
            default: // 右下
                xPoints = new int[]{x, x - size/2, x, x - size/4};
                yPoints = new int[]{y, y - size/4, y - size/2, y};
                break;
        }

        g2d.fillPolygon(xPoints, yPoints, 4);
    }

    private void drawPatternDecoration(Graphics2D g2d, int width, int height) {
        // 绘制传统花纹装饰
        g2d.setColor(new Color(218, 165, 32, 100)); // 半透明金色

        // 在边框周围绘制小花朵
        for (int i = 0; i < 20; i++) {
            int x, y;

            // 上边框
            if (i < 5) {
                x = 80 + i * (width - 160) / 4;
                y = 40;
                drawSmallFlower(g2d, x, y, 8);
            }
            // 下边框
            else if (i < 10) {
                x = 80 + (i-5) * (width - 160) / 4;
                y = height - 40;
                drawSmallFlower(g2d, x, y, 8);
            }
            // 左边框
            else if (i < 15) {
                x = 40;
                y = 80 + (i-10) * (height - 160) / 4;
                drawSmallFlower(g2d, x, y, 8);
            }
            // 右边框
            else {
                x = width - 40;
                y = 80 + (i-15) * (height - 160) / 4;
                drawSmallFlower(g2d, x, y, 8);
            }
        }
    }

    private void drawSmallFlower(Graphics2D g2d, int x, int y, int size) {
        // 绘制小花朵
        g2d.setColor(new Color(255, 192, 203)); // 粉红色
        for (int i = 0; i < 5; i++) {
            double angle = i * 2 * Math.PI / 5;
            int petalX = (int)(x + size * Math.cos(angle));
            int petalY = (int)(y + size * Math.sin(angle));
            g2d.fillOval(petalX - size/3, petalY - size/3, 2*size/3, 2*size/3);
        }
        g2d.setColor(new Color(255, 215, 0)); // 黄色花心
        g2d.fillOval(x - size/4, y - size/4, size/2, size/2);
    }

    private void drawDottedDecoration(Graphics2D g2d, int width, int height) {
        // 绘制点线装饰
        g2d.setColor(new Color(169, 169, 169)); // 灰色

        float[] dashPattern = {2.0f, 3.0f};
        BasicStroke dottedStroke = new BasicStroke(1, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND,
                1.0f, dashPattern, 0.0f);
        g2d.setStroke(dottedStroke);

        g2d.drawRect(60, 60, width - 120, height - 120);
    }

    private void drawSeal(Graphics2D g2d, int width, int height, int style) {
        // 绘制传统印章
        int sealX = width - 180;
        int sealY = height - 180;
        int sealSize = 100;

        // 印章颜色
        Color sealColor = new Color(178, 34, 34); // 红色

        // 绘制圆形印章外框
        g2d.setColor(sealColor);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawOval(sealX, sealY, sealSize, sealSize);

        // 绘制五角星
        int starSize = 20;
        int centerX = sealX + sealSize/2;
        int centerY = sealY + sealSize/2;
        drawStar(g2d, centerX, centerY, starSize, sealColor);

        // 绘制印章文字
        g2d.setFont(new Font("宋体", Font.BOLD, 14));
        FontMetrics fm = g2d.getFontMetrics();

        // 印章上部分文字
        String topText = "家庭荣誉";
        int topTextWidth = fm.stringWidth(topText);
        g2d.drawString(topText, centerX - topTextWidth/2, centerY - 15);

        // 印章下部分文字
        String bottomText = "之章";
        int bottomTextWidth = fm.stringWidth(bottomText);
        g2d.drawString(bottomText, centerX - bottomTextWidth/2, centerY + 25);

        // 添加印章纹理（模拟印章的不规则边缘）
        g2d.setStroke(new BasicStroke(1));
        for (int i = 0; i < 20; i++) {
            double angle = Math.random() * 2 * Math.PI;
            double radius = sealSize/2 + Math.random() * 5 - 2;
            int x = (int)(centerX + radius * Math.cos(angle));
            int y = (int)(centerY + radius * Math.sin(angle));
            g2d.fillOval(x - 1, y - 1, 2, 2);
        }
    }

    private void drawStar(Graphics2D g2d, int x, int y, int size, Color color) {
        g2d.setColor(color);
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

    private void drawCenteredString(Graphics2D g2d, String text, int width, int y) {
        FontMetrics fm = g2d.getFontMetrics();
        int x = (width - fm.stringWidth(text)) / 2;
        g2d.drawString(text, x, y);
    }

    private void drawRightAlignedString(Graphics2D g2d, String text, int x, int y) {
        g2d.drawString(text, x, y);
    }

    private void drawLeftAlignedString(Graphics2D g2d, String text, int x, int y) {
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

    private void saveAsImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("保存奖状为图片");
        fileChooser.setSelectedFile(new File("家庭荣誉奖状.png"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".png")) {
                fileToSave = new File(filePath + ".png");
            }

            try {
                // 创建更高分辨率的图像用于保存
                int saveWidth = 1600;
                int saveHeight = 1200;
                BufferedImage saveImage = new BufferedImage(saveWidth, saveHeight, BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = saveImage.createGraphics();

                // 设置高质量渲染
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                // 重新绘制奖状到高分辨率图像
                // 这里需要重新绘制所有内容，但为了简化，我们使用缩放
                g2d.drawImage(currentTemplate, 0, 0, saveWidth, saveHeight, null);
                g2d.dispose();

                // 保存图像
                ImageIO.write(saveImage, "PNG", fileToSave);

                JOptionPane.showMessageDialog(this,
                        "奖状已成功保存为图片！\n文件: " + fileToSave.getName(),
                        "保存成功", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "保存图片时出错: " + ex.getMessage(),
                        "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveAsPDF() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("保存奖状为PDF");
        fileChooser.setSelectedFile(new File("家庭荣誉奖状.pdf"));

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
                PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(fileToSave));
                document.open();

                // 添加奖状标题
                com.itextpdf.text.Font titleFont = FontFactory.getFont("STSong-Light", "UniGB-UCS2-H", 32);
                titleFont.setColor(new BaseColor(178, 34, 34)); // 深红色
                Paragraph title = new Paragraph(titleField.getText(), titleFont);
                title.setAlignment(Element.ALIGN_CENTER);
                document.add(title);

                document.add(Chunk.NEWLINE);

                // 添加授予信息
                com.itextpdf.text.Font grantFont = FontFactory.getFont("STSong-Light", "UniGB-UCS2-H", 24);
                grantFont.setColor(new BaseColor(0, 0, 139)); // 深蓝色
                Paragraph grant = new Paragraph("授予 " + recipientField.getText() + " 同志", grantFont);
                grant.setAlignment(Element.ALIGN_CENTER);
                document.add(grant);

                document.add(Chunk.NEWLINE);

                // 添加获奖理由
                com.itextpdf.text.Font reasonFont = FontFactory.getFont("STSong-Light", "UniGB-UCS2-H", 18);
                reasonFont.setColor(new BaseColor(47, 79, 79));
                Paragraph reason = new Paragraph("在" + reasonArea.getText() + "方面，", reasonFont);
                reason.setAlignment(Element.ALIGN_CENTER);
                document.add(reason);

                document.add(Chunk.NEWLINE);

                // 添加奖状正文
                com.itextpdf.text.Font contentFont = FontFactory.getFont("STSong-Light", "UniGB-UCS2-H", 20);
                contentFont.setColor(new BaseColor(47, 79, 79));
                Paragraph content = new Paragraph(contentArea.getText(), contentFont);
                content.setAlignment(Element.ALIGN_CENTER);
                document.add(content);

                document.add(Chunk.NEWLINE);
                document.add(Chunk.NEWLINE);
                document.add(Chunk.NEWLINE);
                document.add(Chunk.NEWLINE);

                // 添加颁发机构和日期
                com.itextpdf.text.Font issuerFont = FontFactory.getFont("STSong-Light", "UniGB-UCS2-H", 22);
                issuerFont.setColor(new BaseColor(0, 100, 0));

                Paragraph issuer = new Paragraph(issuerField.getText(), issuerFont);
                issuer.setAlignment(Element.ALIGN_RIGHT);
                document.add(issuer);

                Date date = (Date) dateSpinner.getValue();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
                Paragraph dateParagraph = new Paragraph(sdf.format(date), issuerFont);
                dateParagraph.setAlignment(Element.ALIGN_RIGHT);
                document.add(dateParagraph);

                document.close();

                JOptionPane.showMessageDialog(this,
                        "奖状已成功保存为PDF！\n文件: " + fileToSave.getName(),
                        "保存成功", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "保存PDF时出错: " + ex.getMessage(),
                        "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void printCertificate() {
        int response = JOptionPane.showConfirmDialog(this,
                "即将打印奖状，请确保打印机已连接并准备就绪。\n是否继续？",
                "打印确认",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (response == JOptionPane.YES_OPTION) {
            // 这里可以添加实际的打印代码
            // 暂时显示成功消息
            JOptionPane.showMessageDialog(this,
                    "打印任务已发送到打印机。\n请检查打印机输出。",
                    "打印成功",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void main(String[] args) {
        // 设置外观
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 设置中文字体
        setUIFont(new javax.swing.plaf.FontUIResource("宋体", Font.PLAIN, 13));

        // 启动程序
        SwingUtilities.invokeLater(() -> {
            FamilyCertificateGenerator app = new FamilyCertificateGenerator();
            app.setVisible(true);
        });
    }

    // 设置全局字体
    private static void setUIFont(javax.swing.plaf.FontUIResource f) {
        java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource) {
                UIManager.put(key, f);
            }
        }
    }
}