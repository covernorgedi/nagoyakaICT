package jp.nagoyakanet.ict.scm;

import java.lang.reflect.Type;
import java.util.List;
import org.kyojo.gson.JsonDeserializationContext;
import org.kyojo.gson.JsonDeserializer;
import org.kyojo.gson.JsonElement;
import org.kyojo.gson.JsonParseException;
import org.kyojo.gson.reflect.TypeToken;

public class ListDeserializer<T> implements JsonDeserializer<List<T>> {

	public List<T> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context)
			throws JsonParseException {
		// ParameterizedType gType = (ParameterizedType)type;
		// Type[] aTypes = gType.getActualTypeArguments();
		// Type listType = TypeToken.getParameterized(ArrayList.class, (Class<?>)aTypes[0]).getType();
		// List<?> list = context.deserialize(jsonElement, listType);
		// return list;

		Type listType = new TypeToken<List<T>>(){}.getType();
		return context.deserialize(jsonElement, listType);
	}

}
