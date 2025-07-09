package com.jessebeau.commons.api;

import com.jessebeau.commons.data.Serializer;

import java.io.OutputStream;

@FunctionalInterface
public interface ResponseWriterFactory {
	ResponseWriter create(OutputStream out, Serializer<Response> serializer);
}
