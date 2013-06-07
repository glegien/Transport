package pl.edu.agh.cs.kraksim.main;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

class InputPanel extends JPanel {
	private static final long serialVersionUID = 3399884476951113192L;
	private JTextField textField = null;
	private JFileChooser fileChooser = null;

	public InputPanel(String inputName, String defaultValue, int initialSize,
			final JFileChooser fileChooser) {
		super();
		if (defaultValue == null) {
			defaultValue = "";
		}
		this.fileChooser = fileChooser;
		initLayout(inputName, defaultValue, initialSize);
	}

	private void initLayout(String inputName, String defaultValue,
			int initialSize) {
		this.setLayout(new FlowLayout(FlowLayout.RIGHT));

		textField = new JTextField(defaultValue, initialSize);
		JLabel label = new JLabel(inputName + ": ");
		label.setLabelFor(textField);

		add(label);
		add(textField);

		if (fileChooser != null) {
			JButton btn = new JButton("...");
			btn.setMargin(new Insets(0, 0, 0, 0));
			Dimension dim = new Dimension(30, 20);
			btn.setSize(dim);
			btn.setPreferredSize(dim);

			btn.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					int returnVal = fileChooser.showOpenDialog(InputPanel.this);

					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = fileChooser.getSelectedFile();
						textField.setText(file.getAbsolutePath());
					}
				}
			});

			add(btn);
		} else {
			JButton btn = new JButton("...");
			btn.setMargin(new Insets(0, 0, 0, 0));
			Dimension dim = new Dimension(30, 20);
			btn.setSize(dim);
			btn.setPreferredSize(dim);
			btn.setEnabled(false);

			add(btn);
		}

	}

	public String getText() {
		return textField.getText();
	}

	public String setText(String text) {
		String previous = getText();
		textField.setText(text);

		return previous;
	}

}
