package me.mortaldev.JBMines.modules.reset;

import com.google.gson.*;

import java.lang.reflect.Type;

public class ResetTypeDeserializer implements JsonDeserializer<ResetType> {
  @Override
  public ResetType deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
    JsonObject jsonObject = json.getAsJsonObject();
    if (jsonObject.has("lengthOfTime")) {
      return context.deserialize(json, Timer.class);
    } else if (jsonObject.has("resetPercentage")) {
      return context.deserialize(json, Percent.class);
    } else if (jsonObject.has("timer") && jsonObject.has("percent")) {
      return context.deserialize(json, Combo.class);
    }

    throw new RuntimeException("Unexpected type of ResetType");
  }
}
