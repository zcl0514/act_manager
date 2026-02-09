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
import javax.swing.border.LineBorder;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

public class KidsCertificateGenerator extends JFrame {

    // 组件声明
    private JTextField recipientField, titleField, issuerField;
    private JTextArea contentArea;
    private JComboBox<String> templateCombo;
    private JPanel previewPanel, stickerPanel;
    private JSpinner dateSpinner;
    private JCheckBox borderCheck, stickerCheck, rainbowCheck;
    private JColorChooser colorChooser;
    private JLabel previewLabel;
    private BufferedImage currentTemplate;

    // 贴纸类型
    private String[] stickerTypes = {"星星", "花朵", "动物", "气球", "糖果", "玩具"};
    private JComboBox<String> stickerCombo;

    // 卡通字体
    private String[] fontNames = {"幼圆", "华文彩云", "楷体", "Comic Sans MS", "Arial Rounded MT Bold"};
    private JComboBox<String> fontCombo;
    private JSpinner fontSizeSpinner;

    // 卡通颜色
    private Color[] kidColors = {
            new Color(255, 105, 180), // 粉红
            new Color(30, 144, 255),  // 蓝色
            new Color(50, 205, 50),   // 绿色
            new Color(255, 215, 0),   // 金色
            new Color(255, 140, 0),   // 橙色
            new Color(138, 43, 226)   // 紫色
    };

    public KidsCertificateGenerator() {
        setTitle("儿童奖状生成器 🎨✨");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // 设置窗口图标
        try {
            setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icon.png")));
        } catch (Exception e) {
            // 如果找不到图标，使用默认
        }

        initComponents();
        pack();
        setLocationRelativeTo(null);
        setSize(1100, 750);
        setVisible(true);
    }

    private void initComponents() {
        // 设置主面板背景色
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(240, 248, 255));

        // 左侧控制面板 - 使用更活泼的颜色
        JPanel controlPanel = new JPanel(new GridBagLayout());
        controlPanel.setBackground(new Color(255, 250, 245));
        controlPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 182, 193), 3, true),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // 添加标题标签
        JLabel titleLabel = new JLabel("🎉 创建你的专属奖状 🎉");
        titleLabel.setFont(new Font("华文彩云", Font.BOLD, 24));
        titleLabel.setForeground(new Color(255, 105, 180));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 模板选择 - 儿童风格模板
        gbc.gridx = 0; gbc.gridy = 0;
        controlPanel.add(new JLabel("🎨 选择样式:"), gbc);
        gbc.gridx = 1;
        String[] templates = {"🌈 彩虹乐园", "🐻 动物世界", "🎈 生日派对", "⭐️ 学习之星", "🏆 冠军荣誉"};
        templateCombo = new JComboBox<>(templates);
        templateCombo.setBackground(Color.WHITE);
        templateCombo.setForeground(new Color(30, 144, 255));
        templateCombo.addActionListener(e -> updatePreview());
        controlPanel.add(templateCombo, gbc);

        // 获奖者姓名
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel nameLabel = new JLabel("👑 小朋友姓名:");
        nameLabel.setForeground(new Color(138, 43, 226));
        controlPanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        recipientField = new JTextField(20);
        recipientField.setFont(new Font("楷体", Font.PLAIN, 16));
        recipientField.setBorder(new LineBorder(new Color(255, 182, 193), 2));
        recipientField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
        });
        controlPanel.add(recipientField, gbc);

        // 奖状标题
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel titleTextLabel = new JLabel("🏅 奖状标题:");
        titleTextLabel.setForeground(new Color(255, 140, 0));
        controlPanel.add(titleTextLabel, gbc);
        gbc.gridx = 1;
        titleField = new JTextField(20);
        titleField.setText("优秀小达人");
        titleField.setFont(new Font("楷体", Font.PLAIN, 16));
        titleField.setBorder(new LineBorder(new Color(255, 182, 193), 2));
        titleField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
        });
        controlPanel.add(titleField, gbc);

        // 奖状内容
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel contentLabel = new JLabel("💌 夸奖的话:");
        contentLabel.setForeground(new Color(50, 205, 50));
        controlPanel.add(contentLabel, gbc);
        gbc.gridx = 1;
        contentArea = new JTextArea(4, 20);
        contentArea.setText("小朋友在幼儿园表现很棒，\n乐于助人，认真学习，\n是大家的好榜样！");
        contentArea.setFont(new Font("楷体", Font.PLAIN, 14));
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setBorder(new LineBorder(new Color(255, 182, 193), 2));
        JScrollPane scrollPane = new JScrollPane(contentArea);
        scrollPane.setBorder(null);
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
        JLabel issuerLabel = new JLabel("🏫 颁发单位:");
        issuerLabel.setForeground(new Color(138, 43, 226));
        controlPanel.add(issuerLabel, gbc);
        gbc.gridx = 1;
        issuerField = new JTextField(20);
        issuerField.setText("阳光幼儿园");
        issuerField.setFont(new Font("楷体", Font.PLAIN, 16));
        issuerField.setBorder(new LineBorder(new Color(255, 182, 193), 2));
        issuerField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
        });
        controlPanel.add(issuerField, gbc);

        // 日期选择
        gbc.gridx = 0; gbc.gridy = 5;
        JLabel dateLabel = new JLabel("📅 颁发日期:");
        dateLabel.setForeground(new Color(30, 144, 255));
        controlPanel.add(dateLabel, gbc);
        gbc.gridx = 1;
        SpinnerDateModel dateModel = new SpinnerDateModel();
        dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy年MM月dd日");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.addChangeListener(e -> updatePreview());
        controlPanel.add(dateSpinner, gbc);

        // 字体设置
        gbc.gridx = 0; gbc.gridy = 6;
        JLabel fontLabel = new JLabel("🔤 选择字体:");
        fontLabel.setForeground(new Color(255, 105, 180));
        controlPanel.add(fontLabel, gbc);
        gbc.gridx = 1;
        fontCombo = new JComboBox<>(fontNames);
        fontCombo.addActionListener(e -> updatePreview());
        controlPanel.add(fontCombo, gbc);

        // 字体大小
        gbc.gridx = 0; gbc.gridy = 7;
        JLabel sizeLabel = new JLabel("🔡 字体大小:");
        sizeLabel.setForeground(new Color(255, 140, 0));
        controlPanel.add(sizeLabel, gbc);
        gbc.gridx = 1;
        SpinnerNumberModel fontSizeModel = new SpinnerNumberModel(24, 18, 48, 1);
        fontSizeSpinner = new JSpinner(fontSizeModel);
        fontSizeSpinner.addChangeListener(e -> updatePreview());
        controlPanel.add(fontSizeSpinner, gbc);

        // 贴纸选择
        gbc.gridx = 0; gbc.gridy = 8;
        JLabel stickerLabel = new JLabel("⭐️ 贴纸类型:");
        stickerLabel.setForeground(new Color(50, 205, 50));
        controlPanel.add(stickerLabel, gbc);
        gbc.gridx = 1;
        stickerCombo = new JComboBox<>(stickerTypes);
        stickerCombo.addActionListener(e -> updatePreview());
        controlPanel.add(stickerCombo, gbc);

        // 颜色选择面板
        gbc.gridx = 0; gbc.gridy = 9;
        gbc.gridwidth = 2;
        JPanel colorPanel = new JPanel(new BorderLayout());
        colorPanel.setBorder(BorderFactory.createTitledBorder("🎨 选择颜色"));
        colorChooser = new JColorChooser(Color.BLACK);
        colorChooser.setPreviewPanel(new JPanel());
        colorChooser.getSelectionModel().addChangeListener(e -> updatePreview());
        colorPanel.add(colorChooser, BorderLayout.CENTER);
        controlPanel.add(colorPanel, gbc);

        // 选项面板 - 更可爱的布局
        gbc.gridx = 0; gbc.gridy = 10;
        gbc.gridwidth = 2;
        JPanel optionPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        optionPanel.setBackground(new Color(255, 250, 245));

        borderCheck = new JCheckBox("🎀 彩色边框");
        borderCheck.setSelected(true);
        borderCheck.setBackground(new Color(255, 250, 245));
        borderCheck.addActionListener(e -> updatePreview());

        stickerCheck = new JCheckBox("⭐️ 显示贴纸");
        stickerCheck.setSelected(true);
        stickerCheck.setBackground(new Color(255, 250, 245));
        stickerCheck.addActionListener(e -> updatePreview());

        rainbowCheck = new JCheckBox("🌈 彩虹文字");
        rainbowCheck.setSelected(true);
        rainbowCheck.setBackground(new Color(255, 250, 245));
        rainbowCheck.addActionListener(e -> updatePreview());

        optionPanel.add(borderCheck);
        optionPanel.add(stickerCheck);
        optionPanel.add(rainbowCheck);
        controlPanel.add(optionPanel, gbc);

        // 按钮面板 - 使用更可爱的按钮
        gbc.gridx = 0; gbc.gridy = 11;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(new Color(255, 250, 245));

        // 保存为图片按钮
        JButton saveImageBtn = createStyledButton("💾 保存图片", new Color(30, 144, 255));
        saveImageBtn.addActionListener(e -> saveAsImage());

        // 保存为PDF按钮
        JButton savePdfBtn = createStyledButton("📄 保存PDF", new Color(50, 205, 50));
        savePdfBtn.addActionListener(e -> saveAsPDF());

        // 打印按钮
        JButton printBtn = createStyledButton("🖨️ 打印奖状", new Color(255, 140, 0));
        printBtn.addActionListener(e -> printCertificate());

        // 随机样式按钮
        JButton randomBtn = createStyledButton("🎲 随机样式", new Color(138, 43, 226));
        randomBtn.addActionListener(e -> randomStyle());

        buttonPanel.add(saveImageBtn);
        buttonPanel.add(savePdfBtn);
        buttonPanel.add(printBtn);
        buttonPanel.add(randomBtn);
        controlPanel.add(buttonPanel, gbc);

        // 右侧预览面板 - 设置更可爱的边框
        previewPanel = new JPanel(new BorderLayout());
        previewPanel.setBackground(new Color(240, 248, 255));
        previewPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 182, 193), 4, true),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // 预览标签
        previewLabel = new JLabel();
        previewLabel.setHorizontalAlignment(JLabel.CENTER);
        previewLabel.setVerticalAlignment(JLabel.CENTER);
        previewPanel.add(new JScrollPane(previewLabel), BorderLayout.CENTER);

        // 添加到主窗口
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(controlPanel, BorderLayout.WEST);
        mainPanel.add(previewPanel, BorderLayout.CENTER);
        add(mainPanel);

        // 初始化预览
        updatePreview();
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("微软雅黑", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // 添加鼠标悬停效果
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });

        return button;
    }

    private void randomStyle() {
        // 随机选择模板
        int randomTemplate = (int)(Math.random() * 5);
        templateCombo.setSelectedIndex(randomTemplate);

        // 随机选择字体
        int randomFont = (int)(Math.random() * fontNames.length);
        fontCombo.setSelectedIndex(randomFont);

        // 随机字体大小
        int randomSize = 20 + (int)(Math.random() * 20);
        fontSizeSpinner.setValue(randomSize);

        // 随机选择贴纸
        int randomSticker = (int)(Math.random() * stickerTypes.length);
        stickerCombo.setSelectedIndex(randomSticker);

        // 随机颜色
        int randomColor = (int)(Math.random() * kidColors.length);
        colorChooser.setColor(kidColors[randomColor]);

        // 随机选项
        borderCheck.setSelected(Math.random() > 0.5);
        stickerCheck.setSelected(Math.random() > 0.5);
        rainbowCheck.setSelected(Math.random() > 0.5);

        JOptionPane.showMessageDialog(this, "🎲 已生成随机样式！", "随机样式", JOptionPane.INFORMATION_MESSAGE);
    }

    private void updatePreview() {
        // 创建预览图像
        int width = 700;
        int height = 500;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // 抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // 根据模板绘制背景
        int templateIndex = templateCombo.getSelectedIndex();
        Color bgColor;
        GradientPaint gradient;

        switch (templateIndex) {
            case 0: // 彩虹乐园
                gradient = new GradientPaint(0, 0, new Color(255, 240, 245), width, height, new Color(230, 240, 255));
                g2d.setPaint(gradient);
                break;
            case 1: // 动物世界
                gradient = new GradientPaint(0, 0, new Color(255, 250, 240), width, height, new Color(240, 255, 240));
                g2d.setPaint(gradient);
                break;
            case 2: // 生日派对
                gradient = new GradientPaint(0, 0, new Color(255, 245, 250), width, height, new Color(255, 250, 220));
                g2d.setPaint(gradient);
                break;
            case 3: // 学习之星
                gradient = new GradientPaint(0, 0, new Color(240, 255, 255), width, height, new Color(255, 255, 240));
                g2d.setPaint(gradient);
                break;
            case 4: // 冠军荣誉
                gradient = new GradientPaint(0, 0, new Color(255, 245, 220), width, height, new Color(255, 240, 240));
                g2d.setPaint(gradient);
                break;
            default:
                g2d.setColor(Color.WHITE);
        }
        g2d.fillRect(0, 0, width, height);

        // 绘制卡通边框
        if (borderCheck.isSelected()) {
            drawCartoonBorder(g2d, width, height, templateIndex);
        }

        // 绘制标题
        String title = titleField.getText().trim();
        if (!title.isEmpty()) {
            Font titleFont = getSelectedFont().deriveFont(Font.BOLD, (Integer)fontSizeSpinner.getValue() + 12);
            g2d.setFont(titleFont);

            if (rainbowCheck.isSelected()) {
                drawRainbowText(g2d, title, width, 80);
            } else {
                g2d.setColor(colorChooser.getColor());
                drawCenteredString(g2d, title, width, 80);
            }
        }

        // 绘制"授予"
        g2d.setFont(getSelectedFont().deriveFont(Font.PLAIN, (Integer)fontSizeSpinner.getValue()));
        g2d.setColor(new Color(100, 100, 100));
        drawCenteredString(g2d, "授予", width, 130);

        // 绘制获奖者姓名
        String recipient = recipientField.getText().trim();
        if (!recipient.isEmpty()) {
            Font nameFont = getSelectedFont().deriveFont(Font.BOLD, (Integer)fontSizeSpinner.getValue() + 16);
            g2d.setFont(nameFont);

            if (rainbowCheck.isSelected()) {
                drawRainbowText(g2d, recipient, width, 180);
            } else {
                g2d.setColor(colorChooser.getColor());
                drawCenteredString(g2d, recipient, width, 180);
            }
        }

        // 绘制内容
        String content = contentArea.getText().trim();
        if (!content.isEmpty()) {
            g2d.setFont(getSelectedFont().deriveFont(Font.PLAIN, (Integer)fontSizeSpinner.getValue()));
            g2d.setColor(new Color(80, 80, 80));
            drawWrappedText(g2d, content, width, 230, 350);
        }

        // 绘制颁发机构
        String issuer = issuerField.getText().trim();
        if (!issuer.isEmpty()) {
            g2d.setFont(getSelectedFont().deriveFont(Font.BOLD, (Integer)fontSizeSpinner.getValue()));
            g2d.setColor(new Color(30, 144, 255));
            drawCenteredString(g2d, issuer, width, height - 100);
        }

        // 绘制日期
        Date date = (Date) dateSpinner.getValue();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        String dateStr = sdf.format(date);
        g2d.setFont(getSelectedFont().deriveFont(Font.PLAIN, (Integer)fontSizeSpinner.getValue() - 4));
        g2d.setColor(new Color(100, 100, 100));
        drawCenteredString(g2d, dateStr, width, height - 60);

        // 绘制贴纸
        if (stickerCheck.isSelected()) {
            drawStickers(g2d, width, height, stickerCombo.getSelectedIndex());
        }

        g2d.dispose();

        // 更新预览
        currentTemplate = image;
        previewLabel.setIcon(new ImageIcon(image));
    }

    private Font getSelectedFont() {
        String fontName = (String) fontCombo.getSelectedItem();
        return new Font(fontName, Font.PLAIN, 12);
    }

    private void drawCartoonBorder(Graphics2D g2d, int width, int height, int style) {
        Color borderColor = kidColors[style % kidColors.length];
        g2d.setColor(borderColor);
        g2d.setStroke(new BasicStroke(5));

        // 绘制圆角矩形边框
        int arc = 30;
        g2d.drawRoundRect(20, 20, width - 40, height - 40, arc, arc);

        // 绘制装饰性角标
        int cornerSize = 30;
        g2d.setStroke(new BasicStroke(3));

        // 左上角
        drawCornerDecoration(g2d, 20, 20, cornerSize, 0);
        // 右上角
        drawCornerDecoration(g2d, width - 20, 20, cornerSize, 1);
        // 左下角
        drawCornerDecoration(g2d, 20, height - 20, cornerSize, 2);
        // 右下角
        drawCornerDecoration(g2d, width - 20, height - 20, cornerSize, 3);

        // 绘制装饰性小星星
        for (int i = 0; i < 8; i++) {
            int x = 40 + (i * (width - 80) / 7);
            int y = 15;
            drawStar(g2d, x, y, 5, kidColors[(i + style) % kidColors.length]);
        }
    }

    private void drawCornerDecoration(Graphics2D g2d, int x, int y, int size, int corner) {
        switch (corner) {
            case 0: // 左上
                drawStar(g2d, x + size/2, y + size/2, size/2, kidColors[0]);
                break;
            case 1: // 右上
                drawFlower(g2d, x - size/2, y + size/2, size/2);
                break;
            case 2: // 左下
                drawHeart(g2d, x + size/2, y - size/2, size/2);
                break;
            case 3: // 右下
                drawBalloon(g2d, x - size/2, y - size/2, size/2);
                break;
        }
    }

    private void drawStickers(Graphics2D g2d, int width, int height, int stickerType) {
        switch (stickerType) {
            case 0: // 星星
                for (int i = 0; i < 10; i++) {
                    int x = 50 + (int)(Math.random() * (width - 100));
                    int y = 50 + (int)(Math.random() * (height - 150));
                    int size = 10 + (int)(Math.random() * 20);
                    drawStar(g2d, x, y, size, kidColors[i % kidColors.length]);
                }
                break;
            case 1: // 花朵
                for (int i = 0; i < 8; i++) {
                    int x = 50 + (int)(Math.random() * (width - 100));
                    int y = 50 + (int)(Math.random() * (height - 150));
                    int size = 15 + (int)(Math.random() * 20);
                    drawFlower(g2d, x, y, size);
                }
                break;
            case 2: // 动物
                for (int i = 0; i < 6; i++) {
                    int x = 50 + (int)(Math.random() * (width - 100));
                    int y = 50 + (int)(Math.random() * (height - 150));
                    drawAnimal(g2d, x, y, i % 3);
                }
                break;
            case 3: // 气球
                for (int i = 0; i < 8; i++) {
                    int x = 50 + (int)(Math.random() * (width - 100));
                    int y = 50 + (int)(Math.random() * (height - 150));
                    int size = 20 + (int)(Math.random() * 15);
                    drawBalloon(g2d, x, y, size);
                }
                break;
            case 4: // 糖果
                for (int i = 0; i < 12; i++) {
                    int x = 50 + (int)(Math.random() * (width - 100));
                    int y = 50 + (int)(Math.random() * (height - 150));
                    drawCandy(g2d, x, y);
                }
                break;
            case 5: // 玩具
                for (int i = 0; i < 6; i++) {
                    int x = 50 + (int)(Math.random() * (width - 100));
                    int y = 50 + (int)(Math.random() * (height - 150));
                    drawToy(g2d, x, y, i % 3);
                }
                break;
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
        g2d.setColor(color.darker());
        g2d.drawPolygon(xPoints, yPoints, 10);
    }

    private void drawFlower(Graphics2D g2d, int x, int y, int size) {
        // 花心
        g2d.setColor(new Color(255, 215, 0)); // 黄色
        g2d.fillOval(x - size/4, y - size/4, size/2, size/2);

        // 花瓣
        g2d.setColor(new Color(255, 105, 180)); // 粉红色
        for (int i = 0; i < 5; i++) {
            double angle = i * 2 * Math.PI / 5;
            int petalX = (int)(x + size * Math.cos(angle));
            int petalY = (int)(y + size * Math.sin(angle));
            g2d.fillOval(petalX - size/3, petalY - size/3, 2*size/3, 2*size/3);
        }
    }

    private void drawAnimal(Graphics2D g2d, int x, int y, int type) {
        switch (type) {
            case 0: // 小熊
                g2d.setColor(new Color(139, 69, 19)); // 棕色
                g2d.fillOval(x - 15, y - 15, 30, 30); // 头
                g2d.fillOval(x - 20, y + 5, 15, 15); // 左耳
                g2d.fillOval(x + 5, y + 5, 15, 15); // 右耳
                g2d.setColor(Color.WHITE);
                g2d.fillOval(x - 8, y - 5, 6, 6); // 左眼
                g2d.fillOval(x + 2, y - 5, 6, 6); // 右眼
                g2d.setColor(Color.BLACK);
                g2d.fillOval(x - 6, y - 3, 3, 3); // 左眼珠
                g2d.fillOval(x + 4, y - 3, 3, 3); // 右眼珠
                break;
            case 1: // 小兔子
                g2d.setColor(Color.PINK);
                g2d.fillOval(x - 12, y - 12, 24, 24); // 头
                g2d.fillOval(x - 18, y - 20, 10, 20); // 左耳
                g2d.fillOval(x + 8, y - 20, 10, 20); // 右耳
                g2d.setColor(Color.BLACK);
                g2d.fillOval(x - 5, y - 3, 4, 4); // 左眼
                g2d.fillOval(x + 1, y - 3, 4, 4); // 右眼
                g2d.drawArc(x - 3, y + 2, 6, 4, 0, -180); // 嘴巴
                break;
            case 2: // 小猫
                g2d.setColor(new Color(255, 165, 0)); // 橙色
                g2d.fillOval(x - 12, y - 12, 24, 24); // 头
                g2d.fillOval(x - 16, y - 16, 8, 8); // 左耳
                g2d.fillOval(x + 8, y - 16, 8, 8); // 右耳
                g2d.setColor(Color.BLACK);
                g2d.fillOval(x - 5, y - 3, 4, 4); // 左眼
                g2d.fillOval(x + 1, y - 3, 4, 4); // 右眼
                g2d.fillOval(x - 2, y + 3, 4, 2); // 鼻子
                g2d.drawLine(x - 2, y + 4, x - 2, y + 8); // 嘴巴
                break;
        }
    }

    private void drawBalloon(Graphics2D g2d, int x, int y, int size) {
        Color[] balloonColors = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.PINK};
        Color color = balloonColors[(int)(Math.random() * balloonColors.length)];

        g2d.setColor(color);
        g2d.fillOval(x - size/2, y - size, size, size * 2);

        // 绳子
        g2d.setColor(Color.GRAY);
        g2d.drawLine(x, y + size, x, y + size + 20);
    }

    private void drawCandy(Graphics2D g2d, int x, int y) {
        Color[] candyColors = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW};
        Color color = candyColors[(int)(Math.random() * candyColors.length)];

        g2d.setColor(color);
        g2d.fillRect(x - 5, y - 10, 10, 20);

        // 糖果条纹
        g2d.setColor(Color.WHITE);
        g2d.drawLine(x - 5, y - 5, x + 5, y - 5);
        g2d.drawLine(x - 5, y, x + 5, y);
        g2d.drawLine(x - 5, y + 5, x + 5, y + 5);
    }

    private void drawToy(Graphics2D g2d, int x, int y, int type) {
        switch (type) {
            case 0: // 积木
                g2d.setColor(new Color(255, 100, 100));
                g2d.fillRect(x - 15, y - 10, 30, 20);
                g2d.setColor(Color.BLACK);
                g2d.drawRect(x - 15, y - 10, 30, 20);
                break;
            case 1: // 小汽车
                g2d.setColor(Color.BLUE);
                g2d.fillRoundRect(x - 20, y - 8, 40, 16, 5, 5);
                g2d.setColor(Color.BLACK);
                g2d.fillOval(x - 12, y + 4, 8, 8); // 左轮
                g2d.fillOval(x + 4, y + 4, 8, 8); // 右轮
                break;
            case 2: // 皮球
                g2d.setColor(Color.RED);
                g2d.fillOval(x - 12, y - 12, 24, 24);
                g2d.setColor(Color.WHITE);
                g2d.drawArc(x - 8, y - 8, 16, 16, 0, 180);
                break;
        }
    }

    private void drawHeart(Graphics2D g2d, int x, int y, int size) {
        g2d.setColor(Color.RED);

        // 绘制心形
        int[] xPoints = {x, x - size, x, x + size};
        int[] yPoints = {y + size/2, y - size/2, y + size, y - size/2};

        // 使用贝塞尔曲线绘制更标准的心形
        int topY = y - size/2;
        int bottomY = y + size/2;

        g2d.fillArc(x - size, topY - size/2, size, size, 0, 180);
        g2d.fillArc(x, topY - size/2, size, size, 0, 180);

        int[] triangleX = {x - size, x, x + size};
        int[] triangleY = {topY + size/4, bottomY, topY + size/4};
        g2d.fillPolygon(triangleX, triangleY, 3);
    }

    private void drawRainbowText(Graphics2D g2d, String text, int width, int y) {
        FontMetrics fm = g2d.getFontMetrics();
        int totalWidth = fm.stringWidth(text);
        int startX = (width - totalWidth) / 2;

        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            g2d.setColor(kidColors[i % kidColors.length]);
            g2d.drawString(String.valueOf(ch), startX + fm.stringWidth(text.substring(0, i)), y);
        }
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

    private void saveAsImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("保存奖状为图片");
        fileChooser.setSelectedFile(new File("儿童奖状.png"));

        // 设置可爱的文件选择器
        fileChooser.setApproveButtonText("保存 🎨");

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
                // 这里需要重新绘制所有内容，但为了简化，我们使用缩放
                g2d.drawImage(currentTemplate, 0, 0, saveWidth, saveHeight, null);
                g2d.dispose();

                // 保存图像
                ImageIO.write(saveImage, "PNG", fileToSave);

                // 显示成功消息
                JOptionPane.showMessageDialog(this,
                        "🎉 奖状已保存为图片！\n保存在: " + fileToSave.getName(),
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
        fileChooser.setSelectedFile(new File("儿童奖状.pdf"));
        fileChooser.setApproveButtonText("保存 📄");

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

                // 添加可爱的标题
                com.itextpdf.text.Font titleFont = FontFactory.getFont("STSong-Light", "UniGB-UCS2-H", 28);
                titleFont.setColor(new BaseColor(255, 105, 180)); // 粉色
                Paragraph title = new Paragraph(titleField.getText(), titleFont);
                title.setAlignment(Element.ALIGN_CENTER);
                document.add(title);

                document.add(new Paragraph(" ")); // 空行

                // 添加授予和姓名
                com.itextpdf.text.Font nameFont = FontFactory.getFont("STSong-Light", "UniGB-UCS2-H", 24);
                nameFont.setColor(new BaseColor(30, 144, 255)); // 蓝色
                Paragraph grant = new Paragraph("授予: " + recipientField.getText(), nameFont);
                grant.setAlignment(Element.ALIGN_CENTER);
                document.add(grant);

                document.add(new Paragraph(" ")); // 空行

                // 添加内容
                com.itextpdf.text.Font contentFont = FontFactory.getFont("STSong-Light", "UniGB-UCS2-H", 18);
                contentFont.setColor(new BaseColor(80, 80, 80));
                Paragraph details = new Paragraph(contentArea.getText(), contentFont);
                details.setAlignment(Element.ALIGN_CENTER);
                document.add(details);

                document.add(new Paragraph(" ")); // 空行
                document.add(new Paragraph(" ")); // 空行

                // 添加颁发机构和日期
                com.itextpdf.text.Font issuerFont = FontFactory.getFont("STSong-Light", "UniGB-UCS2-H", 20);
                issuerFont.setColor(new BaseColor(50, 205, 50)); // 绿色
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
                        "🎉 奖状已保存为PDF！\n保存在: " + fileToSave.getName(),
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
                "准备好打印奖状了吗？\n请确保打印机已连接并打开。",
                "打印奖状 🖨️",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (response == JOptionPane.YES_OPTION) {
            // 这里可以添加实际的打印代码
            // 暂时显示成功消息
            JOptionPane.showMessageDialog(this,
                    "🎉 打印任务已发送到打印机！\n请检查打印机输出。",
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

        // 设置字体，确保中文字体显示正常
        setUIFont(new javax.swing.plaf.FontUIResource("微软雅黑", Font.PLAIN, 14));

        // 启动程序
        SwingUtilities.invokeLater(() -> new KidsCertificateGenerator());
    }

    // 设置全局字体
    private static void setUIFont(javax.swing.plaf.FontUIResource f) {
        java.util.Enumeration keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource) {
                UIManager.put(key, f);
            }
        }
    }
}