package org.asciidoctor.internal;

import java.util.HashMap;
import java.util.Map;

import org.asciidoctor.Attributes;
import org.asciidoctor.Options;
import org.jruby.Ruby;

public class RubyGemsPreloader {

	private static final String CODERAY = "coderay";
	private static final String ERUBIS = "erubis";

	private static final Map<String, String> optionToRequiredGem = new HashMap<String, String>() {
		{
			put(Attributes.SOURCE_HIGHLIGHTER, "require 'coderay'");
			put(Options.ERUBY, "require 'erubis'");
			put(Options.TEMPLATE_DIRS, "require 'tilt'");
			put(Attributes.DATA_URI, "require 'base64'");
		}
	};

	private Ruby rubyRuntime;

	public RubyGemsPreloader(Ruby rubyRuntime) {
		this.rubyRuntime = rubyRuntime;
	}

	public void preloadRequiredLibraries(Map<String, Object> options) {

		if (options.containsKey(Options.ATTRIBUTES)) {
			Map<String, Object> attributes = (Map<String, Object>) options.get(Options.ATTRIBUTES);

			if (isOptionSet(attributes, Attributes.SOURCE_HIGHLIGHTER)
					&& isOptionWithValue(attributes, Attributes.SOURCE_HIGHLIGHTER, CODERAY)) {
				preloadLibrary(Attributes.SOURCE_HIGHLIGHTER);
			}

			if (isOptionSet(attributes, Attributes.DATA_URI)) {
				preloadLibrary(Attributes.DATA_URI);
			}
		}
		
		if (isOptionSet(options, Options.ERUBY) && isOptionWithValue(options, Options.ERUBY, ERUBIS)) {
			preloadLibrary(Options.ERUBY);
		}

		if (isOptionSet(options, Options.TEMPLATE_DIRS)) {
			preloadLibrary(Options.TEMPLATE_DIRS);
		}

	}

	private void preloadLibrary(String option) {
		this.rubyRuntime.evalScriptlet(optionToRequiredGem.get(option));
	}

	private boolean isOptionWithValue(Map<String, Object> attributes, String attribute, String value) {
		return attributes.get(attribute).equals(value);
	}

	private boolean isOptionSet(Map<String, Object> attributes, String attribute) {
		return attributes.containsKey(attribute);
	}

}
