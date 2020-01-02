package src;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;


public class Main {
	static List<double[]> WList;
	static List<String> atributes;
	static double[] tetas;
	public static final int VALUE_OF_LETTER_A = 65;
	public static final int VALUE_OF_LETTER_a = 97;

	public static void main(String[] args) {

		learn();
		SwingUtilities.invokeLater(
				()-> Frame()
			);
		
	}

	public static List<Path> findFiles(String dirName) {
		ArrayList<Path> PathList = new ArrayList<Path>();
		try {
			Stream<Path> stream = Files.walk(Paths.get(dirName));
			stream.filter(Files::isRegularFile).forEach(PathList::add);
			stream.close();
		} catch (Exception e) {
			System.err.println("Error while searching for data");
		}
		return PathList;
	}

	public static List<String> getAtributes() {
		File file = new File("LanguageFiles");
		String[] directories = file.list(new FilenameFilter() {
			@Override
			public boolean accept(File current, String name) {
				return new File(current, name).isDirectory();
			}
		});
		List<String> list = Arrays.asList(directories);
		return list;
	}

	public static void learn() {
		List<List<Path>> list = new ArrayList<List<Path>>();
		atributes = getAtributes();
		WList = new ArrayList<double[]>();

		List<Neuron> vectorsList = new ArrayList<Neuron>();

		for (String nameOfAtribute : atributes) {
			String dir = "LanguageFiles/" + nameOfAtribute;
			List<Path> listOfFoundedFiles = findFiles(dir);
			list.add(listOfFoundedFiles);
			double[] w = new double[26];

			for (int i = 0; i < w.length; i++)
				 w[i] = 1 - Math.random() * 3;
				//w[i] = 3;
			WList.add(w);
		}

		for (int i = 0; i < list.size(); i++) {
			for (int j = 0; j < list.get(i).size(); j++) {
				Scanner scanner;

				try {
					scanner = new Scanner(list.get(i).get(j));
					String allFile = "";
					while (scanner.hasNext()) {

						String line = scanner.nextLine();
						line = removeNonASCIIChar(line);
						line = line.replaceAll("[^\\x20-\\x7e]", "");
						allFile += line;
					}

					char[] array = allFile.toCharArray();

					double[] fileVector = new double[26];
					for (int x = 0; x < fileVector.length; x++) {
						fileVector[x] = 0;
					}
					
					int value_of_letter_A = 65;
					int value_of_letter_a = 97;

					int bigLetterToCheck = value_of_letter_A;
					int smallLetterToCheck = value_of_letter_a;
					for (int l = 0; l < 26; l++) {

						double counter = 0;

						for (int k = 0; k < array.length; k++) {
							if (array[k] == bigLetterToCheck || array[k] == smallLetterToCheck) {
								counter++;
							}
						}
						bigLetterToCheck++;
						smallLetterToCheck++;
						fileVector[l] = counter / allFile.length();

					}

					vectorsList.add(new Neuron(fileVector, atributes.get(i)));

				} catch (Exception e) {
				}
			}
		}

		// -----------------------------------------------------------------------------------
		double alfa = 0.2;
		tetas = new double[WList.size()];

		for (int i = 0; i < tetas.length; i++)
			tetas[i] = 0;

		for (int a = 0; a < WList.size(); a++) {
			for (int i = 0; i < 300; i++) {
				for (int j = 0; j < vectorsList.size(); j++) {

					double scalarProduct = 0;
					for (int k = 0; k < WList.get(a).length; k++) {
						scalarProduct += WList.get(a)[k] * vectorsList.get(j).vector[k];
					}

					double y;

					if (scalarProduct >= tetas[a]) {
						y = 1;
					} else {
						y = 0;
					}

					double d;

					if (vectorsList.get(j).attribute.equals(atributes.get(a))) {
						d = 1;
					} else {
						d = 0;
					}

					for (int k = 0; k < WList.get(a).length; k++) {
						WList.get(a)[k] = WList.get(a)[k] + ((d - y) * alfa * vectorsList.get(j).vector[k]);
					}

					tetas[a] += (y - d) * alfa;

				}
			}
		}

	}

	private static String removeNonASCIIChar(String str) {

		StringBuffer stringBuffer = new StringBuffer();
		char chars[] = str.toCharArray();

		for (int i = 0; i < chars.length; i++) {

			if ((64 < chars[i] && chars[i] < 91) || (96 < chars[i] && chars[1] < 123)) {

				stringBuffer.append(chars[i]);
			}

		}
		return stringBuffer.toString();
	}

	public static JFrame Frame() {
		JFrame frame = new JFrame();
		BorderLayout br = new BorderLayout();
		frame.setLayout(br);

		JTextArea area = new JTextArea();
		area.setPreferredSize(new Dimension(300, 400));
		frame.add(area, br.PAGE_START);
		// JScrollPane sp = new JScrollPane(area);

		JPanel jp = new JPanel();
		BorderLayout br2 = new BorderLayout();
		jp.setLayout(br2);
		frame.add(jp, br.PAGE_END);

		JButton button = new JButton("Identify");
		button.setPreferredSize(new Dimension(100, 50));
		jp.add(button, br2.PAGE_START);

		JLabel label = new JLabel();
		jp.add(label, br2.PAGE_END);

		button.addActionListener(e -> {

			String s = area.getText();
			s = removeNonASCIIChar(s);
			s = s.replaceAll("[^\\x20-\\x7e]", "");

			List<Double> nets = new ArrayList<Double>();

			char[] array = s.toCharArray();
			double[] fileVector = new double[26];

			int A = VALUE_OF_LETTER_A;
			int a = VALUE_OF_LETTER_a;

			for (int x = 0; x < fileVector.length; x++) {
				fileVector[x] = 0;
			}
			for (int l = 0; l < 26; l++) {
				double licznik = 0;

				for (int k = 0; k < array.length; k++) {
					if (array[k] == A || array[k] == a) {
						licznik++;
					}
				}
				A++;
				a++;
				fileVector[l] = licznik / s.length();

			}

			for (int r = 0; r < WList.size(); r++) {
				double scalarProduct = 0;
				for (int k = 0; k < WList.get(r).length; k++) {
					scalarProduct += WList.get(r)[k] * fileVector[k];
				}
				nets.add(scalarProduct);
			}

			List<Double> end = new ArrayList<Double>();

			for (int i = 0; i < WList.size(); i++) {
				if (nets.get(i) > tetas[i])
					end.add(nets.get(i));
			}

			double valueMaxFromList = Collections.max(end);
			Integer maxIdx = nets.indexOf(valueMaxFromList);

			String informationAboutRecognisedLanguage = "This is " + atributes.get(maxIdx) + " language.";

			label.setText(informationAboutRecognisedLanguage);
			area.setText("");
		});

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(400, 500));
		frame.setVisible(true);
		frame.pack();
		frame.setLocationRelativeTo(null);
		return frame;
	}
}