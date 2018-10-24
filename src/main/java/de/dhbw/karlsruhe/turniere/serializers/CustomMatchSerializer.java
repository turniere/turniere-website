package de.dhbw.karlsruhe.turniere.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import de.dhbw.karlsruhe.turniere.database.models.Match;
import de.dhbw.karlsruhe.turniere.database.models.Team;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.util.Optional;

@JsonComponent
public class CustomMatchSerializer extends JsonSerializer<Match> {
    @Override
    public void serialize(Match match, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        Optional<Team> optionalTeam1 = Optional.ofNullable(match.getTeam1());
        String name1 = null;
        if (optionalTeam1.isPresent()){
            name1 = optionalTeam1.get().getName();
        }
        Optional<Team> optionalTeam2 = Optional.ofNullable(match.getTeam2());
        String name2 = null;
        if (optionalTeam2.isPresent()){
            name2 = optionalTeam2.get().getName();
        }

        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("name1", name1);
        jsonGenerator.writeStringField("name2", name2);
        jsonGenerator.writeNumberField("score1", Optional.ofNullable(match.getScoreTeam1()).orElse(0));
        jsonGenerator.writeNumberField("score2", Optional.ofNullable(match.getScoreTeam2()).orElse(0));
        jsonGenerator.writeBooleanField("live", match.getState() == Match.State.IN_PROGRESS);
        jsonGenerator.writeBooleanField("isgroupmatch", match.getIsGroupMatch());
        jsonGenerator.writeNumberField("id", match.getId());
        jsonGenerator.writeNumberField("stageid", match.getStageId());
        jsonGenerator.writeStringField("status", match.getState().toString());
        jsonGenerator.writeNumberField("groupid", match.getPosition());
        jsonGenerator.writeEndObject();
    }
}
