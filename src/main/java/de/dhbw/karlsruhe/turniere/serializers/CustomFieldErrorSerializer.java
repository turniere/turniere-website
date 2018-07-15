package de.dhbw.karlsruhe.turniere.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.validation.FieldError;

import java.io.IOException;

@JsonComponent
public class CustomFieldErrorSerializer extends JsonSerializer<FieldError> {
    @Override
    public void serialize(FieldError fieldError, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("field", fieldError.getField());
        jsonGenerator.writeStringField("message", fieldError.getDefaultMessage());
        jsonGenerator.writeEndObject();
    }
}