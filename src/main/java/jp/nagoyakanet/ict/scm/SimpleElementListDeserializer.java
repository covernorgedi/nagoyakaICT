package jp.nagoyakanet.ict.scm;

import java.lang.reflect.Type;
import java.util.List;
import org.kyojo.gson.JsonDeserializationContext;
import org.kyojo.gson.JsonDeserializer;
import org.kyojo.gson.JsonElement;
import org.kyojo.gson.JsonParseException;

public class SimpleElementListDeserializer implements JsonDeserializer<List<SimpleElement>> {

	@Override
	public List<SimpleElement> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context)
			throws JsonParseException {
		if(!jsonElement.isJsonArray()) {
			return null;
		}

		List<SimpleElement> list = null;

		return list;
	}

}
