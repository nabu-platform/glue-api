package be.nabu.glue.spi;

import java.util.List;
import java.util.ServiceLoader;

import be.nabu.glue.api.Parser;
import be.nabu.glue.api.ParserProvider;
import be.nabu.glue.api.ScriptRepository;

public class SPIParserProvider implements ParserProvider {

	private List<ParserProvider> providers;
	
	@Override
	public Parser newParser(ScriptRepository scriptRepository, String name) {
		for (ParserProvider provider : providers) {
			Parser parser = provider.newParser(scriptRepository, name);
			if (parser != null) {
				return parser;
			}
		}
		return null;
	}

	public List<ParserProvider> getProviders() {
		if (providers == null) {
			for (ParserProvider provider : ServiceLoader.load(ParserProvider.class)) {
				providers.add(provider);
			}
		}
		return providers;
	}
}
