package de.dfki.asr.ajan.pluginsystem.majanplugin.extensions.evaluation;

/**
 * Summarizer interface.
 * Required because Java does not support function pointers.
 * @author djh.shih
*/
public interface Summarizer {
	Double summarize(Double[] a);
}
