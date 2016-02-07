package be.nabu.glue.impl;

import junit.framework.TestCase;

public class ImperativeSubstitutorTest extends TestCase {
	public void testTranslation() {
		ImperativeSubstitutor sub = new ImperativeSubstitutor("%", "translate('${value}')");
		assertEquals(
			"Testing ${translate('{{nested brackets}} here')}", 
			sub.substitute("Testing %{{{nested brackets}} here}", null, true)
		);
		assertEquals(
			"Testing ${translate('multiple {{nested}}')} ${translate('{{brackets}}')}",
			sub.substitute("Testing %{multiple {{nested}}} %{{{brackets}}}", null, true)
		);
	}
}
