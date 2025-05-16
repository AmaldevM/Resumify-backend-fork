package com.example.ResumeParser.Service;

import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FontAwarePDFStripper extends PDFTextStripper {

    private final Map<String, Float> fontSizeMap = new LinkedHashMap<>();

    public FontAwarePDFStripper() throws IOException {
        super.setSortByPosition(true);
    }

    @Override
    protected void writeString(String string, List<TextPosition> textPositions) throws IOException {
        if (!string.trim().isEmpty()) {
            float maxFontSize = 0f;
            for (TextPosition text : textPositions) {
                maxFontSize = Math.max(maxFontSize, text.getFontSizeInPt());
            }
            fontSizeMap.put(string.trim(), maxFontSize);
        }
        super.writeString(string, textPositions);
    }

    public String extractLargestText() {
        return fontSizeMap.entrySet().stream()
                .sorted((e1, e2) -> Float.compare(e2.getValue(), e1.getValue()))
                .map(Map.Entry::getKey)
                .filter(line -> line.matches("[A-Za-z ]{3,40}"))
                .findFirst()
                .orElse("Name not found");
    }
}
