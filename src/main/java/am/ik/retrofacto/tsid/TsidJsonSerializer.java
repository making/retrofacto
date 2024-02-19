package am.ik.retrofacto.tsid;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.hypersistence.tsid.TSID;

import org.springframework.boot.jackson.JsonComponent;

@JsonComponent
public class TsidJsonSerializer extends JsonSerializer<TSID> {

	@Override
	public void serialize(TSID tsid, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
			throws IOException {
		jsonGenerator.writeString(tsid.toString());
	}

}