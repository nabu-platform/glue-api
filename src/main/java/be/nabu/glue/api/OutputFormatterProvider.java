package be.nabu.glue.api;

public interface OutputFormatterProvider {
	public OutputFormatter newFormatter(OutputFormatter parent);
}
