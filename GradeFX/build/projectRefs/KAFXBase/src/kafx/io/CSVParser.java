package kafx.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class CSVParser<T> {

	private final File file;
	private final Function<String[], T> mapper;
	private char separator = ',';
	private char quotationMark = '"';
	private boolean containsLabel = false;

	public static char[] defaultSeparators() {
		return new char[] { ',', ';', ' ', '\t', ':' };
	}

	public CSVParser(Function<String[], T> mapper, File file) throws IOException {
		this(mapper, file, ',');
		this.guessSeparator();
	}

	public CSVParser(Function<String[], T> mapper, File file, char separator) {
		this.file = file;
		this.separator = separator;
		this.mapper = mapper;
	}

	public List<T> preview(int lines) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		List<T> preview = reader.lines().skip(this.containsLabel ? 1 : 0).limit(lines)
				.map(string -> split(string, this.separator, this.quotationMark)).map(mapper).filter(t -> t != null)
				.toList();
		reader.close();
		return preview;
	}

	public Stream<T> map() throws IOException {
		@SuppressWarnings("resource")
		BufferedReader reader = new BufferedReader(new FileReader(file));
		return reader.lines().skip(this.containsLabel ? 1 : 0)
				.map(string -> split(string, this.separator, this.quotationMark)).map(mapper).filter(t -> t != null)
				.onClose(() -> {
					try {
						reader.close();
					} catch (IOException e) {
						// FIXME new AlertException(e);
						e.printStackTrace();
					}
				});
	}

	public void guessSeparator() throws IOException {
		this.separator = this.guessSeparatorIntern();
	}

	public char getSeparator() {
		return this.separator;
	}

	public void setSeparator(char separator) {
		this.separator = separator;
	}

	public char getQuotationMark() {
		return this.quotationMark;
	}

	public void setQuotationMark(char quotationMark) {
		this.quotationMark = quotationMark;
	}

	public boolean containsLabel() {
		return this.containsLabel;
	}

	public void setContainsLabel(boolean containsLabel) {
		this.containsLabel = containsLabel;
	}

	/**
	 * Checks if any of the provided labels is contained in the first line.
	 * 
	 * @param labels
	 * @throws IOException
	 */
	public void checkContainsLabels(String... labels) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		reader.lines().findFirst().ifPresent(line -> {
			for (String label : labels) {
				if (line.contains(label)) {
					this.containsLabel = true;
					return;
				}
			}
			this.containsLabel = false;
		});
		reader.close();
	}

	private char guessSeparatorIntern() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		int lines = 10;
		char[] guesses = defaultSeparators();
		int[] amounts = new int[guesses.length];
		reader.lines().limit(lines).forEach(string -> {
			for (int i = 0; i < guesses.length; i++) {
				int amount = split(string, guesses[i], this.quotationMark).length - 1;
				if (amounts[i] == 0) {
					amounts[i] = amount;
				} else if (amounts[i] != amount) {
					amounts[i] = -1;
				}
			}
		});
		reader.close();
		for (int i = 0; i < guesses.length; i++) {
			if (amounts[i] > 0) {
				return guesses[i];
			}
		}
		return ',';
	}

	private static String[] split(String string, char separator, char quotationMark) {
		boolean quote = false;
		char[] chars = string.toCharArray();
		ArrayList<String> splits = new ArrayList<>();
		int prev = 0;
		for (int i = 0; i < chars.length; i++) {
			// if there is a quotation mark, but not doubled
			if (chars[i] == quotationMark && (i == chars.length - 1 || chars[i + 1] != quotationMark)
					&& (i == 0 || chars[i - 1] != quotationMark)) {
				quote = !quote;
			}
			// split at separator, but not inside quote
			if (chars[i] == separator && !quote) {
				splits.add(string.substring(prev, i));
				prev = i + 1;
			}
		}
		if (prev < string.length()) {
			splits.add(string.substring(prev));
		}
		return splits.toArray(n -> new String[n]);
	}

}
