package ru.redrise.marinesco.library;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
//@Entity
@Data
public class InpFile {

    private final String name;
    private final List<InpEntry> inpEntries;

    public InpFile(byte[] content, String name) throws Exception{
        this.name = name.substring(0, name.lastIndexOf('.'));
        this.inpEntries = new ArrayList<>();
        log.info("FILE RELATED "+this.name);
        parseContent(content);
    }

    private void parseContent(byte[] content) throws Exception{
        int lastIndex = 0;
        for (int i = 0; i < content.length; i++){
            if (content[i] == '\n'){
                byte[] line = new byte[i-lastIndex];
                System.arraycopy(content, lastIndex, line, 0, i-lastIndex-1);
                inpEntries.add(new InpEntry(line));
                //RainbowDump.hexDumpUTF8(line);
                
                if (isNextCarriageReturn(i, content)){
                    i += 2;
                    lastIndex = i;
                }
                else
                    lastIndex = ++i;
            }
        }
    }
    private boolean isNextCarriageReturn(int i, byte[] content) {
        return i + 1 < content.length && (content[i + 1] == '\r');
    }
}
