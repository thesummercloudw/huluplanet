package com.catplanet.common.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.List;

/**
 * 图片URL列表序列化器：将 List<String> 中的 /uploads/xxx 相对路径转为完整URL
 */
public class ImageUrlListSerializer extends JsonSerializer<List<String>> {

    @Override
    public void serialize(List<String> values, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (values == null) {
            gen.writeNull();
            return;
        }

        String baseUrl = ImageUrlSerializer.getBaseUrl();
        gen.writeStartArray();
        for (String value : values) {
            if (value == null || value.isEmpty()) {
                gen.writeNull();
            } else if (value.startsWith("http://") || value.startsWith("https://")) {
                gen.writeString(value);
            } else if (value.contains("/uploads/")) {
                String filename = value.substring(value.lastIndexOf("/uploads/") + "/uploads/".length());
                gen.writeString(baseUrl + "/api/public/image/" + filename);
            } else {
                gen.writeString(value);
            }
        }
        gen.writeEndArray();
    }
}
