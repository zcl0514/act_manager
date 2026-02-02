package com.chenchen.act_manager.service;

import com.chenchen.act_manager.dtos.WordInfoDto;
import com.chenchen.act_manager.entity.QueryReq;
import com.chenchen.act_manager.entity.WordInfo;
import com.chenchen.act_manager.enums.ActManagerType;
import com.chenchen.act_manager.enums.OwnerType;
import com.chenchen.act_manager.mapper.WordInfoMapper;
import com.chenchen.act_manager.utils.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.TextAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTbl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblBorders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zouchanglin
 * @since 2026/1/20
 */
@Service
@Slf4j
public class WordService {

    @Autowired
    private WordInfoMapper wordInfoMapper;

    public List<WordInfo> getWordInfos(QueryReq req) {
        return wordInfoMapper.search(req);
    }

    public List<WordInfo> getAllWordInfos() {
        return wordInfoMapper.selectAll();
    }

    public WordInfo getWordInfoById(Integer id) {
        return wordInfoMapper.selectById(id);
    }

    public void deleteWordInfoById(Integer id) {
        wordInfoMapper.deleteById(id);
    }

    public void updateWordInfoById(Integer id, WordInfo wordInfo) {
        wordInfoMapper.updateById(id, wordInfo);
    }

    public Boolean save(WordInfo wordInfo) {
        return wordInfoMapper.insert(wordInfo) > 0;
    }

    public byte[] export(List<WordInfo> wordInfos) {
        List<WordInfoDto> dtos = CollectionUtil.toList(wordInfos, this::toDto);
        assert dtos != null;
        try {
            Map<String, List<WordInfoDto>> typeGroups = dtos.stream().collect(Collectors.groupingBy(WordInfoDto::getType));
            List<String> typeOrder = Arrays.asList("奖励", "扣除", "重大奖励", "重大扣除");

            // 3. 创建Word文档
            XWPFDocument document = new XWPFDocument();

            // 添加标题
            XWPFParagraph title = document.createParagraph();
            title.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleRun = title.createRun();
            titleRun.setText("宸宸家庭行为规范表");
            titleRun.setFontSize(16);
            titleRun.setBold(true);

            // 添加空行
            document.createParagraph();

            // 4. 按type顺序创建表格
            for (String type : typeOrder) {
                List<WordInfoDto> items = typeGroups.get(type);
                if (items == null || items.isEmpty()) continue;

                // 按owner分组
                Map<String, List<WordInfoDto>> ownerGroups = new HashMap<>();
                for (WordInfoDto item : items) {
                    String owner = item.getOwner();
                    ownerGroups.computeIfAbsent(owner, k -> new ArrayList<>()).add(item);
                }

                // 添加type标题
                XWPFParagraph typePara = document.createParagraph();
                XWPFRun typeRun = typePara.createRun();
                typeRun.setText(type + "项目");
                typeRun.setFontSize(14);
                typeRun.setBold(true);
                typeRun.setColor(type.contains("扣除") ? "FF0000" : "000000");

                // 创建表格
                int totalRows = 0;
                for (List<WordInfoDto> ownerItems : ownerGroups.values()) {
                    totalRows += ownerItems.size();
                }

                // 表头行 + 数据行
                XWPFTable table = document.createTable(totalRows + 1, 4);

                // 设置表格样式
                int tableWidth = 9000;
                table.setWidth(tableWidth);

                // 设置表头
                XWPFTableRow headerRow = table.getRow(0);
                for (int i = 0; i < 4; i++) {
                    XWPFTableCell cell = headerRow.getCell(i);
                    cell.removeParagraph(0); // 清除原有段落
                    XWPFParagraph paragraph = cell.addParagraph(); // 添加新段落
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    paragraph.setVerticalAlignment(TextAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText(""); // 初始为空
                }

                setTableHeaderCell(headerRow.getCell(0), "");
                setTableHeaderCell(headerRow.getCell(1), "规则");
                setTableHeaderCell(headerRow.getCell(2), "小红花");
                setTableHeaderCell(headerRow.getCell(3), "额外");
                headerRow.setHeight(400);

                // 填充数据
                int currentRow = 1;
                for (Map.Entry<String, List<WordInfoDto>> ownerEntry : ownerGroups.entrySet()) {
                    String owner = ownerEntry.getKey();
                    List<WordInfoDto> ownerItems = ownerEntry.getValue();

                    int startRow = currentRow;
                    int endRow = currentRow + ownerItems.size() - 1;

                    for (int i = 0; i < ownerItems.size(); i++) {
                        WordInfoDto item = ownerItems.get(i);
                        XWPFTableRow dataRow = table.getRow(currentRow);

                        // 如果是该owner的第一行，设置owner单元格
                        if (i == 0) {
                            setTableCell(dataRow.getCell(0), owner);
                        } else {
                            // 留空，后面会合并
                            setTableCell(dataRow.getCell(0), "");
                        }

                        setTableCell(dataRow.getCell(1), item.getRole());
                        setTableCell(dataRow.getCell(2),
                                item.getRedFlowerCount().toString());

                        if (StringUtils.isNotBlank(item.getExtra())) {
                            setTableCell(dataRow.getCell(3),
                                    item.getExtra());
                        } else {
                            setTableCell(dataRow.getCell(3), "");
                        }

                        currentRow++;
                    }

                    // 合并owner列的单元格
                    if (endRow > startRow) {
                        mergeCellsVertically(table, 0, startRow, endRow);
                    }
                }

                // 设置表格边框
                setTableBorders(table);

                //setTableColumnWidth(table, new int[]{1200, 6000, 800, 1000});
                setTableColumnWidth(table);

                // 添加空行
                document.createParagraph();
            }

            // 5. 保存文档
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            document.write(outputStream);
            document.close();
            return outputStream.toByteArray();

        } catch (Exception e) {
            log.error("文件导出失败", e);
            throw new RuntimeException("文件导出失败");
        }
    }

    public void generate(List<WordInfo> wordInfos) {
        List<WordInfoDto> dtos = CollectionUtil.toList(wordInfos, this::toDto);
        assert dtos != null;
        try {
            Map<String, List<WordInfoDto>> typeGroups = dtos.stream().collect(Collectors.groupingBy(WordInfoDto::getType));
            List<String> typeOrder = Arrays.asList("奖励", "扣除", "重大奖励", "重大扣除");

            // 3. 创建Word文档
            XWPFDocument document = new XWPFDocument();

            // 添加标题
            XWPFParagraph title = document.createParagraph();
            title.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleRun = title.createRun();
            titleRun.setText("宸宸家庭行为规范表");
            titleRun.setFontSize(16);
            titleRun.setBold(true);

            // 添加空行
            document.createParagraph();

            // 4. 按type顺序创建表格
            for (String type : typeOrder) {
                List<WordInfoDto> items = typeGroups.get(type);
                if (items == null || items.isEmpty()) continue;

                // 按owner分组
                Map<String, List<WordInfoDto>> ownerGroups = new HashMap<>();
                for (WordInfoDto item : items) {
                    String owner = item.getOwner();
                    ownerGroups.computeIfAbsent(owner, k -> new ArrayList<>()).add(item);
                }

                // 添加type标题
                XWPFParagraph typePara = document.createParagraph();
                XWPFRun typeRun = typePara.createRun();
                typeRun.setText(type + "项目");
                typeRun.setFontSize(14);
                typeRun.setBold(true);
                typeRun.setColor(type.contains("扣除") ? "FF0000" : "000000");

                // 创建表格
                int totalRows = 0;
                for (List<WordInfoDto> ownerItems : ownerGroups.values()) {
                    totalRows += ownerItems.size();
                }

                // 表头行 + 数据行
                XWPFTable table = document.createTable(totalRows + 1, 4);

                // 设置表格样式
                int tableWidth = 9000;
                table.setWidth(tableWidth);

                // 设置表头
                XWPFTableRow headerRow = table.getRow(0);
                for (int i = 0; i < 4; i++) {
                    XWPFTableCell cell = headerRow.getCell(i);
                    cell.removeParagraph(0); // 清除原有段落
                    XWPFParagraph paragraph = cell.addParagraph(); // 添加新段落
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    paragraph.setVerticalAlignment(TextAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText(""); // 初始为空
                }

                setTableHeaderCell(headerRow.getCell(0), "");
                setTableHeaderCell(headerRow.getCell(1), "规则");
                setTableHeaderCell(headerRow.getCell(2), "小红花");
                setTableHeaderCell(headerRow.getCell(3), "额外");
                headerRow.setHeight(400);

                // 填充数据
                int currentRow = 1;
                for (Map.Entry<String, List<WordInfoDto>> ownerEntry : ownerGroups.entrySet()) {
                    String owner = ownerEntry.getKey();
                    List<WordInfoDto> ownerItems = ownerEntry.getValue();

                    int startRow = currentRow;
                    int endRow = currentRow + ownerItems.size() - 1;

                    for (int i = 0; i < ownerItems.size(); i++) {
                        WordInfoDto item = ownerItems.get(i);
                        XWPFTableRow dataRow = table.getRow(currentRow);

                        // 如果是该owner的第一行，设置owner单元格
                        if (i == 0) {
                            setTableCell(dataRow.getCell(0), owner);
                        } else {
                            // 留空，后面会合并
                            setTableCell(dataRow.getCell(0), "");
                        }

                        setTableCell(dataRow.getCell(1), item.getRole());
                        setTableCell(dataRow.getCell(2),
                                item.getRedFlowerCount().toString());

                        if (StringUtils.isNotBlank(item.getExtra())) {
                            setTableCell(dataRow.getCell(3),
                                    item.getExtra());
                        } else {
                            setTableCell(dataRow.getCell(3), "");
                        }

                        currentRow++;
                    }

                    // 合并owner列的单元格
                    if (endRow > startRow) {
                        mergeCellsVertically(table, 0, startRow, endRow);
                    }
                }

                // 设置表格边框
                setTableBorders(table);

                //setTableColumnWidth(table, new int[]{1200, 6000, 800, 1000});
                setTableColumnWidth(table);

                // 添加空行
                document.createParagraph();
            }

            // 5. 保存文档
            FileOutputStream out = new FileOutputStream("行为规范表" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".docx");
            document.write(out);
            out.close();
        } catch (Exception e) {
            log.error("word生成失败!", e);
        }
    }

    // 设置每列宽度
    private static void setTableColumnWidth(XWPFTable table) {
        // 设置表格总宽度
        table.setWidth(9000);

        // 设置每列宽度（直接设置第一行的单元格宽度）
        List<XWPFTableRow> rows = table.getRows();
        if (!rows.isEmpty()) {
            XWPFTableRow firstRow = rows.get(0);

            // 假设表格有4列
            int[] columnWidths = {800, 6500, 800, 900};

            for (int i = 0; i < firstRow.getTableCells().size() && i < columnWidths.length; i++) {
                XWPFTableCell cell = firstRow.getCell(i);
                CTTc ctTc = cell.getCTTc();

                // 确保有TcPr（单元格属性）
                CTTcPr tcPr = ctTc.getTcPr();
                if (tcPr == null) {
                    tcPr = ctTc.addNewTcPr();
                }

                // 设置宽度
                CTTblWidth cellWidth = tcPr.addNewTcW();
                cellWidth.setW(BigInteger.valueOf(columnWidths[i]));
                cellWidth.setType(STTblWidth.DXA);
            }
        }
    }

    private WordInfoDto toDto(WordInfo wordInfo) {
        WordInfoDto wordInfoDto = new WordInfoDto();
        wordInfoDto.setRole(wordInfo.getRole());
        wordInfoDto.setRedFlowerCount(wordInfo.getRedFlowerCount());
        wordInfoDto.setExtra(wordInfo.getExtra());
        wordInfoDto.setType(ActManagerType.getByCode(wordInfo.getType()));
        wordInfoDto.setOwner(OwnerType.getByCode(wordInfo.getOwner()));
        return wordInfoDto;
    }


    private static void setTableHeaderCell(XWPFTableCell cell, String text) {
        // 清除单元格内容
        cell.removeParagraph(0);

        // 创建新段落并设置居中对齐
        XWPFParagraph paragraph = cell.addParagraph();
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        paragraph.setVerticalAlignment(TextAlignment.CENTER);

        // 添加文本
        XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setBold(true);

        // 设置单元格背景色
        cell.setColor("D3D3D3");

        // 垂直居中对齐
        cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
    }

    private static void setTableCell(XWPFTableCell cell, String text) {
        cell.setText(text);

        // 设置单元格垂直居中对齐
        cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

        // 设置左对齐
        XWPFParagraph paragraph = cell.getParagraphs().get(0);
        paragraph.setAlignment(ParagraphAlignment.LEFT);
    }

    private static void mergeCellsVertically(XWPFTable table, int col, int startRow, int endRow) {
        for (int row = startRow; row <= endRow; row++) {
            XWPFTableCell cell = table.getRow(row).getCell(col);

            if (row == startRow) {
                // 第一个单元格设置合并开始
                cell.getCTTc().addNewTcPr().addNewVMerge().setVal(STMerge.RESTART);
            } else {
                // 后续单元格设置合并继续
                cell.getCTTc().addNewTcPr().addNewVMerge().setVal(STMerge.CONTINUE);
            }
        }
    }

    private static void setTableBorders(XWPFTable table) {
        CTTbl ttbl = table.getCTTbl();
        CTTblPr tblPr = ttbl.getTblPr();
        if (tblPr == null) {
            tblPr = ttbl.addNewTblPr();
        }

        CTTblBorders borders = tblPr.addNewTblBorders();

        // 设置所有边框
        borders.addNewBottom().setVal(STBorder.SINGLE);
        borders.addNewTop().setVal(STBorder.SINGLE);
        borders.addNewLeft().setVal(STBorder.SINGLE);
        borders.addNewRight().setVal(STBorder.SINGLE);
        borders.addNewInsideH().setVal(STBorder.SINGLE);
        borders.addNewInsideV().setVal(STBorder.SINGLE);
    }

}
