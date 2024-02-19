package am.ik.retrofacto.tsid;

import java.io.IOException;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import io.hypersistence.tsid.TSID;

import org.springframework.boot.jackson.JsonComponent;

@JsonComponent
public class TsidJsonDeserializer extends JsonDeserializer<TSID> {

	@Override
	public TSID deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
		return TSID.from(jsonParser.getText());
	}

}