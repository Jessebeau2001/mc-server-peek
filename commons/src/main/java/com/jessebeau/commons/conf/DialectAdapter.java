package com.jessebeau.commons.conf;

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.jessebeau.commons.http.Dialect;

import java.io.IOException;

public class DialectAdapter extends TypeAdapter<Dialect> {
	@Override
	public void write(JsonWriter out, Dialect value) throws IOException {
		out.value(value.toString());
	}

	@Override
	public Dialect read(JsonReader in) throws IOException {
		var value = in.nextString().toLowerCase();
		return Dialect.fromString(value).orElseThrow(
				() -> new JsonParseException(String.format("Invalid dialect '%s'", value))
		);
	}
}
