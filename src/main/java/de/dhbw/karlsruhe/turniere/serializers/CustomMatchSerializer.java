package de.dhbw.karlsruhe.turniere.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import de.dhbw.karlsruhe.turniere.database.models.Match;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.util.Optional;

@JsonComponent
public class CustomMatchSerializer extends JsonSerializer<Match> {
    @Override
    public void serialize(Match match, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("name1", match.getTeam1().getName());
        jsonGenerator.writeStringField("name2", match.getTeam2().getName());
        jsonGenerator.writeNumberField("score1", Optional.ofNullable(match.getScoreTeam1()).orElse(0));
        jsonGenerator.writeNumberField("score2", Optional.ofNullable(match.getScoreTeam2()).orElse(0));
        jsonGenerator.writeBooleanField("live", match.getState() == Match.State.IN_PROGRESS);
        jsonGenerator.writeEndObject();
    }
}
