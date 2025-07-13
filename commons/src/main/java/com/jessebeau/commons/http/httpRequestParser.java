package com.jessebeau.commons.http;

import com.jessebeau.commons.api.Request;
import com.jessebeau.commons.function.Parser;

import java.io.*;

public final class httpRequestParser implements Parser<InputStream, Request> {
	@Override
	public Request parse(InputStream in) throws ParseException {
		try {
			var reader = new BufferedReader(new InputStreamReader(in));
			var builder = Request.builder();
			builder.request(reader.readLine());

			int contentLength = -1;
			for (String line; (line = reader.readLine()) != null && !line.isEmpty(); ) {
				var split = line.split(": ", 2);
				if (split.length != 2) System.out.println("could not parse: " + line);
				builder.header(split[0], split[1]);
				if ("Content-Length".equals(split[0])) {
					contentLength = Integer.parseInt(split[1]);
				}
			}

			if (contentLength != -1) {
				char[] buff = new char[contentLength];
				var bytesRead = reader.read(buff);
				builder.body(new String(buff, 0, bytesRead));
			}

			return builder.create();
		} catch (IOException e) {
			throw new ParseException(e);
		}
	}
}
