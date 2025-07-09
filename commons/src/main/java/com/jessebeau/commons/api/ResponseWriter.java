package com.jessebeau.commons.api;

import java.io.IOException;

public interface ResponseWriter {
	void write(Response response) throws IOException;
}
