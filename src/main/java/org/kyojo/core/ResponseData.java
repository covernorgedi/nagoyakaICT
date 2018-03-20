package org.kyojo.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

public class ResponseData implements Map<String, ReferenceStructure> {

	private HttpServletResponse response;

	private Map<String, ReferenceStructure> map = new HashMap<>();

	public ResponseData(HttpServletResponse response) {
		this.response = response;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public boolean containsKey(Object key) {
		String lwrKey = key.toString().toLowerCase();
		return map.containsKey(lwrKey);
	}

	@Override
	public boolean containsValue(Object val) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<Entry<String, ReferenceStructure>> entrySet() {
		return map.entrySet();
	}

	@Override
	public ReferenceStructure get(Object key) {
		String lwrKey = key.toString().toLowerCase();
		return map.get(lwrKey);
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public Set<String> keySet() {
		return map.keySet();
	}

	@Override
	public ReferenceStructure put(String key, ReferenceStructure val) {
		String lwrKey = key.toString().toLowerCase();
		return map.put(lwrKey, val);
	}

	public ReferenceStructure put(String key, String val) {
		String lwrKey = key.toString().toLowerCase();
		return map.put(lwrKey, new ReferenceStructure(val));
	}

	@Override
	public void putAll(Map<? extends String, ? extends ReferenceStructure> map) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ReferenceStructure remove(Object key) {
		String lwrKey = key.toString().toLowerCase();
		return map.remove(lwrKey);
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public Collection<ReferenceStructure> values() {
		return map.values();
	}

}
