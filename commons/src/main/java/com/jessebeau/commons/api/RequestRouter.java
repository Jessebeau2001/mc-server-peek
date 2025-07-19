package com.jessebeau.commons.api;

import com.jessebeau.commons.function.Handler;
import com.jessebeau.commons.http.Method;

import java.util.EnumMap;
import java.util.HashMap;

public class RequestRouter implements Handler<Request, Response> {
	private final EnumMap<Method, HashMap<String, Handler<Request, Response>>> methods = new EnumMap<>(Method.class);

	public RequestRouter() {
		// Pre populate method map
		for (var method : Method.values()) {
			methods.put(method, new HashMap<>(0));
		}
	}

	public void register(Method method, String path, Handler<Request, Response> handler) {
		methods.get(method).put(path, handler);
	}

	@Override
	public void handle(Request req, Response res) {
		var delegate = methods.get(req.method()).get(req.path());
		if (delegate != null) {
			delegate.handle(req, res);
		}
	}
}
