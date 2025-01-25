package com.github.zlamb1.assignment1;

import com.github.zlamb1.io.MapFileSerializer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class AddressInfoSerializer extends MapFileSerializer<String, AddressInfo> {
    public AddressInfoSerializer(String outputFilePath) {
        super(outputFilePath);
    }

    @Override
    protected void serialize(PrintWriter writer, Map<String, AddressInfo> map) {
        final int[] count = {0};
        map.forEach((k, v) -> {
            writer.print(v.toCSVRow());
            if (count[0]++ != map.size() - 1) {
                writer.print("\n");
            }
        });
    }

    @Override
    protected Map<String, AddressInfo> deserialize(BufferedReader reader) throws IOException {
        HashMap<String, AddressInfo> addressInfoMap = new HashMap<>();
        String line;
        while ((line = reader.readLine()) != null) {
            AddressInfo info = AddressInfo.fromCSVRow(line);
            addressInfoMap.put(info.getFullName(), info);
        }
        return addressInfoMap;
    }
}
