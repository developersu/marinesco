package ru.redrise.marinesco.library;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InpFileScanner {
    private String name;

    public InpFileScanner(byte[] content, String name) throws Exception{
        this.name = name.substring(0, name.lastIndexOf('.'));
        log.info("FILE RELATED "+this.name);
        parseContent(content);
    }

    private void parseContent(byte[] content) throws Exception{
        int lastIndex = 0;
        for (int i = 0; i < content.length; i++){
            if (content[i] == '\n'){
                byte[] line = new byte[i-lastIndex];
                System.arraycopy(content, lastIndex, line, 0, i-lastIndex-1);
                new InpEntry(line, name);
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
